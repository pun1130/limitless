package user11681.limitless.asm.mixin.i18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.limitless.RomanNumerals;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(targets = "net.minecraft.util.Language$1")
abstract class LanguageMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void get(final String key, final CallbackInfoReturnable<String> info) {
        if (key.matches("enchantment\\.level\\.\\d+")) {
            info.setReturnValue(RomanNumerals.fromDecimal(Integer.parseInt(key.replaceAll("\\D", ""))));
        }
    }
}
