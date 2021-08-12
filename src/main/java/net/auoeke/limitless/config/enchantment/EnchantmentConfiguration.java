package net.auoeke.limitless.config.enchantment;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.stream.Collectors;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import net.auoeke.limitless.asm.access.EnchantmentAccess;
import net.auoeke.limitless.config.enchantment.annotation.EnchantmentList;
import net.auoeke.limitless.config.enchantment.entry.EnchantingBlockConfiguration;
import net.auoeke.limitless.config.enchantment.entry.EnchantingConflicts;
import net.auoeke.limitless.config.enchantment.entry.EnchantmentEntry;
import net.auoeke.limitless.config.enchantment.entry.EnchantmentParticleConfiguration;
import net.auoeke.limitless.config.enchantment.entry.ReenchantingConfiguration;
import net.auoeke.limitless.config.enchantment.entry.normalization.EnchantmentNormalizationEntry;
import net.minecraft.util.registry.Registry;

public class EnchantmentConfiguration implements ConfigData {
    @Excluded
    public static final String INTERNAL_NAME = "net/auoeke/limitless/config/enchantment/EnchantmentConfiguration";

    @Excluded
    public static final String DESCRIPTOR = "L" + INTERNAL_NAME + ";";

    @SuppressWarnings("unused")
    public int globalMaxLevel = Integer.MAX_VALUE;

    public boolean allowTreasure = true;

    public boolean revealEnchantments = false;

    @CollapsibleObject
    public EnchantingConflicts conflicts = new EnchantingConflicts();

    @CollapsibleObject
    public EnchantmentNormalizationEntry normalization = new EnchantmentNormalizationEntry();

    @CollapsibleObject
    public ReenchantingConfiguration reenchanting = new ReenchantingConfiguration();

    @CollapsibleObject
    public EnchantingBlockConfiguration enchantingBlocks = new EnchantingBlockConfiguration();

    @CollapsibleObject
    public EnchantmentParticleConfiguration particles = new EnchantmentParticleConfiguration();

    @EnchantmentList
    public ObjectLinkedOpenHashSet<EnchantmentEntry> maxLevels;

    @Override
    public void validatePostLoad() {
        ObjectLinkedOpenHashSet<EnchantmentEntry> oldMaxLevels = this.maxLevels;

        this.maxLevels = Registry.ENCHANTMENT
            .getIds()
            .stream()
            .sorted()
            .map(EnchantmentEntry::new)
            .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));

        if (oldMaxLevels != null) {
            for (EnchantmentEntry configuration : oldMaxLevels) {
                if (this.maxLevels.contains(configuration)) {
                    this.maxLevels.remove(configuration);
                    this.maxLevels.add(configuration);

                    EnchantmentAccess enchantment = configuration.enchantment();
                    enchantment.limitless_setMaxLevel(configuration.maxLevel);
                    enchantment.limitless_setUseGlobalMaxLevel(configuration.useGlobalMaxLevel);
                }
            }
        }
        
        this.enchantingBlocks.validatePostLoad();
    }
}
