package user11681.limitless.mixin.i18n;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.limitless.Limitless;

@Environment(EnvType.CLIENT)
@Mixin(I18n.class)
public abstract class I18nMixin {
    @Inject(method = "translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private static void translate(final String key, final Object[] args, final CallbackInfoReturnable<String> info) {
        if (key.matches("enchantment\\.level\\.\\d+")) {
            info.setReturnValue(Limitless.fromDecimal(Integer.parseInt(key.replaceAll("\\D", ""))));
        }
    }
}
