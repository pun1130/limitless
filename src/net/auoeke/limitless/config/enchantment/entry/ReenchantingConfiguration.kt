package net.auoeke.limitless.config.enchantment.entry

class ReenchantingConfiguration {
    var enabled = true
    var removeConflicts = true
    var allowEnchantedBooks = true
    var allowEquipment = true

    fun allowEnchantedBooks(): Boolean = enabled && allowEnchantedBooks
    fun allowEquipment(): Boolean = enabled && allowEquipment
}
