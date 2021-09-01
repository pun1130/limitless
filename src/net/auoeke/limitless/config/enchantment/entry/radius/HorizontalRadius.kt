package net.auoeke.limitless.config.enchantment.entry.radius

import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete

class HorizontalRadius {
    @BoundedDiscrete(max = 64)
    var min = 0

    @BoundedDiscrete(max = 64)
    var max = 8
}
