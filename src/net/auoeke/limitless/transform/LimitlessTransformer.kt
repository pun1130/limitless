package net.auoeke.limitless.transform

import net.auoeke.extensions.asm.*
import net.auoeke.extensions.cast
import net.auoeke.extensions.find
import net.auoeke.huntinghamhills.plugin.transformer.MethodTransformer
import net.auoeke.huntinghamhills.plugin.transformer.TransformerPlugin
import net.auoeke.limitless.enchantment.EnchantingBlocks
import net.auoeke.limitless.enchantment.EnchantmentUtil
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import org.spongepowered.asm.mixin.transformer.LimitlessCoprocessor

class LimitlessTransformer : TransformerPlugin(), Opcodes {
    override fun onLoad(mixinPackage: String) {
        super.onLoad(mixinPackage)

        putField("creativeMode", 7477)
        putMethod("calculateRequiredExperienceLevel", 8227)
        putMethod("getMaxLevel", 8183)
    }

    override fun shouldApplyMixin(targetName: String, mixinName: String): Boolean = true.also {
        incompatibleMixins.forEach {
            if (FabricLoader.getInstance().isModLoaded(it.key) && it.value.matches(mixinName.substringAfter("net.auoeke.limitless.asm.mixin."))) {
                return false
            }
        }
    }

    @MethodTransformer(8229)
    private fun transformEnchantmentHelperGetPossibleEntries(method: MethodNode) {
        val instructions = method.instructions
        val iterator = instructions.iterator()

        iterator.find({it.opcode == Opcodes.INVOKEVIRTUAL && it.cast<MethodInsnNode>().name == this.method("getMaxLevel")}) {
            while (iterator.previous().type != AbstractInsnNode.LINE) {
                iterator.remove()
            }

            iterator.remove()

            for (instruction in iterator) {
                iterator.remove()

                if (instruction.type == AbstractInsnNode.IINC_INSN) {
                    break
                }
            }

            iterator.next()
            iterator.remove()
            instructions.insert(iterator.previous(), InstructionList()
                .iload(0)
                .aload(7)
                .aload(3)
                .invokestatic(
                    EnchantmentUtil.INTERNAL_NAME,
                    "getHighestSuitableLevel",
                    methodDescriptor('V', 'I', Enchantment::class, List::class),
                    false
                )
            )
        }
    }

    @MethodTransformer(17411)
    private fun transformEnchantmentScreenHandler(method: MethodNode) {
        val iterator = method.instructions.iterator()

        iterator.find({it.opcode == Opcodes.ICONST_0}) {instruction: InsnNode ->
            instruction.next.cast<VarInsnNode>().opcode = Opcodes.FSTORE

            iterator.set(MethodInsnNode(
                Opcodes.INVOKESTATIC,
                EnchantingBlocks.INTERNAL_NAME,
                "countEnchantingPower",
                methodDescriptor('F', World::class, BlockPos::class),
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

        iterator.find({instruction -> instruction.opcode == Opcodes.ILOAD && instruction.cast<VarInsnNode>().`var` == 4}) {instruction: VarInsnNode ->
            instruction.opcode = Opcodes.FLOAD
        }

        iterator.find({it.opcode == Opcodes.INVOKESTATIC}) {instruction: MethodInsnNode ->
            instruction.owner = EnchantingBlocks.INTERNAL_NAME
            instruction.name = "calculateRequiredExperienceLevel"
            instruction.desc = instruction.desc.replaceFirst("II".toRegex(), "IF")
            instruction.itf = false
        }
    }

    @MethodTransformer(8230)
    private fun transformEnchantmentHelperGenerateEnchantments(method: MethodNode) {
        val iterator = method.instructions.iterator()

        iterator.find({it.type == AbstractInsnNode.INT_INSN && (it as IntInsnNode).operand == 50}) {
            iterator.remove()
            iterator.add(VarInsnNode(Opcodes.ILOAD, 2))
            iterator.add(InsnNode(Opcodes.ICONST_2))
            iterator.add(InsnNode(Opcodes.IDIV))
            iterator.add(IntInsnNode(Opcodes.BIPUSH, 20))
            iterator.add(InsnNode(Opcodes.IADD))
        }
    }

    @MethodTransformer(7246, type = 1648)
    private fun transformEnchantBookFactoryCreate(method: MethodNode) {
        method.instructions.forEach {
            if (it.opcode == Opcodes.INVOKEVIRTUAL && (it as MethodInsnNode).name == getMaxLevel) {
                it.name = limitless_getOriginalMaxLevel
            }
        }
    }

    companion object {
        const val limitless_getOriginalMaxLevel = "limitless_getOriginalMaxLevel"

        val getMaxLevel: String get() = method(8183)
        val incompatibleMixins: Map<String, Regex> = mapOf(
            "taxfreelevels" to "normalization\\..*",
            "levelz" to "normalization\\.AnvilScreenHandlerMixin"
        ).mapValues {it.value.toRegex()}

        init {
            LimitlessCoprocessor.init()
        }
    }
}
