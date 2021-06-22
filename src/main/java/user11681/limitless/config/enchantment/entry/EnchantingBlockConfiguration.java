package user11681.limitless.config.enchantment.entry;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import user11681.limitless.config.enchantment.entry.radius.Radius;
import user11681.limitless.enchantment.EnchantingBlockEntry;

public class EnchantingBlockConfiguration {
    public int maxBlocks = 1 << 9;

    public int maxPower = 1 << 10;

    @CollapsibleObject
    public Radius radius = new Radius();

    @Excluded
    public ObjectOpenHashSet<EnchantingBlockEntry> allowed;
}
