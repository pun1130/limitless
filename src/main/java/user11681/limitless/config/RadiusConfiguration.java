package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;

public class RadiusConfiguration {
    @BoundedDiscrete(max = 64)
    public int min;

    @BoundedDiscrete(max = 64)
    public int max;

    public RadiusConfiguration() {}

    public RadiusConfiguration(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
}
