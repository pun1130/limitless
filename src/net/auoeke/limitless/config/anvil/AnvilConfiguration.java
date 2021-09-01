package net.auoeke.limitless.config.anvil;

import net.auoeke.limitless.config.anvil.entry.AnvilNormalizationEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;

public class AnvilConfiguration {
    public boolean mergeConflicts = true;

    public boolean mergeIncompatible = false;

    public boolean fixedCost = true;

    public int levelLimit = Integer.MAX_VALUE;

    @CollapsibleObject
    public AnvilNormalizationEntry normalization = new AnvilNormalizationEntry();
}
