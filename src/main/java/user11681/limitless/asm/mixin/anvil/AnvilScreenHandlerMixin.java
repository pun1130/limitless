package user11681.limitless.asm.mixin.anvil;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.anvil.entry.FilterConfiguration;
import user11681.limitless.enchantment.ExperienceUtil;

@Mixin(value = AnvilScreenHandler.class, priority = -1000)
abstract class AnvilScreenHandlerMixin {
    @Inject(method = "getNextCost", at = @At("HEAD"), cancellable = true)
    private static void getNextCost(final int cost, final CallbackInfoReturnable<Integer> cir) {
        if (LimitlessConfiguration.instance.anvil.fixedCost) {
            cir.setReturnValue(0);
        }
    }

    @Redirect(method = "updateResult",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z",
                       ordinal = 0))
    public boolean disableFilter(final PlayerAbilities abilities) {
        final FilterConfiguration filter = LimitlessConfiguration.instance.anvil.filter;

        if (abilities.creativeMode) {
            return !filter.creative;
        } else {
            return !filter.survival;
        }
    }

    @ModifyConstant(method = "updateResult",
                    constant = @Constant(intValue = 40))
    public int modifyLimit(final int originalLimit) {
        return LimitlessConfiguration.instance.anvil.levelLimit;
    }

    @Redirect(method = "onTakeOutput",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"))
    public void normalizeCost(final PlayerEntity player, final int levels) {
        if (LimitlessConfiguration.instance.anvil.normalization.enabled) {
            ExperienceUtil.addExperienceLevelsNormalized(player, levels);
        } else {
            player.addExperience(levels);
        }
    }
}
