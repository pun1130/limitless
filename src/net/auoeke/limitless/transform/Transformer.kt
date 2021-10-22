package net.auoeke.limitless.transform

import it.unimi.dsi.fastutil.objects.*
import net.auoeke.extensions.*
import net.auoeke.extensions.asm.*
import net.auoeke.huntinghamhills.plugin.transformer.*
import net.auoeke.limitless.config.*
import net.auoeke.limitless.config.enchantment.*
import net.auoeke.limitless.enchantment.*
import net.bytebuddy.agent.*
import net.fabricmc.loader.api.*
import net.minecraft.enchantment.*
import net.minecraft.util.math.*
import net.minecraft.village.TradeOffers.EnchantBookFactory
import net.minecraft.world.*
import org.objectweb.asm.*
import org.objectweb.asm.tree.*
import org.spongepowered.asm.transformers.*

class Transformer : TransformerPlugin(), Opcodes {
    override fun onLoad(mixinPackage: String) = super.onLoad(mixinPackage).also {
        putField("creativeMode", 7477)
        putMethod("calculateRequiredExperienceLevel", 8227)
        putMethod("getMaxLevel", 8183)
    }

    override fun shouldApplyMixin(targetName: String, mixinName: String): Boolean = incompatibleMixins.none {
        FabricLoader.getInstance().isModLoaded(it.key) && it.value.matches(mixinName.substringAfter("${pkg.dotted}."))
    }

    @MethodTransformer(8229)
    private fun transformEnchantmentHelperGetPossibleEntries(method: MethodNode) {
        val instructions = method.instructions
        val iterator = instructions.iterator()

        iterator.find({it.opcode == Opcodes.INVOKEVIRTUAL && it.cast<MethodInsnNode>().name == method("getMaxLevel")}) {
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
                    methodDescriptor('V', 'I', type<Enchantment>(), List::class),
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

    @MethodTransformer(7246, type = EnchantBookFactory::class)
    private fun transformEnchantBookFactoryCreate(method: MethodNode) {
        method.instructions.forEach {
            if (it.opcode == Opcodes.INVOKEVIRTUAL && (it as MethodInsnNode).name == getMaxLevel) {
                it.name = limitless_getOriginalMaxLevel
            }
        }
    }

    @Suppress("JAVA_CLASS_ON_COMPANION")
    private companion object {
        const val limitless_getOriginalMaxLevel = "limitless_getOriginalMaxLevel"
        const val limitless_useGlobalMaxLevel: String = "limitless_useGlobalMaxLevel"
        const val limitless_maxLevel: String = "limitless_maxLevel"

        val Enchantment: String = internal(1887)
        val enchantmentClassNames = ObjectOpenHashSet(arrayOf(Enchantment))
        val nonEnchantmentClassNames = ObjectOpenHashSet(arrayOf(internalName<Any>()))

        val getMaxLevel: String = method(8183)
        val getMaxPower: String = method(20742)

        val incompatibleMixins: Map<String, Regex> = mapOf(
            "taxfreelevels" to "normalization\\..*",
            "levelz" to "normalization\\.AnvilScreenHandlerMixin"
        ).mapValues {it.value.toRegex()}

        init {
            ByteBuddyAgent.install().pretransform {_, loader, _, _, bytecode -> bytecode.mapIf(loader == type.loader, ::transform)}
        }

        @Suppress("ExplicitThis")
        val ClassNode.isEnchantment get(): Boolean = false.also {
            when {
                superName in enchantmentClassNames -> {
                    enchantmentClassNames += name
                    return true
                }
                superName in nonEnchantmentClassNames -> nonEnchantmentClassNames += name
                this@Companion.type.resource("/${superName.slashed}.class")?.let {ClassNode(it, ClassReader.SKIP_CODE).isEnchantment} == true -> {
                    enchantmentClassNames += name
                    return true
                }
                else -> nonEnchantmentClassNames.add(superName, name)
            }
        }

        @Suppress("unused")
        fun transform(bytecode: ByteArray): ByteArray {
            val node = ClassNode(bytecode)
            val methods = node.methods
            var transformed = false

            if (node.isEnchantment) for (i in 0 until methods.size) {
                val method = methods[i]

                when (method.name) {
                    getMaxLevel -> if (method.desc == "()I") {
                        method.name = limitless_getOriginalMaxLevel

                        node.method(Opcodes.ACC_PUBLIC, getMaxLevel, method.desc).instructions = InstructionList {
                            aload(0) // this
                            getfield(node.name, limitless_useGlobalMaxLevel, "Z") // I
                            ifeq("custom")
                            getstatic(Configuration.INTERNAL_NAME, "instance", Configuration.DESCRIPTOR) // Configuration
                            getfield(Configuration.INTERNAL_NAME, "enchantment", EnchantmentConfiguration.DESCRIPTOR) // EnchantmentConfiguration
                            getfield(EnchantmentConfiguration.INTERNAL_NAME, "globalMaxLevel", "I") // I
                            ireturn()
                            label("custom")
                            aload(0) // this
                            getfield(node.name, limitless_maxLevel, "I") // I
                            dup() // I I
                            ldc(Int.MIN_VALUE) // I I I
                            if_icmpne("end") // I
                            pop()
                            aload(0) // this
                            invokespecial(node.name, limitless_getOriginalMaxLevel, method.desc) // I
                            label("end")
                            ireturn()
                        }
                    }
                    getMaxPower -> if (method.desc == "(I)I") {
                        method.name = "limitless_getOriginalMaxPower"

                        node.method(Opcodes.ACC_PUBLIC, getMaxPower, "(I)I").instructions = InstructionList {
                            ldc(Int.MAX_VALUE)
                            ireturn()
                        }
                    }
                    else -> continue
                }

                transformed = true
            }

            return when {
                transformed -> node.write(::MixinClassWriter)
                else -> bytecode
            }
        }
    }
}
