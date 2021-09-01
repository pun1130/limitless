package net.auoeke.limitless.asm.mixin.anvil

import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.config.common.CostDisplay
import net.auoeke.limitless.enchantment.ExperienceUtil
import net.auoeke.limitless.player
import net.minecraft.client.gui.screen.ingame.AnvilScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Constant
import org.spongepowered.asm.mixin.injection.ModifyArg
import org.spongepowered.asm.mixin.injection.ModifyConstant

@Mixin(AnvilScreen::class)
internal abstract class AnvilScreenMixin(handler: AnvilScreenHandler, inventory: PlayerInventory, title: Text) : HandledScreen<AnvilScreenHandler>(handler, inventory, title) {
    @ModifyConstant(method = ["drawForeground"], constant = [Constant(intValue = 40)])
    fun modifyLimit(limit: Int): Int = Configuration.instance.anvil.levelLimit

    @ModifyArg(method = ["drawForeground"], at = At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableText;<init>(Ljava/lang/String;[Ljava/lang/Object;)V"), index = 1)
    fun showNormalizedCost(arguments: Array<Any>): Array<Any> {
        val cost = this.handler.levelCost
        val player: PlayerEntity = player
        val normalization = Configuration.instance.anvil.normalization

        if (normalization.enabled && normalization.display != CostDisplay.NORMAL && !player.abilities.creativeMode && player.experienceLevel > cost) {
            val relative = ExperienceUtil.normalizedCost(player, cost)

            arguments[0] = when (normalization.display) {
                CostDisplay.REPLACE -> relative.toString()
                else -> "${arguments[0]} ($relative)"
            }
        }

        return arguments
    }
}
