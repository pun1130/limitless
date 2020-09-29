package user11681.limitless.asm;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.shortcode.Shortcode;
import user11681.shortcode.instruction.DelegatingInsnList;

public class LimitlessTransformer {
    public static final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();

    public static final String ENCHANTMENT_CLASS_NAME = "net.minecraft.class_1887";
    public static final String COMPOUND_TAG_CLASS_NAME = "net.minecraft.class_2487";
    public static final String REMAPPED_INTERNAL_ENCHANTMENT_CLASS_NAME = mappingResolver.mapClassName("intermediary", ENCHANTMENT_CLASS_NAME).replace('.', '/');
    public static final String REMAPPED_INTERNAL_COMPOUND_TAG_CLASS_NAME = mappingResolver.mapClassName("intermediary", COMPOUND_TAG_CLASS_NAME).replace('.', '/');
    public static final String REMAPPED_INTERNAL_ENCHANTMENT_HELPER_CLASS_NAME = mappingResolver.mapClassName("intermediary", "class_1890");
    public static final String CALCULATE_REQUIRED_EXPERIENCE_LEVEL_METHOD_NAME = mappingResolver.mapMethodName("intermediary", REMAPPED_INTERNAL_ENCHANTMENT_HELPER_CLASS_NAME, "method_8227", "(Ljava/util/Random;IILnet/minecraft/class_1799;)I");
    public static final String GET_MAX_LEVEL_METHOD_NAME = mappingResolver.mapMethodName("intermediary", ENCHANTMENT_CLASS_NAME, "method_8183", "()I");
    public static final String GET_MAX_POWER_METHOD_NAME = mappingResolver.mapMethodName("intermediary", ENCHANTMENT_CLASS_NAME, "method_20742", "(I)I");
    public static final String GET_SHORT_METHOD_NAME = mappingResolver.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10568", "(Ljava/lang/String;)S");
    public static final String GET_INT_METHOD_DESCRIPTOR = "(Ljava/lang/String;)I";
    public static final String GET_INT_METHOD_NAME = mappingResolver.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10550", GET_INT_METHOD_DESCRIPTOR);
    public static final String GET_BYTE_METHOD_NAME = mappingResolver.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10571", "(Ljava/lang/String;)B");
    public static final String PUT_BYTE_METHOD_NAME = mappingResolver.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10567", "(Ljava/lang/String;B)V");
    public static final String PUT_SHORT_METHOD_NAME = LimitlessTransformer.mappingResolver.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10575", "(Ljava/lang/String;S)V");
    public static final String PUT_INT_METHOD_DESCRIPTOR = "(Ljava/lang/String;I)V";
    public static final String PUT_INT_METHOD_NAME = LimitlessTransformer.mappingResolver.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10569", PUT_INT_METHOD_DESCRIPTOR);

    public static final ObjectOpenHashSet<String> ENCHANTMENTS = new ObjectOpenHashSet<String>(32, 1) {{
        this.add(REMAPPED_INTERNAL_ENCHANTMENT_CLASS_NAME);
    }};

    private static void transform(final String name, final ClassNode klass) {
        final List<MethodNode> methods = klass.methods;
        final int methodCount = methods.size();
        AbstractInsnNode instruction;
        MethodInsnNode methodInstruction;
        MethodNode method;
        int i;

        if (ENCHANTMENTS.contains(klass.superName) || REMAPPED_INTERNAL_ENCHANTMENT_CLASS_NAME.equals(name)) {
            if (!REMAPPED_INTERNAL_ENCHANTMENT_CLASS_NAME.equals(name)) {
                ENCHANTMENTS.add(klass.superName);
            }

            for (i = 0; i != methodCount; i++) {
                if (GET_MAX_LEVEL_METHOD_NAME.equals((method = methods.get(i)).name) && method.desc.equals("()I")) {
                    final MethodNode newGetMaxLevel = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, GET_MAX_LEVEL_METHOD_NAME, "()I", null, null);
                    final Label getCustom = new Label();

                    newGetMaxLevel.visitVarInsn(Opcodes.ALOAD, 0);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, klass.name, "limitless_useGlobalMaxLevel", "Z");
                    newGetMaxLevel.visitJumpInsn(Opcodes.IFEQ, getCustom);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETSTATIC, LimitlessConfiguration.INTERNAL_NAME, "instance", LimitlessConfiguration.DESCRIPTOR);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, LimitlessConfiguration.INTERNAL_NAME, "globalMaxLevel", "I");
                    newGetMaxLevel.visitInsn(Opcodes.IRETURN);
                    newGetMaxLevel.visitLabel(getCustom);
                    newGetMaxLevel.visitVarInsn(Opcodes.ALOAD, 0);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, klass.name, "limitless_maxLevel", "I");
                    newGetMaxLevel.visitInsn(Opcodes.IRETURN);

                    method.name = "limitless_getOriginalMaxLevel";

                    DelegatingInsnList setField;
                    Label setOne;
                    InsnList instructions;

                    for (int j = 0; j != methodCount; j++) {
                        if ("<init>".equals(methods.get(j).name)) {
                            instructions = methods.get(j).instructions;
                            instruction = instructions.getFirst();

                            while (instruction != null) {
                                if (instruction.getOpcode() == Opcodes.RETURN) {
                                    setField = new DelegatingInsnList();
                                    setOne = new Label();

                                    setField.addVarInsn(Opcodes.ALOAD, 0); // this
                                    setField.addVarInsn(Opcodes.ALOAD, 0); // this this
                                    setField.addMethodInsn(Opcodes.INVOKEVIRTUAL, klass.name, "limitless_getOriginalMaxLevel", "()I", false); // this I
                                    setField.addInsn(Opcodes.ICONST_1); // this I I
                                    setField.addJumpInsn(Opcodes.IF_ICMPLE, setOne); // this
                                    setField.addLdcInsn(Integer.MAX_VALUE); // this I
                                    setField.addFieldInsn(Opcodes.PUTFIELD, klass.name, "limitless_maxLevel", "I");
                                    setField.addInsn(Opcodes.RETURN);
                                    setField.addLabel(setOne);
                                    setField.addInsn(Opcodes.ICONST_1); // this I
                                    setField.addFieldInsn(Opcodes.PUTFIELD, klass.name, "limitless_maxLevel", "I");

                                    instructions.insertBefore(instruction, Shortcode.copyInstructions(setField));
                                }

                                instruction = instruction.getNext();
                            }
                        }
                    }
                } else if (GET_MAX_POWER_METHOD_NAME.equals(methods.get(i).name)) {
                    methods.get(i).name = "limitless_getOriginalMaxPower";

                    final MethodNode newGetMaxPower = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, GET_MAX_POWER_METHOD_NAME, "(I)I", null, null);

                    newGetMaxPower.visitLdcInsn(Integer.MAX_VALUE);
                    newGetMaxPower.visitInsn(Opcodes.IRETURN);
                }
            }
        }

        for (i = 0; i < methodCount; i++) {
            instruction = methods.get(i).instructions.getFirst();

            while (instruction != null) {
                if (instruction.getType() == AbstractInsnNode.METHOD_INSN) {
//                    switch (instruction.getOpcode()) {
//                        case Opcodes.INVOKEVIRTUAL:
                            if (REMAPPED_INTERNAL_COMPOUND_TAG_CLASS_NAME.equals(((MethodInsnNode) instruction).owner)) {
                                methodInstruction = (MethodInsnNode) instruction;

                                if (PUT_SHORT_METHOD_NAME.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2S) {
                                    methods.get(i).instructions.remove(methodInstruction.getPrevious());
                                    methodInstruction.name = PUT_INT_METHOD_NAME;
                                    methodInstruction.desc = PUT_INT_METHOD_DESCRIPTOR;

                                    if (methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                                        methods.get(i).instructions.remove(methodInstruction.getPrevious());
                                    }
                                } else if (GET_SHORT_METHOD_NAME.equals(methodInstruction.name)) {
                                    methodInstruction.name = GET_INT_METHOD_NAME;
                                    methodInstruction.desc = GET_INT_METHOD_DESCRIPTOR;
                                } else if (PUT_BYTE_METHOD_NAME.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                                    methods.get(i).instructions.remove(methodInstruction.getPrevious());
                                    methodInstruction.name = PUT_INT_METHOD_NAME;
                                    methodInstruction.desc = PUT_INT_METHOD_DESCRIPTOR;
                                } else if (GET_BYTE_METHOD_NAME.equals(methodInstruction.name)) {
                                    methodInstruction.name = GET_INT_METHOD_NAME;
                                    methodInstruction.desc = GET_INT_METHOD_DESCRIPTOR;
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
        TransformerApi.registerPreMixinAsmClassTransformer(LimitlessTransformer::transform);
    }
}
