package net.auoeke.limitless.asm.mixin.enchantment;

import net.auoeke.limitless.asm.access.EnchantmentAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantRandomlyLootFunction.class)
abstract class EnchantRandomlyLootFunctionMixin {
    @Redirect(method = "addEnchantmentToStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    private static int fixUpperBound(Enchantment enchantment) {
        return ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel();
    }
}
