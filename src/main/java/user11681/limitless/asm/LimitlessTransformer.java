package user11681.limitless.asm;

import java.util.List;
import java.util.ListIterator;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import org.objectweb.asm.Label;
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
    private static final String getIntDescriptor = "(Ljava/lang/String;)I";
    private static final String putIntDescriptor = "(Ljava/lang/String;I)V";

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
    private static final String limitless_initialize = "limitless_initialize";
    private static final String limitless_maxLevel = "limitless_maxLevel";

    private static final ObjectOpenHashSet<String> enchantmentClassNames = new ObjectOpenHashSet<>(new String[]{Enchantment}, 0, 1, 1);

    @Override
    public void onLoad(final String mixinPackage) {
        super.onLoad(mixinPackage);

        this.putClass("World", 1937);
        this.putClass("BlockPos", 2338);
        this.putField("creativeMode", 7477);
        this.putMethod("create", 7246);
        this.putMethod("calculateRequiredExperienceLevel", 8227);
        this.putMethod("getPossibleEntries", 8229);
        this.putMethod("generateEnchantments", 8230);

        this.registerPostMixinMethodTransformer(klass(3853, 1648), this.method("create"), null, LimitlessTransformer::transformEnchantBookFactory);
        this.registerPostMixinMethodTransformer(klass(1890), this.method("getPossibleEntries"), null, LimitlessTransformer::transformEnchantmentHelperGetPossibleEntries);
        this.registerPostMixinMethodTransformer(klass(1890), this.method("generateEnchantments"), null, LimitlessTransformer::transformEnchantmentHelperGenerateEnchantments);
        this.registerPostMixinMethodTransformer(klass(1718), "method_17411", null, this::transformEnchantmentScreenHandler);
    }

    private static void transformEnchantBookFactory(final MethodNode method) {
        AbstractInsnNode instruction = method.instructions.getFirst();

        while (instruction != null) {
            if (instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel)) {
                ((MethodInsnNode) instruction).name = limitless_getOriginalMaxLevel;
            }

            instruction = instruction.getNext();
        }
    }

    private static void transformEnchantmentHelperGetPossibleEntries(final MethodNode method) {
        final InsnList instructions = method.instructions;
        final ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel),
            (AbstractInsnNode instruction) -> {
                Shortcode.removeBetweenInclusive(iterator, AbstractInsnNode.LINE, AbstractInsnNode.IINC_INSN);

                iterator.next();
                iterator.remove();

                ExtendedInsnList insertion = new ExtendedInsnList()
                    .iload(0)
                    .aload(7)
                    .aload(3)
                    .invokestatic(
                        EnchantmentUtil.INTERNAL_NAME,
                        "getHighestSuitableLevel",
                        Shortcode.composeMethodDescriptor("V", "I", Enchantment, "java/util/List")
                    );

                instructions.insert(iterator.previous(), insertion);
            }
        );
    }

    private static void transformEnchantmentHelperGenerateEnchantments(final MethodNode method) {
        final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

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

    private void transformEnchantmentScreenHandler(final MethodNode method) {
        final InsnList instructions = method.instructions;
        final ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        Shortcode.findForward(iterator,
            (final AbstractInsnNode instruction) -> instruction.getOpcode() == ICONST_0,
            (final AbstractInsnNode instruction) -> {
                ((VarInsnNode) instruction.getNext()).setOpcode(FSTORE);
                iterator.set(new MethodInsnNode(INVOKESTATIC, "user11681/limitless/enchantment/EnchantingBlocks", "countEnchantingPower", Shortcode.composeMethodDescriptor("F", this.klass("World"), this.klass("BlockPos")), true));
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

                methodInstruction.owner = "user11681/limitless/enchantment/EnchantingBlocks";
                methodInstruction.name = "calculateRequiredExperienceLevel";
                methodInstruction.desc = methodInstruction.desc.replaceFirst("II", "IF");
                methodInstruction.itf = true;
            }
        );
    }

    public static void transform(final ClassNode klass) {
        final List<MethodNode> methods = klass.methods;
        final int methodCount = methods.size();
        AbstractInsnNode instruction;
        MethodInsnNode methodInstruction;
        int i;

        if (enchantmentClassNames.contains(klass.superName) || Enchantment.equals(klass.name)) {
            if (!Enchantment.equals(klass.name)) {
                enchantmentClassNames.add(klass.superName);
            }

            for (i = 0; i < methodCount; i++) {
                final MethodNode method = methods.get(i);

                if (method.name.equals(getMaxLevel) && method.desc.equals("()I")) {
                    final MethodNode newGetMaxLevel = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, getMaxLevel, "()I", null, null);
                    final Label getCustom = new Label();

                    newGetMaxLevel.visitVarInsn(Opcodes.ALOAD, 0);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, klass.name, "limitless_useGlobalMaxLevel", "Z");
                    newGetMaxLevel.visitJumpInsn(Opcodes.IFEQ, getCustom);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETSTATIC, LimitlessConfiguration.INTERNAL_NAME, "instance", LimitlessConfiguration.DESCRIPTOR);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, LimitlessConfiguration.INTERNAL_NAME, "enchantment", EnchantmentConfiguration.DESCRIPTOR);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, EnchantmentConfiguration.INTERNAL_NAME, "globalMaxLevel", "I");
                    newGetMaxLevel.visitInsn(Opcodes.IRETURN);
                    newGetMaxLevel.visitLabel(getCustom);
                    newGetMaxLevel.visitVarInsn(Opcodes.ALOAD, 0);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, klass.name, limitless_maxLevel, "I");
                    newGetMaxLevel.visitInsn(Opcodes.IRETURN);

                    method.name = limitless_getOriginalMaxLevel;

                    for (int j = 0; j != methodCount; j++) {
                        if ("<init>".equals(methods.get(j).name)) {
                            InsnList instructions = methods.get(j).instructions;
                            instruction = instructions.getFirst();

                            while (instruction != null) {
                                if (instruction.getOpcode() == Opcodes.RETURN) {
                                    AbstractInsnNode lastInstruction = new InsnNode(Opcodes.RETURN);
                                    ExtendedInsnList setField = new ExtendedInsnList()
                                        .aload(0)
                                        .invokespecial(Enchantment, limitless_initialize, "()V")
                                        .append(lastInstruction);

                                    instructions.insertBefore(instruction, setField);
                                }

                                instruction = instruction.getNext();
                            }
                        }
                    }
                } else if (method.name.equals(getMaxPower) && method.desc.equals("(I)I")) {
                    method.name = "limitless_getOriginalMaxPower";

                    final MethodNode newGetMaxPower = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, getMaxPower, "(I)I", null, null);

                    newGetMaxPower.visitLdcInsn(Integer.MAX_VALUE);
                    newGetMaxPower.visitInsn(Opcodes.IRETURN);
                }
            }
        }

        for (final MethodNode method : methods) {
            instruction = method.instructions.getFirst();

            while (instruction != null) {
                if (instruction.getType() == AbstractInsnNode.METHOD_INSN) {
//                    switch (instruction.getOpcode()) {
//                        case Opcodes.INVOKEVIRTUAL:
                    if (CompoundTag.equals(((MethodInsnNode) instruction).owner)) {
                        methodInstruction = (MethodInsnNode) instruction;

                        if (putShort.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2S) {
                            method.instructions.remove(methodInstruction.getPrevious());
                            methodInstruction.name = putInt;
                            methodInstruction.desc = putIntDescriptor;

                            if (methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                                method.instructions.remove(methodInstruction.getPrevious());
                            }
                        } else if (getShort.equals(methodInstruction.name)) {
                            methodInstruction.name = getInt;
                            methodInstruction.desc = getIntDescriptor;
                        } else if (putByte.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                            method.instructions.remove(methodInstruction.getPrevious());
                            methodInstruction.name = putInt;
                            methodInstruction.desc = putIntDescriptor;
                        } else if (getByte.equals(methodInstruction.name)) {
                            methodInstruction.name = getInt;
                            methodInstruction.desc = getIntDescriptor;
                        }
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
    }

    static {
        TransformerApi.registerPostMixinAsmClassTransformer(LimitlessTransformer::transform);
    }
}
