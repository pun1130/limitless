package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.asm.access.EnchantmentAccess
import net.auoeke.limitless.config.Configuration
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import org.spongepowered.asm.mixin.Intrinsic
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Constant
import org.spongepowered.asm.mixin.injection.ModifyConstant
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(EnchantedBookItem::class)
internal abstract class EnchantedBookItemMixin(settings: Settings?) : Item(settings) {
    @Redirect(method = ["appendStacks"], at = At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    fun appendOriginalLevelsToGroup(enchantment: Enchantment): Int = (enchantment as EnchantmentAccess).limitless_getOriginalMaxLevel()

    @ModifyConstant(method = ["isEnchantable"], constant = [Constant(intValue = 0)])
    fun makeReenchantable(previous: Int): Int = when {
        Configuration.instance.enchantment.reenchanting.allowEnchantedBooks() -> 1
        else -> previous
    }

    @Intrinsic
    override fun getEnchantability(): Int = when {
        Configuration.instance.enchantment.reenchanting.allowEnchantedBooks() -> Items.BOOK.enchantability
        else -> 0
    }
}
