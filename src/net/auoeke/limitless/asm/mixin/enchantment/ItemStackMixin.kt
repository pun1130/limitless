package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.config.Configuration
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(ItemStack::class)
internal abstract class ItemStackMixin {
    @Redirect(method = ["isEnchantable"], at = At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z"))
    fun makeReenchantable(stack: ItemStack): Boolean = !Configuration.instance.enchantment.reenchanting.allowEquipment() && stack.hasEnchantments()

    @Redirect(method = ["addEnchantment"], at = At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;createNbt(Lnet/minecraft/util/Identifier;I)Lnet/minecraft/nbt/NbtCompound;"))
    fun disableNarrowingConversion(id: Identifier?, level: Int): NbtCompound = EnchantmentHelper.createNbt(id, level)
}
