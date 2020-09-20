package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;

public class EnchantmentParticleConfiguration {
    public boolean enabled = true;
    public boolean inherit = true;

    @CollapsibleObject
    public RadiusConfiguration radiusXZ = new RadiusConfiguration(2, 3);

    @CollapsibleObject
    public RadiusConfiguration radiusY = new RadiusConfiguration(0, 1);
}
