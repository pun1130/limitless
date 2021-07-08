package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.config.LimitlessConfiguration;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    @Redirect(method = "isEnchantable",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z"))
    public boolean makeReenchantable(ItemStack stack) {
        return !LimitlessConfiguration.instance.enchantment.reenchanting.allowEquipment() && stack.hasEnchantments();
    }

    @Redirect(method = "addEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;createNbt(Lnet/minecraft/util/Identifier;I)Lnet/minecraft/nbt/NbtCompound;"))
    public NbtCompound disableNarrowingConversion(Identifier id, int level) {
        return EnchantmentHelper.createNbt(id, level);
    }
}
