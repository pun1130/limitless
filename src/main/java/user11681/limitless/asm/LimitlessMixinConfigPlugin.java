package user11681.limitless.asm;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import user11681.shortcode.Shortcode;
import user11681.shortcode.instruction.DelegatingInsnList;

public class LimitlessMixinConfigPlugin implements IMixinConfigPlugin, Opcodes {
    @Override
    public void onLoad(final String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {}

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
        switch (mixinClassName) {
            case "user11681.limitless.asm.mixin.enchantment.dummy.AnvilScreenDummyMixin":
                transformAnvilScreen(targetClass, LimitlessTransformer.MAPPING_RESOLVER.mapFieldName("intermediary", "net.minecraft.class_1656", "field_7477", "Z")); break;
            case "user11681.limitless.asm.mixin.enchantment.dummy.AnvilScreenHandlerDummyMixin":
                transformAnvilScreenHandler(targetClass, LimitlessTransformer.MAPPING_RESOLVER.mapFieldName("intermediary", "net.minecraft.class_1656", "field_7477", "Z")); break;
            case "user11681.limitless.asm.mixin.enchantment.EnchantmentHelperMixin":
                transformEnchantmentHelper(targetClass); break;
            case "user11681.limitless.asm.mixin.enchantment.EnchantmentScreenHandlerMixin":
                transformEnchantmentScreenHandler(targetClass); break;
        }
    }

    private static void transformAnvilScreen(final ClassNode targetClass, final String creativeModeFieldName) {
        final String drawForeground = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", "net.minecraft.class_465", "method_2388", "(Lnet/minecraft/class_4587;II)V");
        final List<MethodNode> methods = targetClass.methods;
        final int methodCount = methods.size();

        for (int i = methodCount - 1; i >= 0; --i) {
            if (drawForeground.equals(methods.get(i).name)) {
                final ListIterator<AbstractInsnNode> iterator = methods.get(i).instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(creativeModeFieldName),
                    () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.FRAME)
                );

                break;
            }
        }
    }

    private static void transformAnvilScreenHandler(final ClassNode targetClass, final String creativeModeFieldName) {
        final String getNextCost = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", "net.minecraft.class_1706", "method_20398", "(I)I");
        final String updateResult = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", "net.minecraft.class_4861", "method_24928", "()V");
        final List<MethodNode> methods = targetClass.methods;
        final int methodCount = methods.size();

        for (int i = methodCount - 1; i >= 0; i--) {
            final MethodNode method = methods.get(i);

            if (getNextCost.equals(method.name)) {
                method.instructions.clear();
                method.visitVarInsn(Opcodes.ILOAD, 0);
                method.visitInsn(Opcodes.IRETURN);
            } else if (updateResult.equals(method.name)) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(creativeModeFieldName),
                    () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.LINE)
                );

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(creativeModeFieldName),
                    () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.FRAME)
                );
            }
        }
    }

    private static void transformEnchantmentHelper(final ClassNode targetClass) {
        final MethodNode[] methods = targetClass.methods.toArray(new MethodNode[0]);
        final int methodCount = methods.length;
        final String enchantmentHelper = "net.minecraft.class_1890";
        final String calculateRequiredExperienceLevel = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", enchantmentHelper, "method_8227", "(Ljava/util/Random;IILnet/minecraft/class_1799;)I");
        final String getPossibleEntries = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", enchantmentHelper, "method_8229", "(ILnet/minecraft/class_1799;Z)Ljava/util/List;");
        final String generateEnchantments = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", enchantmentHelper, "method_8230", "(Ljava/util/Random;Lnet/minecraft/class_1799;IZ)Ljava/util/List;");

        for (int i = methodCount - 1; i >= 0; i--) {
            final MethodNode method = methods[i];

            if (getPossibleEntries.equals(method.name)) {
                final InsnList instructions = method.instructions;
                final ListIterator<AbstractInsnNode> iterator = instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(LimitlessTransformer.GET_MAX_LEVEL_METHOD_NAME),
                    (final AbstractInsnNode instruction) -> {
                        Shortcode.removeBetweenInclusive(iterator, AbstractInsnNode.LINE, AbstractInsnNode.IINC_INSN);

                        iterator.next();
                        iterator.remove();

                        final DelegatingInsnList insertion = new DelegatingInsnList();
                        insertion.addVarInsn(ILOAD, 0);
                        insertion.addVarInsn(ALOAD, 7);
                        insertion.addVarInsn(ALOAD, 3);
                        insertion.addMethodInsn(
                            INVOKESTATIC,
                            targetClass.name,
                            "limitless_getHighestSuitableLevel",
                            "(IL" + LimitlessTransformer.REMAPPED_INTERNAL_ENCHANTMENT_CLASS_NAME + ";Ljava/util/List;)V",
                            false
                        );

                        instructions.insert(iterator.previous(), insertion);
                    }
                );
            } else if (generateEnchantments.equals(method.name)) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.INT_INSN && ((IntInsnNode) instruction).operand == 50,
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
        }
    }

    private static void transformEnchantmentScreenHandler(final ClassNode targetClass) {
        final List<MethodNode> methods = targetClass.methods;
        final int methodCount = methods.size();

        for (int i = methodCount - 1; i >= 0; --i) {
            if ("method_17411".equals(methods.get(i).name)) {
                final MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
                final String mappedWorldDescriptor = Shortcode.toDescriptor(resolver.mapClassName("intermediary", "net.minecraft.class_1937"));
                final String mappedBlockPosDescriptor = Shortcode.toDescriptor(resolver.mapClassName("intermediary", "net.minecraft.class_2338"));
                final InsnList instructions = methods.get(i).instructions;
                final ListIterator<AbstractInsnNode> iterator = instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getOpcode() == ICONST_0,
                    (final AbstractInsnNode instruction) -> {
                        ((VarInsnNode) instruction.getNext()).setOpcode(FSTORE);
                        iterator.set(new MethodInsnNode(INVOKESTATIC, "user11681/limitless/tag/EnchantingBlocks", "countEnchantingPower", Shortcode.composeMethodDescriptor("F", mappedWorldDescriptor, mappedBlockPosDescriptor), true));
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
                    (final AbstractInsnNode instruction) -> instruction.getOpcode() == ILOAD && ((VarInsnNode) instruction).var == 4,
                    (final AbstractInsnNode instruction) -> ((VarInsnNode) instruction).setOpcode(FLOAD)
                );

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKESTATIC,
                    (final AbstractInsnNode instruction) -> {
                        final MethodInsnNode methodInstruction = (MethodInsnNode) instruction;

                        methodInstruction.owner = "user11681/limitless/tag/EnchantingBlocks";
                        methodInstruction.name = "calculateRequiredExperienceLevel";
                        methodInstruction.desc = methodInstruction.desc.replaceFirst("II", "IF");
                        methodInstruction.itf = true;
                    }
                );

                break;
            }
        }
    }
}
