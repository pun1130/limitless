package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import user11681.limitless.asm.access.EnchantmentAccess;

@Mixin(Enchantment.class)
abstract class EnchantmentMixin implements EnchantmentAccess {
    public int limitless_maxLevel;

    @Override
    public void limitless_setMaxLevel(final int level) {
        this.limitless_maxLevel = level;
    }
}
