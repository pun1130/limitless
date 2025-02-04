package net.auoeke.limitless.transform.mixin.enchantment;

import net.auoeke.limitless.config.Configuration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    @Redirect(method = "isEnchantable",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z"))
    public boolean makeReenchantable(ItemStack stack) {
        return !Configuration.instance.enchantment.getReenchanting().allowEquipment() && stack.hasEnchantments();
    }

    @Redirect(method = "addEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;createNbt(Lnet/minecraft/util/Identifier;I)Lnet/minecraft/nbt/NbtCompound;"))
    public NbtCompound disableNarrowingConversion(Identifier id, int truncated, Enchantment enchantment, int level) {
        return EnchantmentHelper.createNbt(id, level);
    }
}
