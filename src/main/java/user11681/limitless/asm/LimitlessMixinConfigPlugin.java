package user11681.limitless.asm;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
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
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
        final String creativeModeFieldName = LimitlessTransformer.MAPPING_RESOLVER.mapFieldName("intermediary", "net.minecraft.class_1656", "field_7477", "Z");

        if (mixinClassName.endsWith("AnvilScreenHandlerDummyMixin")) {
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
        } else if (mixinClassName.endsWith("AnvilScreenDummyMixin")) {
            final String drawForeground = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", "net.minecraft.class_465", "method_2388", "(Lnet/minecraft/class_4587;II)V");
            final List<MethodNode> methods = targetClass.methods;
            final int methodCount = methods.size();

            for (int i = methodCount - 1; i >= 0; i--) {
                if (drawForeground.equals(methods.get(i).name)) {
                    final ListIterator<AbstractInsnNode> iterator = methods.get(i).instructions.iterator();

                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(creativeModeFieldName),
                        () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.FRAME)
                    );
                }
            }
        }
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
        if (mixinClassName.endsWith("EnchantmentHelperDummyMixin")) {
            final MethodNode[] methods = targetClass.methods.toArray(new MethodNode[0]);
            final int methodCount = methods.length;
            final String enchantmentHelper = "net.minecraft.class_1890";
            final String calculateRequiredExperienceLevel = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", enchantmentHelper, "method_8227", "(Ljava/util/Random;IILnet/minecraft/class_1799;)I");
            final String getPossibleEntries = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", enchantmentHelper, "method_8229", "(ILnet/minecraft/class_1799;Z)Ljava/util/List;");
            final String generateEnchantments = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", enchantmentHelper, "method_8230", "(Ljava/util/Random;Lnet/minecraft/class_1799;IZ)Ljava/util/List;");
            final String getMinLevel = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", LimitlessTransformer.ENCHANTMENT_CLASS_NAME, "method_8187", "()I");

            for (int i = methodCount - 1; i >= 0; i--) {
                final MethodNode method = methods[i];

                if (getPossibleEntries.equals(method.name)) {
                    final InsnList instructions = method.instructions;
                    final ListIterator<AbstractInsnNode> iterator = instructions.iterator();

                    // replace the getMaxLevel invocation with a getMinLevel invocation
                    // add a variable containing the last suitable level
                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(LimitlessTransformer.GET_MAX_LEVEL_METHOD_NAME),
                        (final AbstractInsnNode instruction) -> {
//                            ((MethodInsnNode) instruction).name = getMinLevel;
//
//                            iterator.next();
//                            iterator.add(new LdcInsnNode(Integer.MIN_VALUE));
//                            iterator.add(new VarInsnNode(ISTORE, 9));
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
                                "(IL" + LimitlessTransformer.REMAPPED_ENCHANTMENT_CLASS_NAME + ";Ljava/util/List;)V",
                                false
                            );

                            instructions.insert(iterator.previous(), insertion);
                        }
                    );
/*

                    // replace the getMinLevel invocation with a getMaxLevel invocation
                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMinLevel),
                        (final AbstractInsnNode instruction) -> {
                            ((MethodInsnNode) instruction).name = LimitlessTransformer.GET_MAX_LEVEL_METHOD_NAME;
                        }
                    );

                    // remove substraction of 1 after the getMaxValue call
                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == ICONST_1,
                        () -> {
                            iterator.remove();
                            iterator.next();
                            iterator.remove();
                            ((JumpInsnNode) iterator.next()).setOpcode(IF_ICMPGT);
                        }
                    );

                    // store icmplt for when L19 is encountered to replace the jump's label
                    final JumpInsnNode icmplt = Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == IF_ICMPLT,
                        JumpInsnNode.class::cast
                    );

                    // invert if_icmpgt to if_icmple
                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == IF_ICMPGT,
                        (final AbstractInsnNode instruction) -> {
                            final JumpInsnNode jumpInstruction = (JumpInsnNode) instruction;

                            jumpInstruction.setOpcode(IF_ICMPLE);

                            final LabelNode label = jumpInstruction.label;

                            instructions.insert(label, new VarInsnNode(ISTORE, 9));
                            instructions.insert(label, new InsnNode(ICONST_1));
                        }
                    );

                    // go to L19
                    // redirect if_icmplt to L19 (add to list) instead of L18 (next iteration)
                    final LabelNode L19 = Shortcode.findForward(iterator,
                        LabelNode.class::isInstance,
                        (final AbstractInsnNode instruction) -> {
                            icmplt.label = (LabelNode) instruction;

                            return (LabelNode) instruction;
                        }
                    );

                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == ILOAD,
                        (final AbstractInsnNode instruction) -> ((VarInsnNode) instruction).var = 9
                    );

                    Shortcode.findForward(iterator,
                        JumpInsnNode.class::isInstance,
                        (final AbstractInsnNode instruction) -> {
                            instructions.insert(L19, new JumpInsnNode(IF_ICMPEQ, ((JumpInsnNode) instruction).label));
                            instructions.insert(L19, new LdcInsnNode(Integer.MIN_VALUE));
                            instructions.insert(L19, new VarInsnNode(ILOAD, 9));
                        }
                    );

                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getOpcode() == IINC,
                        (final AbstractInsnNode instruction) -> ((IincInsnNode) instruction).incr = 1
                    );
*/
                } else if (calculateRequiredExperienceLevel.equals(method.name)) {
                    final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                    Shortcode.findForward(iterator,
                        (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FRAME,
                        () -> {
                            iterator.remove();

                            Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.FRAME);
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
    }
}
