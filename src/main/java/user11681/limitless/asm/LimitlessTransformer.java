package user11681.limitless.asm;

import java.util.ListIterator;
import net.gudenau.lib.unsafe.Unsafe;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import user11681.fabricasmtools.plugin.TransformerPlugin;
import user11681.limitless.enchantment.EnchantingBlocks;
import user11681.limitless.enchantment.EnchantmentUtil;
import user11681.limitless.enchantment.EnchantmentWrapper;
import user11681.reflect.Accessor;
import user11681.shortcode.Shortcode;
import user11681.shortcode.instruction.ExtendedInsnList;

public class LimitlessTransformer extends TransformerPlugin implements Opcodes {
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

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);

        this.putClass("World", 1937);
        this.putClass("BlockPos", 2338);
        this.putInternal("Enchantment", 1887);
        this.putField("creativeMode", 7477);
        this.putMethod("calculateRequiredExperienceLevel", 8227);
        this.putMethod("getMaxLevel", 8183);

        this.registerPostMixinMethodTransformer(klass(1890), method(8230), null, LimitlessTransformer::transformEnchantmentHelperGenerateEnchantments);
        this.registerPostMixinMethodTransformer(klass(3853, 1648), method(7246), null, this::transformEnchantBookFactoryCreate);
        this.registerPostMixinMethodTransformer(klass(1890), method(8229), null, this::transformEnchantmentHelperGetPossibleEntries);
        this.registerPostMixinMethodTransformer(klass(1718), method(17411), null, this::transformEnchantmentScreenHandler);
    }

    private void transformEnchantBookFactoryCreate(MethodNode method) {
        Shortcode.findForward(method.instructions.iterator(),
            (AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(this.method("getMaxLevel")),
            (MethodInsnNode instruction) -> {
                instruction.owner = EnchantmentWrapper.INTERNAL_NAME;
                instruction.name = "originalMaxLevel";

                method.instructions.insertBefore(instruction, new TypeInsnNode(Opcodes.CHECKCAST, EnchantmentWrapper.INTERNAL_NAME));
            }
        );
    }

    private void transformEnchantmentHelperGetPossibleEntries(MethodNode method) {
        InsnList instructions = method.instructions;
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) instruction).name.equals(this.method("getMaxLevel")),
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
                        Shortcode.composeMethodDescriptor("V", "I", this.internal("Enchantment"), "java/util/List"),
                        true
                    )
                );
            }
        );
    }

    private void transformEnchantmentScreenHandler(MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == ICONST_0,
            (InsnNode instruction) -> {
                ((VarInsnNode) instruction.getNext()).setOpcode(FSTORE);
                iterator.set(new MethodInsnNode(
                    INVOKESTATIC,
                    EnchantingBlocks.INTERNAL_NAME,
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
            (VarInsnNode instruction) -> instruction.setOpcode(FLOAD)
        );

        Shortcode.findForward(iterator,
            (AbstractInsnNode instruction) -> instruction.getOpcode() == INVOKESTATIC,
            (MethodInsnNode instruction) -> {
                instruction.owner = EnchantingBlocks.INTERNAL_NAME;
                instruction.name = "calculateRequiredExperienceLevel";
                instruction.desc = instruction.desc.replaceFirst("II", "IF");
                instruction.itf = true;
            }
        );
    }

    static {
        Object delegate = Accessor.getObject(LimitlessTransformer.class.getClassLoader(), "delegate");
        LimitlessFabricMixinTransformerProxy transformer = Unsafe.allocateInstance(LimitlessFabricMixinTransformerProxy.class);

        transformer.original = Accessor.getObject(delegate, "mixinTransformer");

        Accessor.putObject(delegate, "mixinTransformer", transformer);
    }
}
