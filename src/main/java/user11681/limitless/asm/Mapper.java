package user11681.limitless.asm;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public abstract class Mapper {
    public static final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();

    protected static final Object2ObjectOpenHashMap<String, String> classes = new Object2ObjectOpenHashMap<>();
    protected static final Object2ObjectOpenHashMap<String, String> fields = new Object2ObjectOpenHashMap<>();
    protected static final Object2ObjectOpenHashMap<String, String> methods = new Object2ObjectOpenHashMap<>();

    private static final boolean development = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static String internal(final String yarn) {
        return klass(yarn).replace('.', '/');
    }

    public static String klass(final String yarn) {
        final String intermediary = classes.get(yarn);

        if (intermediary == null) {
            throw new IllegalArgumentException(yarn);
        }

        return intermediary;
    }

    public static String field(final String yarn) {
        final String intermediary = fields.get(yarn);

        if (intermediary == null) {
            return yarn;
        }

        if (development) {
            throw new IllegalArgumentException(yarn);
        }

        return intermediary;
    }

    public static String method(final String yarn) {
        final String intermediary = methods.get(yarn);

        if (intermediary == null) {
            return yarn;
        }

        if (development) {
            throw new IllegalArgumentException(yarn);
        }

        return intermediary;
    }

    protected static void putClass(final String yarn, final int number) {
        String mapped = "net.minecraft.class_" + number;

        if (development) {
            mapped = mappingResolver.mapClassName("intermediary", mapped);
        }

        if (classes.put(yarn, mapped) != null) {
            throw new IllegalArgumentException(mapped + number + " already exists.");
        }
    }

    protected static void putField(final String yarn, final int number) {
        if (fields.put(yarn, "field_" + number) != null) {
            throw new IllegalArgumentException("field_" + number + " already exists.");
        }
    }

    protected static void putMethod(final String yarn, final int number) {
        if (methods.put(yarn, "method_" + number) != null) {
            throw new IllegalArgumentException("method_" + number + " already exists.");
        }
    }

    static {
        putClass("Enchantment", 1887);
        putClass("CompoundTag", 2487);

        if (!development) {
            putField("creativeMode", 7477);

            putMethod("drawForeground", 2388);
            putMethod("create", 7246);
            putMethod("getMaxLevel", 8183);
            putMethod("calculateRequiredExperienceLevel", 8227);
            putMethod("getPossibleEntries", 8229);
            putMethod("generateEnchantments", 8230);
            putMethod("getInt", 10550);
            putMethod("putByte", 10567);
            putMethod("getShort", 10568);
            putMethod("getByte", 10571);
            putMethod("putShort", 10575);
            putMethod("putInt", 10569);
            putMethod("getNextCost", 20398);
            putMethod("getMaxPower", 20742);
            putMethod("updateResult", 24928);
        }
    }
}
