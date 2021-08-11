package net.auoeke.limitless.config;

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
public class LimitlessConfiguration implements ConfigData {
    @Excluded
    public transient static final String INTERNAL_NAME = "net/auoeke/limitless/config/LimitlessConfiguration";

    @Excluded
    public transient static final String DESCRIPTOR = "L" + INTERNAL_NAME + ";";

    @Excluded
    public static transient final String ENCHANTMENT = "default";

    @Excluded
    public static transient final String ANVIL = "anvil";

    @Excluded
    public static transient final String COMMAND = "command";

    @Excluded
    public transient static LimitlessConfiguration instance;

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
