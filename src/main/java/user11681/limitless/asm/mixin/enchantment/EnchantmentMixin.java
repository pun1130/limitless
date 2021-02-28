package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import user11681.limitless.enchantment.EnchantmentWrapper;

@Mixin(Enchantment.class)
abstract class EnchantmentMixin {
    @SuppressWarnings("ConstantConditions")
    @Intrinsic
    @Override
    public boolean equals(Object that) {
        return that instanceof EnchantmentWrapper && this == (Object) ((EnchantmentWrapper) that).delegate || that == this;
    }
}
