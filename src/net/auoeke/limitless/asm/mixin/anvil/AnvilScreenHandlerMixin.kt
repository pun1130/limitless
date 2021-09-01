package net.auoeke.limitless.asm.mixin.anvil

import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.enchantment.ExperienceUtil
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.AnvilScreenHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Constant
import org.spongepowered.asm.mixin.injection.ModifyConstant
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(value = [AnvilScreenHandler::class], priority = -1000)
internal abstract class AnvilScreenHandlerMixin {
    @Redirect(method = ["updateResult"], at = At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    fun configureAcceptableItem(enchantment: Enchantment, stack: ItemStack): Boolean = enchantment.isAcceptableItem(stack) || Configuration.instance.anvil.mergeIncompatible

    @Redirect(method = ["updateResult"], at = At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canCombine(Lnet/minecraft/enchantment/Enchantment;)Z"))
    fun configureConflict(enchantment: Enchantment, other: Enchantment): Boolean = enchantment.canCombine(other) || Configuration.instance.anvil.mergeConflicts

    @ModifyConstant(method = ["updateResult"], constant = [Constant(intValue = 40)])
    fun modifyLimit(originalLimit: Int): Int = Configuration.instance.anvil.levelLimit

    @Redirect(method = ["onTakeOutput"], at = At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"))
    fun normalizeCost(player: PlayerEntity, levels: Int) = when {
        Configuration.instance.anvil.normalization.enabled -> ExperienceUtil.addExperienceLevelsNormalized(player, levels)
        else -> player.addExperience(levels)
    }
}
