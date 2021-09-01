package net.auoeke.limitless.config.enchantment.entry

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import net.auoeke.limitless.config.enchantment.entry.radius.Radius

class EnchantmentParticleConfiguration {
    var enabled: Boolean = true
    var inherit: Boolean = true

    @CollapsibleObject
    var radius = Radius()
}
