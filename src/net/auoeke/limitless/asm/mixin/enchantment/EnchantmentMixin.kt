package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.asm.access.EnchantmentAccess
import net.minecraft.enchantment.Enchantment
import org.spongepowered.asm.mixin.Mixin

@Suppress("PropertyName")
@Mixin(Enchantment::class)
internal abstract class EnchantmentMixin : EnchantmentAccess {
    @JvmField
    var limitless_maxLevel = Int.MIN_VALUE

    @JvmField
    var limitless_useGlobalMaxLevel = false

    override fun limitless_setMaxLevel(level: Int) {
        this.limitless_maxLevel = level
    }

    override fun limitless_setUseGlobalMaxLevel(useGlobalMaxLevel: Boolean) {
        this.limitless_useGlobalMaxLevel = useGlobalMaxLevel
    }

    // override fun limitless_useGlobalMaxLevel(): Boolean = limitless_useGlobalMaxLevel
}
