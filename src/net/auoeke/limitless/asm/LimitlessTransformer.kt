package net.auoeke.limitless.asm

import net.auoeke.limitless.enchantment.EnchantingBlocks
import net.auoeke.limitless.enchantment.EnchantmentUtil
import net.auoeke.shortcode.Shortcode
import net.auoeke.shortcode.instruction.ExtendedInsnList
import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import user11681.huntinghamhills.plugin.TransformerPlugin
import user11681.reflect.Accessor
import user11681.reflect.Classes

class LimitlessTransformer : TransformerPlugin(), Opcodes {
    override fun onLoad(mixinPackage: String) {
        super.onLoad(mixinPackage)
        this.putClass("World", 1937)
        this.putClass("BlockPos", 2338)
        this.putInternal("Enchantment", 1887)
        this.putField("creativeMode", 7477)
        this.putMethod("calculateRequiredExperienceLevel", 8227)
        this.putMethod("getMaxLevel", 8183)
        this.methodAfter(klass(1890), method(8230), null, this::transformEnchantmentHelperGenerateEnchantments)
        this.methodAfter(klass(3853, 1648), method(7246), null, this::transformEnchantBookFactoryCreate)
        this.methodAfter(klass(1890), method(8229), null, this::transformEnchantmentHelperGetPossibleEntries)
        this.methodAfter(klass(1718), method(17411), null, this::transformEnchantmentScreenHandler)
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        incompatibleMixins.forEach {
            if (FabricLoader.getInstance().isModLoaded(it.key) && it.value.matches(mixinClassName.substringAfter("net.auoeke.limitless.asm.mixin."))) {
                return false
            }
        }

        return true
    }

    private fun transformEnchantmentHelperGetPossibleEntries(method: MethodNode) {
        val instructions = method.instructions
        val iterator = instructions.iterator()

        Shortcode.findForward(iterator, {instruction -> instruction.opcode == Opcodes.INVOKEVIRTUAL && (instruction as MethodInsnNode).name == this.method("getMaxLevel")}) {
            Shortcode.removeBetweenInclusive(iterator, AbstractInsnNode.LINE, AbstractInsnNode.IINC_INSN)
            iterator.next()
            iterator.remove()
            instructions.insert(iterator.previous(), ExtendedInsnList()
                .iload(0)
                .aload(7)
                .aload(3)
                .invokestatic(
                    EnchantmentUtil.INTERNAL_NAME,
                    "getHighestSuitableLevel",
                    Shortcode.composeDescriptor("V", "I", Enchantment, "java/util/List"),
                    false
                )
            )
        }
    }

    private fun transformEnchantmentScreenHandler(method: MethodNode) {
        val iterator = method.instructions.iterator()

        Shortcode.findForward(iterator, {it.opcode == Opcodes.ICONST_0}) {instruction: InsnNode ->
            (instruction.next as VarInsnNode).opcode = Opcodes.FSTORE
            iterator.set(MethodInsnNode(
                Opcodes.INVOKESTATIC,
                EnchantingBlocks.INTERNAL_NAME,
                "countEnchantingPower",
                Shortcode.composeDescriptor("F", this.klass("World"), this.klass("BlockPos")),
                false
            ))

            iterator.previous()
            iterator.add(VarInsnNode(Opcodes.ALOAD, 2))
            iterator.add(VarInsnNode(Opcodes.ALOAD, 3))
        }

        iterator.next()
        iterator.next()

        var gotoCount = 0

        while (gotoCount != 3) {
            if (iterator.next().opcode == Opcodes.GOTO) {
                ++gotoCount
            }

            iterator.remove()
        }

        Shortcode.findForward(iterator, {instruction -> instruction.opcode == Opcodes.ILOAD && (instruction as VarInsnNode).`var` == 4}) {instruction: VarInsnNode ->
            instruction.opcode = Opcodes.FLOAD
        }

        Shortcode.findForward(iterator, {it.opcode == Opcodes.INVOKESTATIC}) {instruction: MethodInsnNode ->
            instruction.owner = EnchantingBlocks.INTERNAL_NAME
            instruction.name = "calculateRequiredExperienceLevel"
            instruction.desc = instruction.desc.replaceFirst("II".toRegex(), "IF")
            instruction.itf = false
        }
    }

    private fun transformEnchantmentHelperGenerateEnchantments(method: MethodNode) {
        val iterator = method.instructions.iterator()

        Shortcode.findForward(iterator, {instruction -> instruction.type == AbstractInsnNode.INT_INSN && (instruction as IntInsnNode).operand == 50}) {
            iterator.remove()
            iterator.add(VarInsnNode(Opcodes.ILOAD, 2))
            iterator.add(InsnNode(Opcodes.ICONST_2))
            iterator.add(InsnNode(Opcodes.IDIV))
            iterator.add(IntInsnNode(Opcodes.BIPUSH, 20))
            iterator.add(InsnNode(Opcodes.IADD))
        }
    }

    private fun transformEnchantBookFactoryCreate(method: MethodNode) {
        var instruction = method.instructions.first

        while (instruction != null) {
            if (instruction.opcode == Opcodes.INVOKEVIRTUAL && (instruction as MethodInsnNode).name == getMaxLevel) {
                instruction.name = limitless_getOriginalMaxLevel
            }

            instruction = instruction.next
        }
    }

    companion object {
        const val limitless_getOriginalMaxLevel = "limitless_getOriginalMaxLevel"

        val Enchantment: String = internal(1887)
        val getMaxLevel: String = method(8183)
        val getMaxPower: String = method(20742)
        val incompatibleMixins: Map<String, Regex> = mapOf(
            "taxfreelevels" to "normalization.*",
            "levelz" to "normalization.AnvilScreenHandlerMixin"
        ).mapValues {it.value.toRegex()}

        init {
            Classes.reinterpret(Accessor.getObject(Accessor.getObject(LimitlessTransformer::class.java.classLoader, "delegate") as Any, "mixinTransformer"), LimitlessMixinTransformerProxy::class.java)
        }
    }
}
