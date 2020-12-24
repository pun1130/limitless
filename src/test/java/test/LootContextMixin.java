package test;

import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootContext.class)
abstract class LootContextMixin {
    @Redirect(method = "getLuck",
              at = @At(value = "FIELD"))
    public float removeLuck(LootContext context) {
        return 0;
    }
}
