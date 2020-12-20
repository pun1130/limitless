package user11681.limitless;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.enchantment.provider.EnchantmentListProvider;

import java.lang.reflect.Field;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class Limitless implements ModInitializer, ClientModInitializer {
    public static final String ID = "limitless";

    public static final ReferenceOpenHashSet<ItemStack> forConflictRemoval = new ReferenceOpenHashSet<>();

    @Override
    public void onInitialize() {
        LimitlessConfiguration.instance = AutoConfig.register(LimitlessConfiguration.class, JanksonConfigSerializer::new).getConfig();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        AutoConfig.getGuiRegistry(LimitlessConfiguration.class).registerPredicateProvider(new EnchantmentListProvider(), (final Field field) -> field.getName().equals("maxLevels"));
    }
}
