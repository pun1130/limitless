package user11681.limitless.config.enchantment.entry;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import user11681.limitless.config.enchantment.entry.radius.Radius;

public class EnchantmentParticleConfiguration {
    public boolean enabled = true;
    public boolean inherit = true;

    @CollapsibleObject
    public Radius radius = new Radius();
}
