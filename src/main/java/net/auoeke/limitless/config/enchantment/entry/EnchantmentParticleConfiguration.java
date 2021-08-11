package net.auoeke.limitless.config.enchantment.entry;

import net.auoeke.limitless.config.enchantment.entry.radius.Radius;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;

public class EnchantmentParticleConfiguration {
    public boolean enabled = true;
    public boolean inherit = true;

    @CollapsibleObject
    public Radius radius = new Radius();
}
