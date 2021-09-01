package net.auoeke.limitless.config.enchantment.entry.normalization;

import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;

public class EnchantmentNormalizationEntry {
    public boolean enabled = true;

    @BoundedDiscrete(max = 200)
    public int offset = 30;
}
