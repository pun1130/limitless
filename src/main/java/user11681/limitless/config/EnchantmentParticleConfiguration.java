package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;

public class EnchantmentParticleConfiguration {
    public boolean enabled = true;
    public boolean inherit = true;

    @CollapsibleObject
    public RadiusConfiguration radiusXZ = new RadiusConfiguration(2, 8);

    @CollapsibleObject
    public VerticalRadiusConfiguration radiusY = new VerticalRadiusConfiguration(-5, 5);
}
