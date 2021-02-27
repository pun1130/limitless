package user11681.limitless.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.transformer.FabricMixinTransformerProxy;
import org.spongepowered.asm.transformers.MixinClassWriter;
import user11681.fabricasmtools.Mapper;
import user11681.reflect.Classes;

public class LimitlessFabricMixinTransformerProxy extends FabricMixinTransformerProxy {
    private static final String getInt_DESCRIPTOR = "(Ljava/lang/String;)I";
    private static final String putInt_DESCRIPTOR = "(Ljava/lang/String;I)V";

    private static final String CompoundTag = Mapper.internal(2487);

    private static final String getByte = Mapper.method(10571);
    private static final String getShort = Mapper.method(10568);
    private static final String getInt = Mapper.method(10550);
    private static final String putByte = Mapper.method(10567);
    private static final String putShort = Mapper.method(10575);
    private static final String putInt = Mapper.method(10569);

    public FabricMixinTransformerProxy original;

    private static boolean transform(ClassNode klass) {
        boolean transformed = false;

        for (MethodNode method : klass.methods) {
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

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        basicClass = this.original.transformClassBytes(name, transformedName, basicClass);

        if (basicClass == null) {
            return null;
        }

        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

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
