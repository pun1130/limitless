package net.auoeke.limitless.config.enchantment.entry;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import net.auoeke.limitless.config.enchantment.entry.radius.Radius;
import net.auoeke.limitless.enchantment.EnchantingBlocks;
import net.auoeke.limitless.log.LimitlessLogger;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantingBlockConfiguration implements ConfigData {
    public int maxBlocks = 512;
    public int maxPower = 1024;

    @CollapsibleObject
    public Radius radius = new Radius();

    @Excluded
    public Map<String, Float> whitelist = new Object2FloatOpenHashMap<>(new String[]{EXAMPLE_BLOCK}, new float[]{5});

    @Excluded
    public List<String> blacklist = new ObjectArrayList<>(new String[]{EXAMPLE_BLOCK});

    @Excluded
    public transient final Reference2FloatMap<Block> blockWhitelist = new Reference2FloatOpenHashMap<>();

    @Excluded
    public transient final Set<Block> blockBlacklist = new ReferenceOpenHashSet<>();

    private static final String EXAMPLE_BLOCK = "examplemod:example_block";

    private static Optional<Block> verifyBlock(String key, String type) {
        var id = Identifier.tryParse(key);

        if (id == null) {
            LimitlessLogger.error("Key \"%s\" listed in limitless' enchanting block %slist is not formatted correctly.", type);
        } else if (!key.equals(EXAMPLE_BLOCK)) {
            var block = Registry.BLOCK.getOrEmpty(id);

            if (block.isPresent()) {
                return block;
            }

            LimitlessLogger.warn("A block with identifier \"%s\" is not registered.", id);
        }

        return Optional.empty();
    }

    public float enchantingPower(Block block) {
        if (this.blockWhitelist.containsKey(block)) {
            return this.blockWhitelist.getFloat(block);
        }

        if (this.blockBlacklist.contains(block) || !EnchantingBlocks.tag.contains(block)) {
            return 0;
        }

        return 2;
    }

    @Override
    public void validatePostLoad() {
        this.blockWhitelist.clear();
        this.blockBlacklist.clear();

        this.whitelist.forEach((key, power) -> verifyBlock(key, "white").ifPresent(block -> this.blockWhitelist.put(block, power)));
        this.blacklist.forEach(key -> verifyBlock(key, "black").ifPresent(this.blockBlacklist::add));

        this.blockWhitelist.keySet().stream()
            .filter(this.blockBlacklist::contains)
            .forEach(block -> LimitlessLogger.warn("Block with identifier \"%s\" was found in whitelist and blacklist; whitelist takes precedence.", Registry.BLOCK.getId(block)));
    }
}
