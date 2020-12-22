package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Category;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.TransitiveObject;
import user11681.limitless.Limitless;
import user11681.limitless.config.anvil.AnvilConfiguration;
import user11681.limitless.config.enchantment.EnchantmentConfiguration;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Config(name = Limitless.ID)
@Background("textures/block/andesite.png")
public class LimitlessConfiguration implements ConfigData {
    @Excluded
    public transient static final String INTERNAL_NAME = "user11681/limitless/config/LimitlessConfiguration";

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
