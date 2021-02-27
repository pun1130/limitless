package user11681.limitless.asm.mixin.enchantment;

import java.util.HashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import user11681.limitless.enchantment.EnchantmentWrapper;

@Mixin(SimpleRegistry.class)
abstract class SimpleRegistryMixin<T> {
    @Unique
    private static final HashMap<Object, EnchantmentWrapper> wrappers = new HashMap<>();

    @ModifyVariable(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;Z)Ljava/lang/Object;",
                    at = @At("HEAD"),
                    ordinal = 0)
    public <V extends T> Object wrapEnchantment(V entry) {
        if (entry instanceof Enchantment) {
            EnchantmentWrapper wrapper = new EnchantmentWrapper((Enchantment) entry);

            wrappers.put(entry, wrapper);

            return wrapper;
        }

        return entry;
    }

    @ModifyVariable(method = "getId", at = @At("HEAD"))
    public Object fixDelegateID(T entry) {
        return entry instanceof EnchantmentWrapper ? entry : entry instanceof Enchantment ? wrappers.get(entry) : entry;
    }
}
