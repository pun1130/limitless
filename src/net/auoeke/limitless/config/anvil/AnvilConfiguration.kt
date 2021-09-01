package net.auoeke.limitless.config.anvil

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import net.auoeke.limitless.config.anvil.entry.AnvilNormalizationEntry

class AnvilConfiguration {
    var mergeConflicts = true
    var mergeIncompatible = false
    var fixedCost = true
    var levelLimit = Int.MAX_VALUE

    @CollapsibleObject
    var normalization = AnvilNormalizationEntry()
}
