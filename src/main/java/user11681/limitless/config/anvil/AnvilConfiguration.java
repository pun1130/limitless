package user11681.limitless.config.anvil;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import user11681.limitless.config.anvil.entry.AnvilNormalizationEntry;
import user11681.limitless.config.anvil.entry.FilterConfiguration;

public class AnvilConfiguration {
    @CollapsibleObject
    public FilterConfiguration filter = new FilterConfiguration();

    @CollapsibleObject
    public AnvilNormalizationEntry normalization = new AnvilNormalizationEntry();

    public boolean fixedCost = true;

    public int levelLimit = Integer.MAX_VALUE;
}
