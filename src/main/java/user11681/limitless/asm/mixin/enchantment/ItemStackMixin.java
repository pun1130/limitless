package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.config.LimitlessConfiguration;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    @Redirect(method = "isEnchantable",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z"))
    public boolean makeReenchantable(final ItemStack stack) {
        return !LimitlessConfiguration.instance.allowReenchanting && stack.hasEnchantments();
    }
}
