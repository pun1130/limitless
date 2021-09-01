package net.auoeke.limitless.config.enchantment

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import net.auoeke.limitless.config.enchantment.annotation.EnchantmentList
import net.auoeke.limitless.config.enchantment.entry.*
import net.auoeke.limitless.config.enchantment.entry.normalization.EnchantmentNormalizationEntry
import net.minecraft.util.registry.Registry

class EnchantmentConfiguration : ConfigData {
    var globalMaxLevel = Int.MAX_VALUE
    var allowTreasure = true
    var revealEnchantments = false

    @CollapsibleObject
    var conflicts = EnchantingConflicts()

    @CollapsibleObject
    var normalization = EnchantmentNormalizationEntry()

    @CollapsibleObject
    var reenchanting = ReenchantingConfiguration()

    @CollapsibleObject
    var enchantingBlocks = EnchantingBlockConfiguration()

    @CollapsibleObject
    var particles = EnchantmentParticleConfiguration()

    @EnchantmentList
    var maxLevels: ObjectLinkedOpenHashSet<EnchantmentEntry>? = null

    @Suppress("SENSELESS_COMPARISON")
    override fun validatePostLoad() {
        val oldMaxLevels = this.maxLevels
        this.maxLevels = Registry.ENCHANTMENT
            .ids
            .sorted()
            .mapTo(ObjectLinkedOpenHashSet()) {EnchantmentEntry(it)}

        if (oldMaxLevels != null) {
            for (configuration in oldMaxLevels) {
                if (this.maxLevels!!.remove(configuration)) {
                    this.maxLevels!!.add(configuration)

                    configuration.enchantment!!.also {
                        it.limitless_setMaxLevel(configuration.maxLevel)
                        it.limitless_setUseGlobalMaxLevel(configuration.useGlobalMaxLevel)
                    }
                }
            }
        }

        this.enchantingBlocks.validatePostLoad()
    }

    companion object {
        const val INTERNAL_NAME = "net/auoeke/limitless/config/enchantment/EnchantmentConfiguration"
        const val DESCRIPTOR = "L${this.INTERNAL_NAME};"
    }
}
