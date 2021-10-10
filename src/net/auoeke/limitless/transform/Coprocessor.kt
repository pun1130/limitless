@file:Suppress("PackageDirectoryMismatch")

package org.spongepowered.asm.mixin.transformer

import org.objectweb.asm.tree.ClassNode
import java.lang.invoke.MethodHandle

@Suppress("unused")
internal class Coprocessor(private val transform: MethodHandle) : MixinCoprocessor() {
    override fun postProcess(name: String, node: ClassNode): Boolean = transform(node) as Boolean
}
