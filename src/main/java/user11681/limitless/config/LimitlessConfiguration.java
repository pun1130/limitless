package user11681.limitless.config;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.stream.Collectors;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import user11681.limitless.Limitless;
import user11681.limitless.asm.access.EnchantmentAccess;
import user11681.limitless.config.annotation.EnchantmentList;
import user11681.limitless.enchantment.EnchantingBlockEntry;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Config(name = Limitless.ID)
@Background("textures/block/andesite.png")
public class LimitlessConfiguration implements ConfigData {
    @Excluded
    public transient static final String INTERNAL_NAME = "user11681/limitless/config/LimitlessConfiguration";

    @Excluded
    public transient static final String DESCRIPTOR = "Luser11681/limitless/config/LimitlessConfiguration;";

    @Excluded
    public transient static LimitlessConfiguration instance;

    @Excluded
    public transient final Reference2ReferenceOpenHashMap<Block, EnchantingBlockEntry> enchantingBlockToEntry;

    public int globalMaxLevel = Integer.MAX_VALUE;

    public int maxEnchantingBlocks = 80;

    public int maxEnchantingPower = 160;

    public boolean allowTreasure = true;

    public boolean allowReenchanting = true;

    public boolean anvilIncrementalCost = false;

    @CollapsibleObject
    public RadiusConfiguration enchantingBlockRadiusXZ = new RadiusConfiguration(2, 5);

    @CollapsibleObject
    public RadiusConfiguration enchantingBlockRadiusY = new RadiusConfiguration(0, 1);

    @CollapsibleObject
    public EnchantmentParticleConfiguration enchantmentParticles = new EnchantmentParticleConfiguration();

    // waiting for Cloth Config to update to allow more complex entries
    @Excluded
    public ObjectOpenHashSet<EnchantingBlockEntry> enchantingBlocks;

    @EnchantmentList
    public ObjectLinkedOpenHashSet<EnchantmentConfiguration> maxLevels;

    @Override
    public void validatePostLoad() {
        final ObjectLinkedOpenHashSet<EnchantmentConfiguration> oldMaxLevels = this.maxLevels;

        this.maxLevels = Registry.ENCHANTMENT
            .getIds()
            .stream()
            .sorted()
            .map(EnchantmentConfiguration::new)
            .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));

        if (oldMaxLevels != null) {
            EnchantmentAccess enchantment;

            for (final EnchantmentConfiguration configuration : oldMaxLevels) {
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

    {
        this.enchantingBlocks = new ObjectOpenHashSet<>(new EnchantingBlockEntry[]{new EnchantingBlockEntry("bookshelf", 2)}, 0, 1, 1);
        this.enchantingBlockToEntry = new Reference2ReferenceOpenHashMap<>();

        for (final EnchantingBlockEntry entry : enchantingBlocks) {
            this.enchantingBlockToEntry.put(entry.getBlock(), entry);
        }
    }
}
