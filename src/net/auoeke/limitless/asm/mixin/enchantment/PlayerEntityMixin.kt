package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.enchantment.ExperienceUtil
import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(PlayerEntity::class)
internal abstract class PlayerEntityMixin {
    @Redirect(method = ["applyEnchantmentCosts"], at = At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;experienceLevel:I", ordinal = 1))
    fun normalizeCost(player: PlayerEntity, levels: Int) {
        val normalization = Configuration.instance.enchantment.normalization

        if (normalization.enabled && player.experienceLevel > normalization.offset) {
            ExperienceUtil.addExperienceLevelsRelatively(player, normalization.offset, levels - player.experienceLevel)
        } else {
            player.experienceLevel = levels
        }
    }
}
