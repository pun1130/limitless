package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.limitless.config.LimitlessConfiguration;

@Mixin(value = AnvilScreenHandler.class, priority = -1000)
abstract class AnvilScreenHandlerMixin {
    @Inject(method = "getNextCost", at = @At("HEAD"), cancellable = true)
    private static void getNextCost(final int cost, final CallbackInfoReturnable<Integer> cir) {
        if (LimitlessConfiguration.instance.disableAnvilIncrease) {
            cir.setReturnValue(0);
        }
    }
}
