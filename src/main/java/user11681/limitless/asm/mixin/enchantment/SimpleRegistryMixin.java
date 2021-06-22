package user11681.limitless.asm.mixin.enchantment;

import com.mojang.serialization.Lifecycle;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.limitless.asm.access.EnchantmentAccess;

@Mixin(SimpleRegistry.class)
abstract class SimpleRegistryMixin<T> {
    @Inject(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;Z)Ljava/lang/Object;",
            at = @At("RETURN"))
    public <V> void initializeEnchantment(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle, boolean checkDuplicateKeys, CallbackInfoReturnable<V> info) {
        if (entry instanceof Enchantment enchantment) {
            EnchantmentAccess enchantmentAccess = (EnchantmentAccess) enchantment;

            if (enchantmentAccess.limitless_getOriginalMaxLevel() == 1) {
                enchantmentAccess.limitless_setMaxLevel(1);
            } else {
                int minLevel = enchantment.getMinLevel();
                int maxIterations = Math.min(1000, enchantmentAccess.limitless_getOriginalMaxLevel() - minLevel);
                int previousPower = enchantment.getMinPower(minLevel);

                for (int i = 1; i <= maxIterations; i++) {
                    int power = enchantment.getMinPower(minLevel + i);

                    if (previousPower < power) {
                        enchantmentAccess.limitless_setMaxLevel(Integer.MAX_VALUE);

                        return;
                    }

                    previousPower = power;
                }

                enchantmentAccess.limitless_setMaxLevel(enchantmentAccess.limitless_getOriginalMaxLevel());
            }
        }
    }

}
