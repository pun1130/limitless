package net.auoeke.limitless.config.enchantment.entry

class ReenchantingConfiguration {
    var enabled = true
    var removeConflicts = true
    var allowEnchantedBooks = true
    var allowEquipment = true

    fun allowEnchantedBooks(): Boolean = this.enabled && this.allowEnchantedBooks
    fun allowEquipment(): Boolean = this.enabled && this.allowEquipment
}
