package net.auoeke.limitless.config.enchantment.entry.radius

import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete

class VerticalRadius {
    @BoundedDiscrete(min = -64, max = 64)
    var min = -8

    @BoundedDiscrete(min = -64, max = 64)
    var max = 8
}
