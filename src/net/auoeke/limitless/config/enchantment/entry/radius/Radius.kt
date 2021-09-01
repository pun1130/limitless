package net.auoeke.limitless.config.enchantment.entry.radius

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject

class Radius {
    @CollapsibleObject(startExpanded = true)
    var xz = HorizontalRadius()

    @CollapsibleObject(startExpanded = true)
    var y = VerticalRadius()
}
