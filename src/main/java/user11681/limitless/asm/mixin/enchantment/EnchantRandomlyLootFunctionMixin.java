package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.enchantment.EnchantmentWrapper;

@Mixin(EnchantRandomlyLootFunction.class)
abstract class EnchantRandomlyLootFunctionMixin {
    @Redirect(method = "method_26266",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    private static int fixUpperBound(Enchantment enchantment) {
        return ((EnchantmentWrapper) enchantment).getOriginalMaxLevel();
    }
}
