package user11681.limitless;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.ModInitializer;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.LimitlessSerializer;
import user11681.limitless.config.MapTypeProvider;

public class Limitless implements ModInitializer {
    public static final String ID = "limitless";

    @Override
    public void onInitialize() {
        AutoConfig.register(LimitlessConfiguration.Instance.class, LimitlessSerializer::new);

        AutoConfig.getGuiRegistry(LimitlessConfiguration.Instance.class).registerTypeProvider(new MapTypeProvider(), Object2IntOpenHashMap.class);
    }
}
