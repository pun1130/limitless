package net.auoeke.limitless.asm;

import net.auoeke.limitless.enchantment.EnchantingBlocks;
import net.auoeke.limitless.enchantment.EnchantmentUtil;
import net.auoeke.shortcode.Shortcode;
import net.auoeke.shortcode.instruction.ExtendedInsnList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import user11681.huntinghamhills.plugin.TransformerPlugin;
import user11681.reflect.Accessor;
import user11681.reflect.Classes;

public class LimitlessTransformer extends TransformerPlugin implements Opcodes {
    static final String limitless_getOriginalMaxLevel = "limitless_getOriginalMaxLevel";
    static final String limitless_useGlobalMaxLevel = "limitless_useGlobalMaxLevel";
    static final String limitless_maxLevel = "limitless_maxLevel";

    static final String Enchantment = internal(1887);
    static final String getMaxLevel = method(8183);
    static final String getMaxPower = method(20742);

    private static void transformEnchantmentHelperGenerateEnchantments(MethodNode method) {
        var iterator = method.instructions.iterator();

        Shortcode.findForward(iterator,
            instruction -> instruction.getType() == AbstractInsnNode.INT_INSN && ((IntInsnNode) instruction).operand == 50,
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

    private static void transformEnchantBookFactoryCreate(MethodNode method) {
        var instruction = method.instructions.getFirst();

        while (instruction != null) {
            if (instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(getMaxLevel)) {
                ((MethodInsnNode) instruction).name = limitless_getOriginalMaxLevel;
            }

            instruction = instruction.getNext();
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);

        this.putClass("World", 1937);
        this.putClass("BlockPos", 2338);
        this.putInternal("Enchantment", 1887);
        this.putField("creativeMode", 7477);
        this.putMethod("calculateRequiredExperienceLevel", 8227);
        this.putMethod("getMaxLevel", 8183);

        this.methodAfter(klass(1890), method(8230), null, LimitlessTransformer::transformEnchantmentHelperGenerateEnchantments);
        this.methodAfter(klass(3853, 1648), method(7246), null, LimitlessTransformer::transformEnchantBookFactoryCreate);
        this.methodAfter(klass(1890), method(8229), null, this::transformEnchantmentHelperGetPossibleEntries);
        this.methodAfter(klass(1718), method(17411), null, this::transformEnchantmentScreenHandler);
    }

    private void transformEnchantmentHelperGetPossibleEntries(MethodNode method) {
        var instructions = method.instructions;
        var iterator = instructions.iterator();

        Shortcode.findForward(iterator,
            instruction -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(this.method("getMaxLevel")),
            (MethodInsnNode instruction) -> {
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
                        Shortcode.composeDescriptor("V", "I", Enchantment, "java/util/List"),
                        true
                    )
                );
            }
        );
    }

    private void transformEnchantmentScreenHandler(MethodNode method) {
        var iterator = method.instructions.iterator();

        Shortcode.findForward(iterator,
            instruction -> instruction.getOpcode() == ICONST_0,
            (InsnNode instruction) -> {
                ((VarInsnNode) instruction.getNext()).setOpcode(FSTORE);
                iterator.set(new MethodInsnNode(
                    INVOKESTATIC,
                    EnchantingBlocks.INTERNAL_NAME,
                    "countEnchantingPower",
                    Shortcode.composeDescriptor("F", this.klass("World"), this.klass("BlockPos")),
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
            if (iterator.next().getOpcode() == GOTO) {
                ++gotoCount;
            }

            iterator.remove();
        }

        Shortcode.findForward(iterator,
            instruction -> instruction.getOpcode() == ILOAD && ((VarInsnNode) instruction).var == 4,
            (VarInsnNode instruction) -> instruction.setOpcode(FLOAD)
        );

        Shortcode.findForward(iterator,
            instruction -> instruction.getOpcode() == INVOKESTATIC,
            (MethodInsnNode instruction) -> {
                instruction.owner = EnchantingBlocks.INTERNAL_NAME;
                instruction.name = "calculateRequiredExperienceLevel";
                instruction.desc = instruction.desc.replaceFirst("II", "IF");
                instruction.itf = true;
            }
        );
    }

    static {
        Classes.reinterpret(Accessor.getObject((Object) Accessor.getObject(LimitlessTransformer.class.getClassLoader(), "delegate"), "mixinTransformer"), LimitlessMixinTransformerProxy.class);
    }
}
