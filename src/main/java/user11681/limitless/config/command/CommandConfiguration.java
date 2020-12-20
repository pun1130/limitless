package user11681.limitless.config.command;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import user11681.limitless.config.command.entry.EffectCommandConfiguration;
import user11681.limitless.config.command.entry.EnchantCommandConfiguration;
import user11681.limitless.config.command.entry.FillCommandConfiguration;

public class CommandConfiguration {
    @CollapsibleObject
    public EffectCommandConfiguration effect = new EffectCommandConfiguration();

    @CollapsibleObject
    public EnchantCommandConfiguration enchant = new EnchantCommandConfiguration();

    @CollapsibleObject
    public FillCommandConfiguration fill = new FillCommandConfiguration();
}
