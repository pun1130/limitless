package user11681.limitless.config.enchantment.entry.radius;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;

public class Radius {
    @CollapsibleObject(startExpanded = true)
    public HorizontalRadius xz = new HorizontalRadius();

    @CollapsibleObject(startExpanded = true)
    public VerticalRadius y = new VerticalRadius();
}
