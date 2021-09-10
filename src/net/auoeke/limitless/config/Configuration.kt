package net.auoeke.limitless.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.Config.Gui.Background
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject
import net.auoeke.limitless.Limitless
import net.auoeke.limitless.config.anvil.AnvilConfiguration
import net.auoeke.limitless.config.enchantment.EnchantmentConfiguration

@Config(name = Limitless.ID)
@Background("textures/block/andesite.png")
class Configuration : ConfigData {
    @JvmField
    @Category(ENCHANTMENT)
    @TransitiveObject
    var enchantment = EnchantmentConfiguration()

    @Category(ANVIL)
    @TransitiveObject
    var anvil = AnvilConfiguration()

    override fun validatePostLoad() {
        this.enchantment.validatePostLoad()
    }

    companion object {
        const val INTERNAL_NAME: String = "net/auoeke/limitless/config/Configuration"
        const val DESCRIPTOR: String = "L$INTERNAL_NAME;"
        const val ENCHANTMENT: String = "default"
        const val ANVIL: String = "anvil"

        lateinit var holder: ConfigHolder<Configuration>
        lateinit var instance: Configuration

        fun refresh() {
            instance = holder.get()
        }
    }
}
