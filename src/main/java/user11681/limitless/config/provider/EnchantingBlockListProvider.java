package user11681.limitless.config.provider;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import user11681.limitless.config.EnchantingBlockListListEntry;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.provider.EnchantmentListProvider;
import user11681.limitless.tag.EnchantingBlockEntry;

@Environment(EnvType.CLIENT)
public class EnchantingBlockListProvider implements GuiProvider {
    @Override
    public List<AbstractConfigListEntry> get(final String i13n, final Field field, final Object config, final Object defaults, final GuiRegistryAccess registry) {
        try {
            final LimitlessConfiguration configuration = (LimitlessConfiguration) config;
            final LimitlessConfiguration defaultConfiguration = (LimitlessConfiguration) defaults;

            return ReferenceArrayList.wrap(new AbstractConfigListEntry[]{new EnchantingBlockListListEntry(
                new TranslatableText("config.limitless.enchanting_blocks"),
                ReferenceArrayList.wrap(configuration.enchantingBlocks.toArray(new EnchantingBlockEntry[0])),
                false,
                Optional::empty,
                (final List<EnchantingBlockEntry> enchantingBlocks) -> {
                    configuration.enchantingBlocks = new ObjectOpenHashSet<>(enchantingBlocks);

                    configuration.enchantingBlockToEntry.clear();

                    for (final EnchantingBlockEntry entry : enchantingBlocks) {
                        configuration.enchantingBlockToEntry.put(entry.getBlock(), entry);
                    }
                },
                () -> ReferenceArrayList.wrap(defaultConfiguration.enchantingBlocks.toArray(new EnchantingBlockEntry[0])),
                EnchantmentListProvider.resetKey,
                false,
                true,
                false,
                EnchantingBlockListListEntry.EnchantingBlockListCell::new
            )}, 1);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
