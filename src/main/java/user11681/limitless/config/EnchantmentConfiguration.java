package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentConfiguration {
    public final String identifier;

    public int maxLevel;
    public boolean useGlobalMaxLevel;

    @Excluded
    private transient Enchantment enchantment;

    public EnchantmentConfiguration() {
        this.identifier = null;
    }

    @SuppressWarnings("ConstantConditions")
    public EnchantmentConfiguration(final String identifier, final boolean useGlobalMaxLevel) {
        this.identifier = identifier;
        this.enchantment = Registry.ENCHANTMENT.get(new Identifier(identifier));
        this.maxLevel = this.enchantment.getMaxLevel();
        this.useGlobalMaxLevel = useGlobalMaxLevel;
    }

    public Enchantment getEnchantment() {
        return this.enchantment == null ? this.enchantment = Registry.ENCHANTMENT.get(new Identifier(this.identifier)) : this.enchantment;
    }
}
