package net.auoeke.limitless.asm.mixin.anvil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.auoeke.limitless.config.LimitlessConfiguration;
import net.auoeke.limitless.enchantment.ExperienceUtil;

@Mixin(value = AnvilScreenHandler.class, priority = -1000)
abstract class AnvilScreenHandlerMixin {
    @Inject(method = "getNextCost", at = @At("HEAD"), cancellable = true)
    private static void getNextCost(int cost, CallbackInfoReturnable<Integer> info) {
        if (LimitlessConfiguration.instance.anvil.fixedCost) {
            info.setReturnValue(0);
        }
    }

    @Redirect(method = "updateResult",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean configureAcceptableItem(Enchantment enchantment, ItemStack stack) {
        return enchantment.isAcceptableItem(stack) || LimitlessConfiguration.instance.anvil.mergeIncompatible;

    }

    @Redirect(method = "updateResult",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;canCombine(Lnet/minecraft/enchantment/Enchantment;)Z"))
    public boolean configureConflict(Enchantment enchantment, Enchantment other) {
        return enchantment.canCombine(other) || LimitlessConfiguration.instance.anvil.mergeConflicts;

    }

    @ModifyConstant(method = "updateResult",
                    constant = @Constant(intValue = 40))
    public int modifyLimit(int originalLimit) {
        return LimitlessConfiguration.instance.anvil.levelLimit;
    }

    @Redirect(method = "onTakeOutput",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"))
    public void normalizeCost(PlayerEntity player, int levels) {
        if (LimitlessConfiguration.instance.anvil.normalization.enabled) {
            ExperienceUtil.addExperienceLevelsNormalized(player, levels);
        } else {
            player.addExperience(levels);
        }
    }
}
