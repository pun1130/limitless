package user11681.limitless.config.enchantment.entry.radius;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;

public class VerticalRadius {
    @BoundedDiscrete(min = -64, max = 64)
    public int min = -8;

    @BoundedDiscrete(min = -64, max = 64)
    public int max = 8;

    public VerticalRadius() {}

    public VerticalRadius(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
}
