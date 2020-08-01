package user11681.limitless.mixin.i18n;

import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.limitless.Limitless;

@Mixin(value = TranslationStorage.class, targets = "net.minecraft.util.Language$1")
public abstract class LanguageMixin {
    @Inject(method = "get(Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void get(final String key, final CallbackInfoReturnable<String> info) {
        if (key.matches("enchantment\\.level\\.\\d+")) {
            info.setReturnValue(Limitless.fromDecimal(Integer.parseInt(key.replaceAll("\\D", ""))));
        }
    }
}
