package net.auoeke.limitless.config

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import java.lang.reflect.Field

object ExcludingNonprovider : GuiProvider {
    override fun get(i18n: String, field: Field, config: Any, default: Any, registry: GuiRegistryAccess?): List<AbstractConfigListEntry<Any>> = listOf()
}
