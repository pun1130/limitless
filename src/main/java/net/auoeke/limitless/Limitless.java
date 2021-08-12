package net.auoeke.limitless;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.config.enchantment.provider.EnchantmentListProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class Limitless implements ModInitializer, ClientModInitializer {
    public static final String ID = "limitless";

    public static final ReferenceOpenHashSet<ItemStack> forConflictRemoval = new ReferenceOpenHashSet<>();

    @Override
    public void onInitialize() {
        Configuration.holder = AutoConfig.register(Configuration.class, GsonConfigSerializer::new);
        Configuration.refresh();
        CommandRegistrationCallback.EVENT.register(LimitlessCommand::register);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        AutoConfig.getGuiRegistry(Configuration.class).registerPredicateProvider(new EnchantmentListProvider(), field -> field.getName().equals("maxLevels"));
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("unused")
    public static class ModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return parent -> AutoConfig.getConfigScreen(Configuration.class, parent).get();
        }
    }
}
