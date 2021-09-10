package net.auoeke.limitless.asm

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.config.enchantment.EnchantmentConfiguration
import net.auoeke.shortcode.instruction.ExtendedInsnList
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.spongepowered.asm.mixin.transformer.FabricMixinTransformerProxy
import org.spongepowered.asm.transformers.MixinClassWriter
import user11681.reflect.Classes

@Suppress("NAME_SHADOWING")
class LimitlessMixinTransformerProxy : FabricMixinTransformerProxy() {
    override fun transformClassBytes(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
        val basicClass: ByteArray = super.transformClassBytes(name, transformedName, basicClass) ?: return null
        val node = ClassNode().also {ClassReader(basicClass).accept(it, 0)}

        return when {
            transform(node) -> MixinClassWriter(ClassWriter.COMPUTE_FRAMES).also(node::accept).toByteArray()
            else -> basicClass
        }
    }

    companion object {
        private const val limitless_useGlobalMaxLevel = "limitless_useGlobalMaxLevel"
        private const val limitless_maxLevel = "limitless_maxLevel"
        private val enchantmentClassNames = ObjectOpenHashSet(arrayOf(LimitlessTransformer.Enchantment))

        init {
            Classes.load(ClassNode::class.java.name, ClassReader::class.java.name, ClassWriter::class.java.name)
        }

        private fun transform(klass: ClassNode): Boolean {
            val methods = klass.methods
            var transformed = false

            if (this.enchantmentClassNames.contains(klass.superName) || LimitlessTransformer.Enchantment == klass.name) {
                if (LimitlessTransformer.Enchantment != klass.name) {
                    this.enchantmentClassNames.add(klass.superName)
                }

                for (i in 0 until methods.size) {
                    val method = methods[i]

                    if (method.name == LimitlessTransformer.getMaxLevel && method.desc == "()I") {
                        (klass.visitMethod(Opcodes.ACC_PUBLIC, LimitlessTransformer.getMaxLevel, method.desc, null, null) as MethodNode).instructions = ExtendedInsnList()
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
                            .invokespecial(klass.name, LimitlessTransformer.limitless_getOriginalMaxLevel, method.desc) // I
                            .label("end")
                            .ireturn()
                        method.name = LimitlessTransformer.limitless_getOriginalMaxLevel
                    } else if (method.name == LimitlessTransformer.getMaxPower && method.desc == "(I)I") {
                        method.name = "limitless_getOriginalMaxPower"
                        (klass.visitMethod(Opcodes.ACC_PUBLIC, LimitlessTransformer.getMaxPower, "(I)I", null, null) as MethodNode).instructions = ExtendedInsnList()
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
