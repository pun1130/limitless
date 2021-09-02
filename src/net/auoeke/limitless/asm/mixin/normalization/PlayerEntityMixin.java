package net.auoeke.limitless.asm.mixin.normalization;

import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.enchantment.ExperienceUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
    @Redirect(method = "applyEnchantmentCosts",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/entity/player/PlayerEntity;experienceLevel:I",
                       ordinal = 1))
    public void normalizeCost(PlayerEntity player, int levels) {
        var normalization = Configuration.instance.getEnchantment().getNormalization();

        if (normalization.getEnabled() && player.experienceLevel > normalization.getOffset()) {
            ExperienceUtil.INSTANCE.addExperienceLevelsRelatively(player, normalization.getOffset(), levels - player.experienceLevel);
        } else {
            player.experienceLevel = levels;
        }
    }
}
