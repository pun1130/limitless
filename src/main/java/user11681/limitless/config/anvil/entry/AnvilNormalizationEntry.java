package user11681.limitless.config.anvil.entry;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler;
import user11681.limitless.config.common.CostDisplay;

public class AnvilNormalizationEntry {
    public boolean enabled = true;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public CostDisplay display = CostDisplay.REPLACE;
}
