package net.auoeke.limitless.config.anvil.entry

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler
import net.auoeke.limitless.config.common.CostDisplay

class AnvilNormalizationEntry {
    var enabled = true

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    var display = CostDisplay.REPLACE
}
