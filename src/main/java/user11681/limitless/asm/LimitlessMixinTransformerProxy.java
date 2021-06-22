package user11681.limitless.asm;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.transformer.FabricMixinTransformerProxy;
import org.spongepowered.asm.transformers.MixinClassWriter;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.enchantment.EnchantmentConfiguration;
import user11681.reflect.Classes;
import user11681.shortcode.instruction.ExtendedInsnList;

public class LimitlessMixinTransformerProxy extends FabricMixinTransformerProxy {
    private static final ObjectOpenHashSet<String> enchantmentClassNames = new ObjectOpenHashSet<>(new String[]{LimitlessTransformer.Enchantment});

    @SuppressWarnings("ForLoopReplaceableByForEach") // prevent ConcurrentModificationException from visitMethod
    private static boolean transform(ClassNode klass) {
        List<MethodNode> methods = klass.methods;
        boolean transformed = false;

        if (enchantmentClassNames.contains(klass.superName) || LimitlessTransformer.Enchantment.equals(klass.name)) {
            if (!LimitlessTransformer.Enchantment.equals(klass.name)) {
                enchantmentClassNames.add(klass.superName);
            }

            for (int i = 0, size = methods.size(); i < size; i++) {
                MethodNode method = methods.get(i);

                if (method.name.equals(LimitlessTransformer.getMaxLevel) && method.desc.equals("()I")) {
                    ((MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, LimitlessTransformer.getMaxLevel, method.desc, null, null)).instructions = new ExtendedInsnList()
                        .aload(0) // this
                        .getfield(klass.name, LimitlessTransformer.limitless_useGlobalMaxLevel, "Z") // I
                        .ifeq("custom")
                        .getstatic(LimitlessConfiguration.INTERNAL_NAME, "instance", LimitlessConfiguration.DESCRIPTOR) // LimitlessConfiguration
                        .getfield(LimitlessConfiguration.INTERNAL_NAME, "enchantment", EnchantmentConfiguration.DESCRIPTOR) // EnchantmentConfiguration
                        .getfield(EnchantmentConfiguration.INTERNAL_NAME, "globalMaxLevel", "I") // I
                        .ireturn()
                        .label("custom")
                        .aload(0) // this
                        .getfield(klass.name, LimitlessTransformer.limitless_maxLevel, "I") // I
                        .dup() // I I
                        .ldc(Integer.MIN_VALUE) // I I I
                        .if_icmpne("end") // I
                        .pop()
                        .aload(0) // this
                        .invokespecial(klass.name, LimitlessTransformer.limitless_getOriginalMaxLevel, method.desc) // I
                        .label("end")
                        .ireturn();

                    method.name = LimitlessTransformer.limitless_getOriginalMaxLevel;
                } else if (method.name.equals(LimitlessTransformer.getMaxPower) && method.desc.equals("(I)I")) {
                    method.name = "limitless_getOriginalMaxPower";

                    ((MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, LimitlessTransformer.getMaxPower, "(I)I", null, null)).instructions = new ExtendedInsnList()
                        .ldc(Integer.MAX_VALUE)
                        .ireturn();
                } else {
                    continue;
                }

                transformed = true;
            }
        }

        return transformed;

    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        basicClass = super.transformClassBytes(name, transformedName, basicClass);

        if (basicClass == null) {
            return null;
        }

        ClassNode node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);

        if (transform(node)) {
            ClassWriter writer = new MixinClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);

            return writer.toByteArray();
        }

        return basicClass;
    }

    static {
        Classes.load(ClassNode.class.getName(), ClassReader.class.getName(), ClassWriter.class.getName());
    }
}
