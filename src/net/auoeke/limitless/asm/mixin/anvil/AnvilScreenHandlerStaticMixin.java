package net.auoeke.limitless.asm.mixin.anvil;

import kotlin.jvm.JvmStatic;
import net.auoeke.limitless.config.Configuration;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
abstract class AnvilScreenHandlerStaticMixin {
    @JvmStatic
    @Inject(method ="getNextCost", at = @At("HEAD"), cancellable = true)
    private static void getNextCost(int cost, CallbackInfoReturnable<Integer> info) {
        if (Configuration.instance.getAnvil().getFixedCost()) {
            info.setReturnValue(0);
        }
    }
}
