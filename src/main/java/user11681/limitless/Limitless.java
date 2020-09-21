package user11681.limitless;

import java.lang.reflect.Field;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.provider.EnchantmentListProvider;

public class Limitless implements ModInitializer, ClientModInitializer {
    public static final String ID = "limitless";

    @Override
    public void onInitialize() {
        LimitlessConfiguration.instance = AutoConfig.register(LimitlessConfiguration.class, JanksonConfigSerializer::new).getConfig();
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.getGuiRegistry(LimitlessConfiguration.class).registerPredicateProvider(new EnchantmentListProvider(), (final Field field) -> field.getName().equals("maxLevels"));

        // disabled until Cloth Config should update in order to allow more complex entries
//        AutoConfig.getGuiRegistry(LimitlessConfiguration.class).registerPredicateProvider(new EnchantingBlockListProvider(), (final Field field) -> field.getName().equals("enchantingBlocks"));
    }
}
