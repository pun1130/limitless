package test;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootableContainerBlockEntity.class)
abstract class LootableContainerBlockEntityMixin {
    @Redirect(method = "checkUnlocked",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    public boolean forceUnlocked(final PlayerEntity entity) {
        return false;
    }
}
