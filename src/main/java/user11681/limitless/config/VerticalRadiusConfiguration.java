package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;

public class VerticalRadiusConfiguration {
    @BoundedDiscrete(min = -64, max = 64)
    public int min;

    @BoundedDiscrete(min = -64, max = 64)
    public int max;

    public VerticalRadiusConfiguration() {}

    public VerticalRadiusConfiguration(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
}
