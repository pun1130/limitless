package user11681.limitless.config.enchantment.entry.normalization;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler;
import user11681.limitless.config.common.CostDisplay;

public class EnchantmentNormalizationEntry {
    public boolean enabled = true;

    @BoundedDiscrete(max = 200)
    public int offset = 30;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public CostDisplay display = CostDisplay.APPEND;
}
