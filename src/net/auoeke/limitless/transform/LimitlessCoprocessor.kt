@file:Suppress("PackageDirectoryMismatch")

package org.spongepowered.asm.mixin.transformer

import org.objectweb.asm.tree.ClassNode
import java.lang.invoke.MethodHandle

@Suppress("unused")
internal class LimitlessCoprocessor(private val transform: MethodHandle) : MixinCoprocessor() {
    var count = 0

    override fun postProcess(name: String, node: ClassNode): Boolean = transform(node).also {println(++count)} as Boolean
}
