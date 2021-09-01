package net.auoeke.limitless.config.enchantment.entry.normalization

import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete

class EnchantmentNormalizationEntry {
    var enabled = true

    @BoundedDiscrete(max = 200)
    var offset = 30
}
