package net.auoeke.limitless.config.enchantment.entry;

import net.auoeke.limitless.config.enchantment.entry.radius.Radius;
import net.auoeke.limitless.enchantment.EnchantingBlockEntry;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;

public class EnchantingBlockConfiguration {
    public int maxBlocks = 1 << 9;

    public int maxPower = 1 << 10;

    @CollapsibleObject
    public Radius radius = new Radius();

    @Excluded
    public ObjectOpenHashSet<EnchantingBlockEntry> allowed;
}
