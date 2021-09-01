package net.auoeke.limitless.asm.mixin.enchantment

import com.mojang.serialization.Lifecycle
import net.auoeke.limitless.asm.access.EnchantmentAccess
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.min

@Mixin(SimpleRegistry::class)
internal abstract class SimpleRegistryMixin<T> {
    @Inject(method = ["set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;Z)Ljava/lang/Object;"], at = [At("RETURN")])
    fun <V> initializeEnchantment(rawId: Int, key: RegistryKey<T> , entry: V , lifecycle: Lifecycle , checkDuplicateKeys: Boolean , info: CallbackInfoReturnable<V> ) {
        if (entry is Enchantment) {
            val enchantmentAccess = entry as EnchantmentAccess

            if (enchantmentAccess.limitless_getOriginalMaxLevel() == 1) {
                enchantmentAccess.limitless_setMaxLevel(1)
            } else {
                val minLevel = entry.minLevel
                var previousPower = entry.getMinPower(minLevel)

                for (i in 1..min(1000, enchantmentAccess.limitless_getOriginalMaxLevel() - minLevel)) {
                    val power = entry.getMinPower(minLevel + i)

                    if (previousPower < power) {
                        enchantmentAccess.limitless_setMaxLevel(Integer.MAX_VALUE)

                        return
                    }

                    previousPower = power
                }

                enchantmentAccess.limitless_setMaxLevel(enchantmentAccess.limitless_getOriginalMaxLevel())
            }
        }
    }
}
