package test

import net.minecraft.loot.context.LootContext
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(LootContext::class)
internal abstract class LootContextMixin {
    @Redirect(method = ["getLuck"], at = At(value = "FIELD"))
    fun removeLuck(context: LootContext?): Float = 0F
}
