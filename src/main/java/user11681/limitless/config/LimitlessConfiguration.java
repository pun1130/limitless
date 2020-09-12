package user11681.limitless.config;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.limitless.Limitless;

public class LimitlessConfiguration {
    public static Object2IntOpenHashMap<Identifier> maxLevels;

    private static final Object2ReferenceOpenHashMap<String, Field> staticFields;
    private static final ReferenceOpenHashSet<Field> instanceFields;

    static {
        try {
            Field[] fieldArray = LimitlessConfiguration.class.getDeclaredFields();
            int fieldCount = fieldArray.length;
            staticFields = new Object2ReferenceOpenHashMap<>(fieldCount);

            for (int i = 0; i != fieldCount; i++) {
                final Field field = fieldArray[i];

                if ((field.getModifiers() & Modifier.FINAL) == 0) {
                    staticFields.put(field.getName(), field);
                }
            }

            fieldArray = Instance.class.getDeclaredFields();
            fieldCount = fieldArray.length;
            instanceFields = new ReferenceOpenHashSet<>(fieldCount);

            for (int i = 0; i != fieldCount; i++) {
                final Field field = fieldArray[i];

                if ((field.getModifiers() & Modifier.FINAL) == 0) {
                    instanceFields.add(field);
                }
            }
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Config(name = Limitless.ID)
    @Background("textures/block/andesite.png")
    public static class Instance implements ConfigData {
        public Object2IntOpenHashMap<Identifier> maxLevels = new Object2IntOpenHashMap<>();

        {
            for (final Enchantment enchantment : Registry.ENCHANTMENT) {
                this.maxLevels.put(Registry.ENCHANTMENT.getId(enchantment), enchantment.getMaxLevel());
            }
        }

        @Override
        public void validatePostLoad() {
            this.copy();
        }

        public void copy() {
            try {
                for (final Field field : instanceFields) {
                    staticFields.get(field.getName()).set(null, field.get(this));
                }
            } catch (final Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
