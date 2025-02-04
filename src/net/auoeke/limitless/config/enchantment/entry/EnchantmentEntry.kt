package net.auoeke.limitless.config.enchantment.entry

import net.auoeke.limitless.transform.access.EnchantmentAccess
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class EnchantmentEntry(identifier: Identifier) {
    @Transient
    var enchantment: EnchantmentAccess? = null
        get() = field ?: (Registry.ENCHANTMENT[Identifier(identifier)] as EnchantmentAccess).also {field = it}

    var maxLevel = 0
    var useGlobalMaxLevel = false
    private val identifier: String = identifier.toString()

    init {
        maxLevel = (enchantment as Enchantment).maxLevel
        useGlobalMaxLevel = false
    }

    override fun hashCode(): Int = identifier.hashCode()
    override fun equals(other: Any?): Boolean = other is EnchantmentEntry && other.hashCode() == this.hashCode()
}
