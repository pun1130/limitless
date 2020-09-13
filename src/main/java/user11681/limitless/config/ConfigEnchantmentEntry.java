package user11681.limitless.config;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConfigEnchantmentEntry {
    public final Identifier identifier;

    public int maxLevel;
    public boolean useGlobalMaxLevel;

    @SuppressWarnings("ConstantConditions")
    public ConfigEnchantmentEntry(final Identifier identifier, final boolean useGlobalMaxLevel) {
        this.identifier = identifier;
        this.maxLevel = Registry.ENCHANTMENT.get(identifier).getMaxLevel();
        this.useGlobalMaxLevel = useGlobalMaxLevel;
    }
}
