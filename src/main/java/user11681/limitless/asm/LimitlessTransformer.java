package user11681.limitless.asm;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.ListIterator;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import user11681.fabricasmtools.plugin.TransformerPlugin;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.enchantment.EnchantmentConfiguration;
import user11681.limitless.enchantment.EnchantmentUtil;
import user11681.shortcode.Shortcode;
import user11681.shortcode.instruction.ExtendedInsnList;

public class LimitlessTransformer extends TransformerPlugin implements Opcodes {
    private static final String getInt_DESCRIPTOR = "(Ljava/lang/String;)I";
    private static final String putInt_DESCRIPTOR = "(Ljava/lang/String;I)V";

    private static final String Enchantment = internal(1887);
    private static final String CompoundTag = internal(2487);
    private static final String getMaxLevel = method(8183);
    private static final String getMaxPower = method(20742);
    private static final String getByte = method(10571);
    private static final String getShort = method(10568);
    private static final String getInt = method(10550);
    private static final String putByte = method(10567);
    private static final String putShort = method(10575);
    private static final String putInt = method(10569);

    private static final String limitless_getOriginalMaxLevel = "limitless_getOriginalMaxLevel";
    private static final String limitless_useGlobalMaxLevel = "limitless_useGlobalMaxLevel";
    private static final String limitless_maxLevel = "limitless_maxLevel";

    private static final ObjectOpenHashSet<String> enchantmentClassNames = new ObjectOpenHashSet<>(new String[]{Enchantment});

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);

        this.putClass("World", 1937);
        this.putClass("BlockPos", 2338);
        this.putField("creativeMode", 7477);
        this.putMethod("calculateRequiredExperienceLevel", 8227);

        this.registerPostMixinMethodTransformer(klass(3853, 1648), method(7246), null, LimitlessTransformer::transformEnchantBookFactoryCreate);
        this.registerPostMixinMethodTransformer(klass(1890), method(8229), null, LimitlessTransformer::transformEnchantmentHelperGetPossibleEntries);
        this.registerPostMixinMethodTransformer(klass(1890), method(8230), null, LimitlessTransformer::transformEnchantmentHelperGenerateEnchantments);
        this.registerPostMixinMethodTransformer(klass(1718), method(17411), null, this::transformEnchantmentScreenHandler);
    }

    private static void transformEnchantBookFactoryCreate(MethodNode method) {
        AbstractInsnNode instruction = method.instructions.getFirst();

        while (instruction != null) {
            if (instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel)) {
                ((MethodInsnNode) instruction).name = limitless_getOriginalMaxLevel;
            }

            instruction = instruction.getNext();
        }
    }

    private static void transformEnchantmentHelperGetPossibleEntries(MethodNode method) {
        InsnList instructions = method.instructions;
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel),
            (AbstractInsnNode instruction) -> {
                Shortcode.removeBetweenInclusive(iterator, AbstractInsnNode.LINE, AbstractInsnNode.IINC_INSN);

                iterator.next();
                iterator.remove();

                instructions.insert(iterator.previous(), new ExtendedInsnList()
                    .iload(0)
                    .aload(7)
                    .aload(3)
                    .invokestatic(
                        EnchantmentUtil.INTERNAL_NAME,
                        "getHighestSuitableLevel",
                        Shortcode.composeMethodDescriptor("V", "I", Enchantment, "java/util/List"),
                        true
                    )
                );
            }
        );
    }

    private static void transformEnchantmentHelperGenerateEnchantments(MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.INT_INSN && ((IntInsnNode) instruction).operand == 50,
            () -> {
                iterator.remove();

                iterator.add(new VarInsnNode(ILOAD, 2));
                iterator.add(new InsnNode(ICONST_2));
                iterator.add(new InsnNode(IDIV));
                iterator.add(new IntInsnNode(BIPUSH, 20));
                iterator.add(new InsnNode(IADD));
            }
        );
    }

    private void transformEnchantmentScreenHandler(MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == ICONST_0,
            (AbstractInsnNode instruction) -> {
                ((VarInsnNode) instruction.getNext()).setOpcode(FSTORE);
                iterator.set(new MethodInsnNode(
                    INVOKESTATIC,
                    "user11681/limitless/enchantment/EnchantingBlocks",
                    "countEnchantingPower",
                    Shortcode.composeMethodDescriptor("F", this.klass("World"), this.klass("BlockPos")),
                    true
                ));
                iterator.previous();
                iterator.add(new VarInsnNode(ALOAD, 2));
                iterator.add(new VarInsnNode(ALOAD, 3));
            }
        );

        iterator.next();
        iterator.next();

        int gotoCount = 0;

        while (gotoCount != 3) {
            final AbstractInsnNode next = iterator.next();

            if (next.getOpcode() == GOTO) {
                ++gotoCount;
            }

            iterator.remove();
        }

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == ILOAD && ((VarInsnNode) instruction).var == 4,
            (AbstractInsnNode instruction) -> ((VarInsnNode) instruction).setOpcode(FLOAD)
        );

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKESTATIC,
            (AbstractInsnNode instruction) -> {
                MethodInsnNode methodInstruction = (MethodInsnNode) instruction;

                methodInstruction.owner = "user11681/limitless/enchantment/EnchantingBlocks";
                methodInstruction.name = "calculateRequiredExperienceLevel";
                methodInstruction.desc = methodInstruction.desc.replaceFirst("II", "IF");
                methodInstruction.itf = true;
            }
        );
    }

    @SuppressWarnings("ForLoopReplaceableByForEach") // prevent ConcurrentModificationException from visitMethod
    public static boolean transform(ClassNode klass) {
        List<MethodNode> methods = klass.methods;
        boolean transformed = false;

        if (enchantmentClassNames.contains(klass.superName) || Enchantment.equals(klass.name)) {
            if (!Enchantment.equals(klass.name)) {
                enchantmentClassNames.add(klass.superName);
            }

            for (int i = 0, size = methods.size(); i < size; i++) {
                MethodNode method = methods.get(i);

                if (method.name.equals(getMaxLevel) && method.desc.equals("()I")) {
                    ((MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, getMaxLevel, method.desc, null, null)).instructions = new ExtendedInsnList()
                        .aload(0) // this
                        .getfield(klass.name, limitless_useGlobalMaxLevel, "Z") // I
                        .ifeq("custom")
                        .getstatic(LimitlessConfiguration.INTERNAL_NAME, "instance", LimitlessConfiguration.DESCRIPTOR) // LimitlessConfiguration
                        .getfield(LimitlessConfiguration.INTERNAL_NAME, "enchantment", EnchantmentConfiguration.DESCRIPTOR) // EnchantmentConfiguration
                        .getfield(EnchantmentConfiguration.INTERNAL_NAME, "globalMaxLevel", "I") // I
                        .ireturn()
                        .label("custom")
                        .aload(0) // this
                        .getfield(klass.name, limitless_maxLevel, "I") // I
                        .dup() // I I
                        .ldc(Integer.MIN_VALUE) // I I I
                        .if_icmpne("end") // I
                        .pop()
                        .aload(0) // this
                        .invokespecial(klass.name, limitless_getOriginalMaxLevel, method.desc) // I
                        .label("end")
                        .ireturn();

                    method.name = limitless_getOriginalMaxLevel;
                } else if (method.name.equals(getMaxPower) && method.desc.equals("(I)I")) {
                    method.name = "limitless_getOriginalMaxPower";

                    ((MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, getMaxPower, "(I)I", null, null)).instructions = new ExtendedInsnList()
                        .ldc(Integer.MAX_VALUE)
                        .ireturn();
                } else {
                    continue;
                }

                transformed = true;
            }
        }

        for (MethodNode method : methods) {
            AbstractInsnNode instruction = method.instructions.getFirst();

            while (instruction != null) {
                type:
                if (instruction.getType() == AbstractInsnNode.METHOD_INSN) {
                    // concern
//                    switch (instruction.getOpcode()) {
//                        case Opcodes.INVOKEVIRTUAL:
                    if (CompoundTag.equals(((MethodInsnNode) instruction).owner)) {
                        MethodInsnNode methodInstruction = (MethodInsnNode) instruction;

                        if (putShort.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2S) {
                            method.instructions.remove(methodInstruction.getPrevious());
                            methodInstruction.name = putInt;
                            methodInstruction.desc = putInt_DESCRIPTOR;

                            if (methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                                method.instructions.remove(methodInstruction.getPrevious());
                            }
                        } else if (getShort.equals(methodInstruction.name)) {
                            methodInstruction.name = getInt;
                            methodInstruction.desc = getInt_DESCRIPTOR;
                        } else if (putByte.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                            method.instructions.remove(methodInstruction.getPrevious());
                            methodInstruction.name = putInt;
                            methodInstruction.desc = putInt_DESCRIPTOR;
                        } else if (getByte.equals(methodInstruction.name)) {
                            methodInstruction.name = getInt;
                            methodInstruction.desc = getInt_DESCRIPTOR;
                        } else {
                            break type;
                        }

                        transformed = true;
                    }
//
//                            break;
//
//                        case Opcodes.INVOKESTATIC:
//                            if (CALCULATE_REQUIRED_EXPERIENCE_LEVEL_METHOD_NAME.equals(((MethodInsnNode) instruction).name)) {
//                                methodInstruction = (MethodInsnNode) instruction;
//
//                                if (REMAPPED_INTERNAL_ENCHANTMENT_HELPER_CLASS_NAME.equals(methodInstruction.owner)) {
//
//                                }
//                            }
//                    }
                }

                instruction = instruction.getNext();
            }
        }

        return transformed;
    }

    static {
        TransformerApi.registerPostMixinAsmClassTransformer(LimitlessTransformer::transform);
    }
}
