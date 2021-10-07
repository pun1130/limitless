package org.spongepowered.asm.mixin.transformer

import net.auoeke.limitless.transform.LimitlessMixinTransformer
import net.auoeke.reflect.Accessor
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import java.util.function.Function

object LimitlessCoprocessor {
    fun init() {
        val loader = javaClass.classLoader.javaClass.classLoader
        loader.loadClass(LimitlessMixinTransformer.javaClass.name)

        MixinEnvironment.getCurrentEnvironment().activeTransformer.ref<Any>("processor").ref<ArrayList<Any>>("coprocessors").add(
            loader.loadClass("${javaClass.name}\$Coprocessor").constructors[0].newInstance(Function {node: ClassNode ->
                LimitlessMixinTransformer.transform(node)
            })
        )
    }

    private fun <T> Any?.ref(name: String) = Accessor.getObject<T>(this, name)

    internal class Coprocessor(private val transformer: Function<ClassNode, Boolean>) : MixinCoprocessor() {
        override fun postProcess(name: String, node: ClassNode): Boolean = transformer.apply(node)
    }
}
