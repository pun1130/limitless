package net.auoeke.limitless.config.enchantment.provider

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ReferenceArrayList
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.auoeke.limitless.config.enchantment.entry.EnchantmentEntry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.enchantment.Enchantment
import net.minecraft.text.TranslatableText
import java.lang.reflect.Field

@Suppress("UNCHECKED_CAST")
@Environment(EnvType.CLIENT)
object EnchantmentListProvider : GuiProvider {
    private val resetKey = TranslatableText("text.cloth-config.reset_value")

    override fun get(i13n: String, field: Field, config: Any, defaults: Any, guiRegistry: GuiRegistryAccess): List<AbstractConfigListEntry<*>> {
        val entries = ReferenceArrayList<AbstractConfigListEntry<*>>()
        val listBuilder = SubCategoryBuilder(this.resetKey, TranslatableText("config.limitless.enchantments"))

        for (entry in field.apply {trySetAccessible()}[config] as ObjectLinkedOpenHashSet<EnchantmentEntry>) {
            val enchantment = entry.enchantment

            if (enchantment !== null) {
                val builder = SubCategoryBuilder(this.resetKey, TranslatableText((enchantment as Enchantment).translationKey))
                builder.add(0, IntFieldBuilder(this.resetKey, TranslatableText("config.limitless.maxLevel"), entry.maxLevel)
                    .setDefaultValue(enchantment.limitless_getOriginalMaxLevel())
                    .setSaveConsumer {level ->
                        enchantment.limitless_setMaxLevel(level)
                        entry.maxLevel = level
                    }.build()
                )

                builder.add(1, BooleanToggleBuilder(this.resetKey, TranslatableText("config.limitless.useGlobalMaxLevel"), entry.useGlobalMaxLevel)
                    .setDefaultValue(false)
                    .setSaveConsumer {useGlobalMaxLevel ->
                        enchantment.limitless_setUseGlobalMaxLevel(useGlobalMaxLevel)
                        entry.useGlobalMaxLevel = useGlobalMaxLevel
                    }.build()
                )

                listBuilder.add(builder.build())
            }
        }

        entries.add(listBuilder.build())

        return entries
    }
}
