package user11681.limitless.asm.mixin.rei;

import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.asm.access.EnchantmentAccess;

@Pseudo
@Mixin(targets = "me.shedaniel.rei.plugin.DefaultPlugin")
abstract class DefaultPluginMixin {
    @Redirect(method = "registerEntries",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int fixEnchantmentEntries(final Enchantment enchantment) {
        return ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel();
    }
}
