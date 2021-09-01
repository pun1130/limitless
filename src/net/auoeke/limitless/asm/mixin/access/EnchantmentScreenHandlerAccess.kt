package net.auoeke.limitless.asm.mixin.access

import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.ItemStack
import net.minecraft.screen.EnchantmentScreenHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(EnchantmentScreenHandler::class)
interface EnchantmentScreenHandlerAccess {
    @Invoker("generateEnchantments")
    fun invokeGenerateEnchantments(stack: ItemStack, slot: Int, level: Int): List<EnchantmentLevelEntry>
}
