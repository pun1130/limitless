package org.spongepowered.asm.mixin.transformer

import net.auoeke.limitless.transform.LimitlessMixinTransformer
import net.auoeke.reflect.Accessor
import net.auoeke.reflect.Classes
import net.auoeke.reflect.Invoker
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import java.lang.invoke.MethodHandle

object LimitlessCoprocessor {
    fun init() {
        val knotLoader = javaClass.classLoader
        val loader = knotLoader.javaClass.classLoader

        MixinEnvironment.getCurrentEnvironment().activeTransformer.ref<Any>("processor").ref<ArrayList<Any>>("coprocessors").add(
            loader.crossLoad(knotLoader, "${javaClass.name}\$Coprocessor").constructors[0].newInstance(Invoker.bind(LimitlessMixinTransformer, "transform", Boolean::class.javaPrimitiveType, ClassNode::class.java))
        )
    }

    private fun <T> Any?.ref(name: String) = Accessor.getObject<T>(this, name)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun ClassLoader.crossLoad(resourceLoader: ClassLoader, name: String): Class<Any> {
        return Classes.defineClass(this, name, resourceLoader.getResourceAsStream("${name.replace('.', '/')}.class").readBytes())
    }

    internal class Coprocessor(private val transform: MethodHandle) : MixinCoprocessor() {
        override fun postProcess(name: String, node: ClassNode): Boolean = transform(node) as Boolean
    }
}
