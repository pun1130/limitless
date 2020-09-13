package user11681.limitless;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.MapTypeProvider;

public class Limitless implements ModInitializer {
    public static final String ID = "limitless";

    @Override
    public void onInitialize() {
        LimitlessConfiguration.instance = AutoConfig.register(LimitlessConfiguration.class, GsonConfigSerializer::new).getConfig();

        AutoConfig.getGuiRegistry(LimitlessConfiguration.class).registerTypeProvider(new MapTypeProvider(), ReferenceArrayList.class);
    }
}
