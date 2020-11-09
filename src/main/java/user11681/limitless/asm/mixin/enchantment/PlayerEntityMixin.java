package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.enchantment.entry.normalization.EnchantmentNormalizationEntry;
import user11681.limitless.enchantment.ExperienceUtil;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
    @Redirect(method = "applyEnchantmentCosts",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/entity/player/PlayerEntity;experienceLevel:I",
                       ordinal = 1))
    public void normalizeCost(final PlayerEntity player, final int levels) {
        final EnchantmentNormalizationEntry normalization = LimitlessConfiguration.instance.enchantment.normalization;

        if (normalization.enabled && player.experienceLevel > normalization.offset) {
            ExperienceUtil.addExperienceLevelsRelatively(player, normalization.offset, levels - player.experienceLevel);
        }
    }
}
