package user11681.limitless.config.enchantment.entry;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentEntry {
    public final String identifier;

    public int maxLevel;
    public boolean useGlobalMaxLevel;

    @Excluded
    private transient Enchantment enchantment;

    public EnchantmentEntry() {
        this.identifier = null;
    }

    @SuppressWarnings("ConstantConditions")
    public EnchantmentEntry(final Identifier identifier) {
        this.identifier = identifier.toString();
        this.enchantment = Registry.ENCHANTMENT.get(identifier);
        this.maxLevel = this.enchantment.getMaxLevel();
        this.useGlobalMaxLevel = false;
    }

    public Enchantment getEnchantment() {
        return this.enchantment == null ? this.enchantment = Registry.ENCHANTMENT.get(new Identifier(this.identifier)) : this.enchantment;
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof EnchantmentEntry && that.hashCode() == this.hashCode();
    }
}
