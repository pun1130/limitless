package net.auoeke.limitless.asm.mixin.normalization;

import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.enchantment.ExperienceUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
abstract class AnvilScreenHandlerMixin {
    @Redirect(method = "onTakeOutput",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"))
    public void normalizeCost(PlayerEntity player, int levels) {
        if (Configuration.instance.getAnvil().getNormalization().getEnabled()) {
            ExperienceUtil.INSTANCE.addExperienceLevelsNormalized(player, levels);
        } else {
            player.addExperience(levels);
        }
    }
}
