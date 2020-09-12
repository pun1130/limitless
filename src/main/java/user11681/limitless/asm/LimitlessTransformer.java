package user11681.limitless.asm;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import user11681.shortcode.Shortcode;
import user11681.shortcode.instruction.DelegatingInsnList;

public class LimitlessTransformer {
    public static final MappingResolver MAPPING_RESOLVER = FabricLoader.getInstance().getMappingResolver();

    public static final String ENCHANTMENT_CLASS_NAME = "net.minecraft.class_1887";
    public static final String COMPOUND_TAG_CLASS_NAME = "net.minecraft.class_2487";
    public static final String REMAPPED_ENCHANTMENT_CLASS_NAME = MAPPING_RESOLVER.mapClassName("intermediary", ENCHANTMENT_CLASS_NAME).replace('.', '/');
    public static final String REMAPPED_COMPOUND_TAG_CLASS_NAME = MAPPING_RESOLVER.mapClassName("intermediary", COMPOUND_TAG_CLASS_NAME).replace('.', '/');
    public static final String GET_MAX_LEVEL_METHOD_NAME = MAPPING_RESOLVER.mapMethodName("intermediary", ENCHANTMENT_CLASS_NAME, "method_8183", "()I");
    public static final String GET_MAX_POWER_METHOD_NAME = MAPPING_RESOLVER.mapMethodName("intermediary", ENCHANTMENT_CLASS_NAME, "method_20742", "(I)I");
    public static final String GET_SHORT_METHOD_NAME = MAPPING_RESOLVER.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10568", "(Ljava/lang/String;)S");
    public static final String GET_INT_METHOD_DESCRIPTOR = "(Ljava/lang/String;)I";
    public static final String GET_INT_METHOD_NAME = MAPPING_RESOLVER.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10550", GET_INT_METHOD_DESCRIPTOR);
    public static final String GET_BYTE_METHOD_NAME = MAPPING_RESOLVER.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10571", "(Ljava/lang/String;)B");
    public static final String PUT_BYTE_METHOD_NAME = MAPPING_RESOLVER.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10567", "(Ljava/lang/String;B)V");
    public static final String PUT_SHORT_METHOD_NAME = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10575", "(Ljava/lang/String;S)V");
    public static final String PUT_INT_METHOD_DESCRIPTOR = "(Ljava/lang/String;I)V";
    public static final String PUT_INT_METHOD_NAME = LimitlessTransformer.MAPPING_RESOLVER.mapMethodName("intermediary", COMPOUND_TAG_CLASS_NAME, "method_10569", PUT_INT_METHOD_DESCRIPTOR);

    public static final ObjectOpenHashSet<String> ENCHANTMENTS = new ObjectOpenHashSet<String>(32, 1) {{
        this.add(REMAPPED_ENCHANTMENT_CLASS_NAME);
    }};

    private static void transform(final String name, final ClassNode klass) {
        final List<MethodNode> methods = klass.methods;
        final int methodCount = methods.size();
        AbstractInsnNode instruction;
        MethodInsnNode methodInstruction;
        MethodNode method;
        int i;

        if (ENCHANTMENTS.contains(klass.superName) || REMAPPED_ENCHANTMENT_CLASS_NAME.equals(name)) {
            if (!REMAPPED_ENCHANTMENT_CLASS_NAME.equals(name)) {
                ENCHANTMENTS.add(klass.superName);
            }


            for (i = 0; i != methodCount; i++) {
                if (GET_MAX_LEVEL_METHOD_NAME.equals((method = methods.get(i)).name) && method.desc.equals("()I")) {
                    final MethodNode newGetMaxLevel = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, GET_MAX_LEVEL_METHOD_NAME, "()I", null, null);

                    newGetMaxLevel.visitVarInsn(Opcodes.ALOAD, 0);
                    newGetMaxLevel.visitFieldInsn(Opcodes.GETFIELD, klass.name, "limitless_maxLevel", "I");
                    newGetMaxLevel.visitInsn(Opcodes.IRETURN);

                    method.name = "limitless_getOriginalMaxLevel";

                    final DelegatingInsnList setField = new DelegatingInsnList();

                    setField.addVarInsn(Opcodes.ALOAD, 0);

                    instruction = method.instructions.getFirst();

                    while (instruction != null) {
                        if (instruction.getOpcode() == Opcodes.ICONST_1 && instruction.getNext().getOpcode() == Opcodes.IRETURN) {
                            setField.addInsn(Opcodes.ICONST_1);

                            break;
                        }

                        instruction = instruction.getNext();
                    }

                    if (instruction == null) {
                        setField.addLdcInsn(Integer.MAX_VALUE);
                    }

                    setField.addFieldInsn(Opcodes.PUTFIELD, klass.name, "limitless_maxLevel", "I");

                    for (int j = 0; j != methodCount; j++) {
                        if ("<init>".equals(methods.get(j).name)) {
                            instruction = methods.get(j).instructions.getFirst();

                            while (instruction != null) {
                                if (instruction.getOpcode() == Opcodes.RETURN) {
                                    methods.get(j).instructions.insertBefore(instruction, Shortcode.copyInstructions(setField));
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
                    if (REMAPPED_COMPOUND_TAG_CLASS_NAME.equals(((MethodInsnNode) instruction).owner)) {
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
                }

                instruction = instruction.getNext();
            }
        }
    }

    static {
        TransformerApi.registerPreMixinAsmClassTransformer(LimitlessTransformer::transform);
    }
}
