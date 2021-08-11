package net.auoeke.limitless.enchantment;

import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantingBlockEntry implements Comparable<EnchantingBlockEntry> {
    public String identifier;
    public float power;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    private EnchantingBlockEntry() {
        this.power = 2;
    }

    public EnchantingBlockEntry(String identifier, float power) {
        this.identifier = identifier;
        this.power = power;
    }

    public Block getBlock() {
        return Registry.BLOCK.get(new Identifier(this.identifier));
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof EnchantingBlockEntry)) {
            return false;
        }

        final EnchantingBlockEntry castThat = (EnchantingBlockEntry) that;

        return Objects.equals(castThat.identifier, this.identifier) && this.power == castThat.power;
    }

    @Override
    public int compareTo(EnchantingBlockEntry that) {
        return this.identifier.compareTo(that.identifier);
    }
}
