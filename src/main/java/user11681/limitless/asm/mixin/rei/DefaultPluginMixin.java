package user11681.limitless.asm.mixin.rei;

import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.asm.access.EnchantmentAccess;

@Pseudo
@Mixin(DefaultPlugin.class)
abstract class DefaultPluginMixin {
    @Redirect(method = "registerEntries",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int fixEnchantmentEntries(final Enchantment enchantment) {
        return ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel();
    }
}
