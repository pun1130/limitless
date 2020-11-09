package user11681.limitless.config.enchantment;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.stream.Collectors;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import user11681.limitless.asm.access.EnchantmentAccess;
import user11681.limitless.config.enchantment.annotation.EnchantmentList;
import user11681.limitless.config.enchantment.entry.EnchantmentEntry;
import user11681.limitless.config.enchantment.entry.EnchantmentParticleConfiguration;
import user11681.limitless.config.enchantment.entry.ReenchantingConfiguration;
import user11681.limitless.config.enchantment.entry.normalization.EnchantmentNormalizationEntry;
import user11681.limitless.config.enchantment.entry.EnchantingBlockConfiguration;
import user11681.limitless.enchantment.EnchantingBlockEntry;

public class EnchantmentConfiguration implements ConfigData {
    @Excluded
    public static transient final String INTERNAL_NAME = "user11681/limitless/config/enchantment/EnchantmentConfiguration";

    @Excluded
    public static transient final String DESCRIPTOR = "L" + INTERNAL_NAME + ";";

    @SuppressWarnings("unused")
    public int globalMaxLevel = Integer.MAX_VALUE;

    public boolean allowTreasure = true;

    public boolean revealEnchantments = false;

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

    @Excluded
    public transient final Reference2ReferenceOpenHashMap<Block, EnchantingBlockEntry> enchantingBlockToEntry;

    public EnchantmentConfiguration() {
        this.enchantingBlocks.allowed = new ObjectOpenHashSet<>(new EnchantingBlockEntry[]{new EnchantingBlockEntry("bookshelf", 2)}, 0, 1, 1);
        this.enchantingBlockToEntry = new Reference2ReferenceOpenHashMap<>();

        for (final EnchantingBlockEntry entry : enchantingBlocks.allowed) {
            this.enchantingBlockToEntry.put(entry.getBlock(), entry);
        }
    }

    @Override
    public void validatePostLoad() {
        final ObjectLinkedOpenHashSet<EnchantmentEntry> oldMaxLevels = this.maxLevels;

        this.maxLevels = Registry.ENCHANTMENT
            .getIds()
            .stream()
            .sorted()
            .map(EnchantmentEntry::new)
            .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));

        if (oldMaxLevels != null) {
            EnchantmentAccess enchantment;

            for (final EnchantmentEntry configuration : oldMaxLevels) {
                if (this.maxLevels.contains(configuration)) {
                    this.maxLevels.remove(configuration);
                    this.maxLevels.add(configuration);

                    enchantment = (EnchantmentAccess) configuration.getEnchantment();
                    enchantment.limitless_setMaxLevel(configuration.maxLevel);
                    enchantment.limitless_setUseGlobalMaxLevel(configuration.useGlobalMaxLevel);
                }
            }
        }
    }
}
