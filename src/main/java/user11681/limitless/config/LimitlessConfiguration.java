package user11681.limitless.config;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.stream.Collectors;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.limitless.Limitless;
import user11681.limitless.config.annotation.EnchantmentList;
import user11681.limitless.tag.EnchantingBlockEntry;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Config(name = Limitless.ID)
@Background("textures/block/andesite.png")
public class LimitlessConfiguration implements ConfigData {
    @Excluded
    public static final String INTERNAL_NAME = "user11681/limitless/config/LimitlessConfiguration";

    @Excluded
    public static final String DESCRIPTOR = "Luser11681/limitless/config/LimitlessConfiguration;";

    @Excluded
    public static LimitlessConfiguration instance;

    @Excluded
    public final Reference2ReferenceOpenHashMap<Block, EnchantingBlockEntry> enchantingBlockToEntry;

    public int globalMaxLevel = Integer.MAX_VALUE;

    public int maxEnchantingBlocks = 40;

    public int maxEnchantingPower = 80;

    @CollapsibleObject
    public RadiusConfiguration enchantingBlockRadiusXZ = new RadiusConfiguration(2, 3);

    @CollapsibleObject
    public RadiusConfiguration enchantingBlockRadiusY = new RadiusConfiguration(0, 1);

    @CollapsibleObject
    public EnchantmentParticleConfiguration enchantmentParticles = new EnchantmentParticleConfiguration();

    // waiting for Cloth Config to update to allow more complex entries
    @Excluded
    public ObjectOpenHashSet<EnchantingBlockEntry> enchantingBlocks;

    @EnchantmentList
    public ReferenceArrayList<EnchantmentConfiguration> maxLevels;

    {
        enchantingBlocks = new ObjectOpenHashSet<>(new EnchantingBlockEntry[]{new EnchantingBlockEntry("bookshelf", 2)}, 1);

        maxLevels = Registry.ENCHANTMENT
            .getIds()
            .stream()
            .sorted()
            .map((final Identifier identifier) -> new EnchantmentConfiguration(identifier, false))
            .collect(Collectors.toCollection(ReferenceArrayList::new));

        enchantingBlockToEntry = new Reference2ReferenceOpenHashMap<>();

        for (final EnchantingBlockEntry entry : enchantingBlocks) {
            enchantingBlockToEntry.put(entry.getBlock(), entry);
        }
    }
}
