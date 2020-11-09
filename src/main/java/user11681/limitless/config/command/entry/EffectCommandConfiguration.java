package user11681.limitless.config.command.entry;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;

public class EffectCommandConfiguration {
    public int amplifierLimit = Integer.MAX_VALUE;

    @BoundedDiscrete(max = Integer.MAX_VALUE / 20)
    public int durationLimit = Integer.MAX_VALUE / 20;
}
