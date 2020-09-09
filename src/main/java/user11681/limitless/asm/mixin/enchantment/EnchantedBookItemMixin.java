package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.asm.access.EnchantmentAccess;

@Mixin(EnchantedBookItem.class)
abstract class EnchantedBookItemMixin {
    @Redirect(method = "appendStacks",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int invokeOriginalGetMaxLevel(final Enchantment enchantment) {
        return ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel();
    }
}
