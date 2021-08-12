package net.auoeke.limitless.config;

import me.shedaniel.autoconfig.ConfigHolder;
import net.auoeke.limitless.config.enchantment.EnchantmentConfiguration;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.Config.Gui.Background;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import net.auoeke.limitless.Limitless;
import net.auoeke.limitless.config.anvil.AnvilConfiguration;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Config(name = Limitless.ID)
@Background("textures/block/andesite.png")
public class Configuration implements ConfigData {
    @Excluded
    public transient static final String INTERNAL_NAME = "net/auoeke/limitless/config/Configuration";

    @Excluded
    public transient static final String DESCRIPTOR = "L" + INTERNAL_NAME + ";";

    @Excluded
    public static transient final String ENCHANTMENT = "default";

    @Excluded
    public static transient final String ANVIL = "anvil";

    @Excluded
    public static transient final String COMMAND = "command";

    @Excluded
    public transient static ConfigHolder<Configuration> holder;

    @Excluded
    public transient static Configuration instance;

    public static void refresh() {
        instance = holder.get();
    }

    @Category(ENCHANTMENT)
    @TransitiveObject
    public EnchantmentConfiguration enchantment = new EnchantmentConfiguration();

    @Category(ANVIL)
    @TransitiveObject
    public AnvilConfiguration anvil = new AnvilConfiguration();

    @Override
    public void validatePostLoad() {
        this.enchantment.validatePostLoad();
    }
}
