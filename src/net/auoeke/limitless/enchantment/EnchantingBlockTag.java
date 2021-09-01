package net.auoeke.limitless.enchantment;

import java.util.List;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class EnchantingBlockTag implements Tag<Block> {
    private static final Tag<Block> enchantingBlocks = TagRegistry.block(new Identifier("c", "enchanting_blocks"));
    private static final Tag<Block> bookshelves = TagRegistry.block(new Identifier("c", "bookshelves"));

    @Override
    public boolean contains(Block entry) {
        return enchantingBlocks.contains(entry) || bookshelves.contains(entry);
    }

    @Override
    public List<Block> values() {
        var values = enchantingBlocks.values();
        values.addAll(bookshelves.values());

        return values;
    }
}
