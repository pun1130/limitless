package net.auoeke.limitless.config.anvil.entry;

import net.auoeke.limitless.config.common.CostDisplay;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;

public class AnvilNormalizationEntry {
    public boolean enabled = true;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public CostDisplay display = CostDisplay.REPLACE;
}
