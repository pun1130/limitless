package test

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.StorageMinecartEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(StorageMinecartEntity::class)
internal abstract class StorageMinecartEntityMixin {
    @Redirect(method = ["createMenu"], at = At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    fun forceLoot(entity: PlayerEntity?): Boolean = false
}
