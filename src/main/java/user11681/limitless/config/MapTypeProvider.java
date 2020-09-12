package user11681.limitless.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.limitless.asm.access.EnchantmentAccess;

public class MapTypeProvider implements GuiProvider {
    @Override
    public List<AbstractConfigListEntry> get(final String i13n, final Field field, final Object config, final Object defaults, final GuiRegistryAccess registry) {
        try {
            final Object2IntOpenHashMap<Identifier> levels = (Object2IntOpenHashMap<Identifier>) field.get(config);
            final ReferenceArrayList<AbstractConfigListEntry> entries = ReferenceArrayList.wrap(new AbstractConfigListEntry[levels.size()], 0);

            for (final Object2IntMap.Entry<Identifier> entry : levels.object2IntEntrySet()) {
                final Identifier identifier = entry.getKey();
                final Enchantment enchantment = Registry.ENCHANTMENT.get(identifier);

                entries.add(new IntegerListEntry(
                    new TranslatableText(enchantment.getTranslationKey()),
                    enchantment.getMaxLevel(),
                    new TranslatableText("reset"),
                    ((EnchantmentAccess) enchantment)::limitless_getOriginalMaxLevel,
                    ((EnchantmentAccess) enchantment)::limitless_setMaxLevel
                ));
            }

            return entries.parallelStream().sorted(Comparator.comparingInt((AbstractConfigListEntry entry) -> entry.getFieldName().getString().charAt(0))).collect(Collectors.toList());
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
