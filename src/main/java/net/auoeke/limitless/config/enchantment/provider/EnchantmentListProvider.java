package net.auoeke.limitless.config.enchantment.provider;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.lang.reflect.Field;
import java.util.List;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.TranslatableText;
import net.auoeke.limitless.asm.access.EnchantmentAccess;
import net.auoeke.limitless.config.enchantment.entry.EnchantmentEntry;

@Environment(EnvType.CLIENT)
public class EnchantmentListProvider implements GuiProvider {
    public static final TranslatableText resetKey = new TranslatableText("text.cloth-config.reset_value");

    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess guiRegistry) {
        try {
            ObjectLinkedOpenHashSet<EnchantmentEntry> levels = (ObjectLinkedOpenHashSet<EnchantmentEntry>) field.get(config);
            ReferenceArrayList<AbstractConfigListEntry> entries = ReferenceArrayList.wrap(new AbstractConfigListEntry[levels.size()], 0);
            SubCategoryBuilder listBuilder = new SubCategoryBuilder(resetKey, new TranslatableText("config.limitless.enchantments"));

            for (EnchantmentEntry entry : levels) {
                EnchantmentAccess enchantment = entry.enchantment();

                if (enchantment != null) {
                    SubCategoryBuilder builder = new SubCategoryBuilder(resetKey, new TranslatableText(((Enchantment) enchantment).getTranslationKey()));

                    builder.add(0, new IntFieldBuilder(resetKey, new TranslatableText("config.limitless.maxLevel"), entry.maxLevel)
                        .setDefaultValue(enchantment.limitless_getOriginalMaxLevel())
                        .setSaveConsumer((Integer level) -> {
                            enchantment.limitless_setMaxLevel(level);
                            entry.maxLevel = level;
                        }).build()
                    );

                    builder.add(1, new BooleanToggleBuilder(resetKey, new TranslatableText("config.limitless.useGlobalMaxLevel"), entry.useGlobalMaxLevel)
                        .setDefaultValue(false)
                        .setSaveConsumer((Boolean useGlobalMaxLevel) -> {
                            enchantment.limitless_setUseGlobalMaxLevel(useGlobalMaxLevel);
                            entry.useGlobalMaxLevel = useGlobalMaxLevel;
                        }).build()
                    );

                    listBuilder.add(builder.build());
                }
            }

            entries.add(listBuilder.build());

            return entries;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
