package net.auoeke.limitless.transform.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import net.auoeke.limitless.transform.access.EnchantmentAccess;

@Mixin(Enchantment.class)
abstract class EnchantmentMixin implements EnchantmentAccess {
    public int limitless_maxLevel = Integer.MIN_VALUE;
    public boolean limitless_useGlobalMaxLevel;

    @Override
    public void limitless_setMaxLevel(int level) {
        this.limitless_maxLevel = level;
    }

    @Override
    public void limitless_setUseGlobalMaxLevel(boolean useGlobalMaxLevel) {
        this.limitless_useGlobalMaxLevel = useGlobalMaxLevel;
    }
}
