package user11681.limitless.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

public class LimitlessSerializer extends GsonConfigSerializer<LimitlessConfiguration.Instance> {
    public LimitlessSerializer(final Config definition, final Class<LimitlessConfiguration.Instance> configClass) {
        super(definition, configClass);
    }

    @Override
    public void serialize(final LimitlessConfiguration.Instance config) throws SerializationException {
        super.serialize(config);

        config.copy();
    }
}
