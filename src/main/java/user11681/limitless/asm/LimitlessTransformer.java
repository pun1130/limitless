package user11681.limitless.asm;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import net.devtech.grossfabrichacks.entrypoints.PrePrePreLaunch;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import net.devtech.grossfabrichacks.transformer.asm.AsmClassTransformer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Label;
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
import user11681.limitless.config.LimitlessConfiguration;
import user11681.shortcode.Shortcode;
import user11681.shortcode.instruction.DelegatingInsnList;

public class LimitlessTransformer extends Mapper implements PrePrePreLaunch, AsmClassTransformer, Opcodes, IMixinConfigPlugin {
    private static final String getIntDescriptor = "(Ljava/lang/String;)I";
    private static final String putIntDescriptor = "(Ljava/lang/String;I)V";

    private static final String Enchantment = internal("Enchantment");
    private static final String CompoundTag = internal("CompoundTag");
    private static final String getMaxLevel = method("getMaxLevel");
    private static final String getMaxPower = method("getMaxPower");
    private static final String getShort = method("getShort");
    private static final String getInt = method("getInt");
    private static final String getByte = method("getByte");
    private static final String putByte = method("putByte");
    private static final String putShort = method("putShort");
    private static final String putInt = method("putInt");
    private static final String getOriginalMaxLevel = "limitless_getOriginalMaxLevel";

    private static final ObjectOpenHashSet<String> enchantmentClassNames = new ObjectOpenHashSet<>(32, 1);

    @Override
    public void onPrePrePreLaunch() {
        TransformerApi.registerPostMixinAsmClassTransformer(this);
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
        switch (mixinClassName) {
            case "user11681.limitless.asm.mixin.enchantment.dummy.AnvilScreenDummyMixin":
                transformAnvilScreen(targetClass); break;
            case "user11681.limitless.asm.mixin.enchantment.dummy.AnvilScreenHandlerDummyMixin":
                transformAnvilScreenHandler(targetClass); break;
            case "user11681.limitless.asm.mixin.enchantment.dummy.EnchantBookFactoryDummyMixin":
                transformEnchantBookFactory(targetClass); break;
            case "user11681.limitless.asm.mixin.enchantment.EnchantmentHelperMixin":
                transformEnchantmentHelper(targetClass); break;
            case "user11681.limitless.asm.mixin.enchantment.EnchantmentScreenHandlerMixin":
                transformEnchantmentScreenHandler(targetClass); break;
        }
    }

    private static void transformAnvilScreen(final ClassNode targetClass) {
        final String drawForeground = method("drawForeground");
        final List<MethodNode> methods = targetClass.methods;
        final int methodCount = methods.size();

        for (int i = methodCount - 1; i >= 0; --i) {
            if (drawForeground.equals(methods.get(i).name)) {
                final ListIterator<AbstractInsnNode> iterator = methods.get(i).instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(field("creativeMode")),
                    () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.FRAME)
                );

                break;
            }
        }
    }

    private static void transformAnvilScreenHandler(final ClassNode targetClass) {
        final String getNextCost = method("getNextCost");
        final String updateResult = method("updateResult");
        final List<MethodNode> methods = targetClass.methods;
        final int methodCount = methods.size();

        for (int i = methodCount - 1; i >= 0; i--) {
            final MethodNode method = methods.get(i);

            if (getNextCost.equals(method.name)) {
                method.instructions.clear();
                method.visitVarInsn(Opcodes.ILOAD, 0);
                method.visitInsn(Opcodes.IRETURN);
            } else if (updateResult.equals(method.name)) {
                final String creativeMode = field("creativeMode");
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(creativeMode),
                    () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.LINE)
                );

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode) instruction).name.equals(creativeMode),
                    () -> Shortcode.removeBetween(iterator, AbstractInsnNode.LINE, AbstractInsnNode.FRAME)
                );
            }
        }
    }

    private static void transformEnchantBookFactory(final ClassNode targetClass) {
        final String create = method("create");

        for (final MethodNode method : targetClass.methods) {
            if (method.name.equals(create)) {
                AbstractInsnNode instruction = method.instructions.getFirst();

                while (instruction != null) {
                    if (instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel)) {
                        ((MethodInsnNode) instruction).name = getOriginalMaxLevel;
                    }

                    instruction = instruction.getNext();
                }

                return;
            }
        }
    }

    private static void transformEnchantmentHelper(final ClassNode targetClass) {
        final MethodNode[] methods = targetClass.methods.toArray(new MethodNode[0]);
        final int methodCount = methods.length;
        final String getPossibleEntries = method("getPossibleEntries");
        final String generateEnchantments = method("generateEnchantments");

        for (int i = methodCount - 1; i >= 0; i--) {
            final MethodNode method = methods[i];

            if (getPossibleEntries.equals(method.name)) {
                final InsnList instructions = method.instructions;
                final ListIterator<AbstractInsnNode> iterator = instructions.iterator();

                Shortcode.findForward(iterator,
                    (final AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel),
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
                            "(IL" + Enchantment + ";Ljava/util/List;)V",
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
                        iterator.set(new MethodInsnNode(INVOKESTATIC, "user11681/limitless/enchantment/EnchantingBlocks", "countEnchantingPower", Shortcode.composeMethodDescriptor("F", mappedWorldDescriptor, mappedBlockPosDescriptor), true));
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

                break;
            }
        }
    }

    @Override
    public void transform(final String name, final ClassNode klass) {
        final List<MethodNode> methods = klass.methods;
        final int methodCount = methods.size();
        AbstractInsnNode instruction;
        MethodInsnNode methodInstruction;
        MethodNode method;
        int i;

        if (enchantmentClassNames.contains(klass.superName) || Enchantment.equals(name)) {
            if (!Enchantment.equals(name)) {
                enchantmentClassNames.add(klass.superName);
            }

            for (i = 0; i != methodCount; i++) {
                if (getMaxLevel.equals((method = methods.get(i)).name) && method.desc.equals("()I")) {
                    final MethodNode newGetMaxLevel = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, getMaxLevel, "()I", null, null);
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

                    method.name = getOriginalMaxLevel;

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
                                    setField.addMethodInsn(Opcodes.INVOKEVIRTUAL, klass.name, getOriginalMaxLevel, "()I", false); // this I
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
                } else if (getMaxPower.equals(methods.get(i).name)) {
                    methods.get(i).name = "limitless_getOriginalMaxPower";

                    final MethodNode newGetMaxPower = (MethodNode) klass.visitMethod(Opcodes.ACC_PUBLIC, getMaxPower, "(I)I", null, null);

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
                    if (CompoundTag.equals(((MethodInsnNode) instruction).owner)) {
                        methodInstruction = (MethodInsnNode) instruction;

                        if (putShort.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2S) {
                            methods.get(i).instructions.remove(methodInstruction.getPrevious());
                            methodInstruction.name = putInt;
                            methodInstruction.desc = putIntDescriptor;

                            if (methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                                methods.get(i).instructions.remove(methodInstruction.getPrevious());
                            }
                        } else if (getShort.equals(methodInstruction.name)) {
                            methodInstruction.name = getInt;
                            methodInstruction.desc = getIntDescriptor;
                        } else if (putByte.equals(methodInstruction.name) && methodInstruction.getPrevious().getOpcode() == Opcodes.I2B) {
                            methods.get(i).instructions.remove(methodInstruction.getPrevious());
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

    static {
        enchantmentClassNames.add(Enchantment);
    }
}
