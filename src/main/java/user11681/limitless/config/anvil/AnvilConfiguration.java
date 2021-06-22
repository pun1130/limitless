package user11681.limitless.config.anvil;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import user11681.limitless.config.anvil.entry.AnvilNormalizationEntry;

public class AnvilConfiguration {
    public boolean mergeConflicts = true;

    public boolean mergeIncompatible = false;

    public boolean fixedCost = true;

    public int levelLimit = Integer.MAX_VALUE;

    @CollapsibleObject
    public AnvilNormalizationEntry normalization = new AnvilNormalizationEntry();
}
