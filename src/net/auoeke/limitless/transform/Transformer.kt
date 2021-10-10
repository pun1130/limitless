package net.auoeke.limitless.transform

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.auoeke.extensions.asm.*
import net.auoeke.extensions.cast
import net.auoeke.extensions.find
import net.auoeke.extensions.type
import net.auoeke.huntinghamhills.plugin.transformer.MethodTransformer
import net.auoeke.huntinghamhills.plugin.transformer.TransformerPlugin
import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.config.enchantment.EnchantmentConfiguration
import net.auoeke.limitless.enchantment.EnchantingBlocks
import net.auoeke.limitless.enchantment.EnchantmentUtil
import net.auoeke.reflect.Accessor
import net.auoeke.reflect.Classes
import net.auoeke.reflect.Invoker
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.impl.gui.FabricGuiEntry
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import org.spongepowered.asm.mixin.MixinEnvironment

class Transformer : TransformerPlugin(), Opcodes {
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

    @MethodTransformer(7246, type = 1648)
    private fun transformEnchantBookFactoryCreate(method: MethodNode) {
        method.instructions.forEach {
            if (it.opcode == Opcodes.INVOKEVIRTUAL && (it as MethodInsnNode).name == getMaxLevel) {
                it.name = limitless_getOriginalMaxLevel
            }
        }
    }

    @Suppress("JAVA_CLASS_ON_COMPANION")
    private companion object {
        private const val limitless_getOriginalMaxLevel = "limitless_getOriginalMaxLevel"
        private const val limitless_useGlobalMaxLevel: String = "limitless_useGlobalMaxLevel"
        private const val limitless_maxLevel: String = "limitless_maxLevel"

        private val Enchantment: String = internal(1887)
        private val enchantmentClassNames = ObjectOpenHashSet(arrayOf(Enchantment))

        private val getMaxLevel: String = method(8183)
        private val getMaxPower: String = method(20742)

        private val incompatibleMixins: Map<String, Regex> = mapOf(
            "taxfreelevels" to "normalization\\..*",
            "levelz" to "normalization\\.AnvilScreenHandlerMixin"
        ).mapValues {it.value.toRegex()}

        init {
            // @formatter:off
            "0.12.1".also {requiredVersion -> if (FabricLoader.getInstance().getModContainer("fabricloader").get().metadata.version < Version.parse(requiredVersion)) {
                FabricGuiEntry.displayCriticalError(object : RuntimeException("limitless requires Fabric version $requiredVersion or greater.", null, false, false) {}, true)
            }}
            // @formatter:on

            MixinEnvironment.getCurrentEnvironment().activeTransformer.ref<Any>("processor").ref<ArrayList<Any>>("coprocessors").add(
                javaClass.classLoader.run {javaClass.classLoader.crossLoad(this, "org.spongepowered.asm.mixin.transformer.LimitlessCoprocessor")}
                    .constructors[0]
                    .newInstance(Invoker.bind(this, "transform", Boolean::class.javaPrimitiveType, ClassNode::class.java))
            )
        }

        private fun <T> Any?.ref(name: String) = Accessor.getObject<T>(this, name)

        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        private fun ClassLoader.crossLoad(resourceLoader: ClassLoader, name: String): Class<Any> {
            return Classes.defineClass(this, name, resourceLoader.getResourceAsStream("${name.replace('.', '/')}.class").readBytes())
        }

        @Suppress("unused")
        private fun transform(klass: ClassNode): Boolean {
            val methods = klass.methods
            var transformed = false

            if (klass.superName in enchantmentClassNames || Enchantment == klass.name) {
                if (Enchantment != klass.name) {
                    enchantmentClassNames += klass.superName
                }

                for (i in 0 until methods.size) {
                    val method = methods[i]

                    if (method.name == getMaxLevel && method.desc == "()I") {
                        method.name = limitless_getOriginalMaxLevel

                        klass.method(Opcodes.ACC_PUBLIC, getMaxLevel, method.desc).instructions = InstructionList()
                            .aload(0) // this
                            .getfield(klass.name, limitless_useGlobalMaxLevel, "Z") // I
                            .ifeq("custom")
                            .getstatic(Configuration.INTERNAL_NAME, "instance", Configuration.DESCRIPTOR) // Configuration
                            .getfield(Configuration.INTERNAL_NAME, "enchantment", EnchantmentConfiguration.DESCRIPTOR) // EnchantmentConfiguration
                            .getfield(EnchantmentConfiguration.INTERNAL_NAME, "globalMaxLevel", "I") // I
                            .ireturn()
                            .label("custom")
                            .aload(0) // this
                            .getfield(klass.name, limitless_maxLevel, "I") // I
                            .dup() // I I
                            .ldc(Int.MIN_VALUE) // I I I
                            .if_icmpne("end") // I
                            .pop()
                            .aload(0) // this
                            .invokespecial(klass.name, limitless_getOriginalMaxLevel, method.desc) // I
                            .label("end")
                            .ireturn()
                    } else if (method.name == getMaxPower && method.desc == "(I)I") {
                        method.name = "limitless_getOriginalMaxPower"

                        klass.method(Opcodes.ACC_PUBLIC, getMaxPower, "(I)I").instructions = InstructionList()
                            .ldc(Int.MAX_VALUE)
                            .ireturn()
                    } else {
                        continue
                    }

                    transformed = true
                }
            }

            return transformed
        }
    }
}
