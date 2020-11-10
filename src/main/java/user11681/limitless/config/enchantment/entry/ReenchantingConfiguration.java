package user11681.limitless.config.enchantment.entry;

public class ReenchantingConfiguration {
    public boolean enabled = true;
    public boolean removeConflicts = true;
    public boolean allowEnchantedBooks = true;
    public boolean allowEquipment = true;

    public boolean allowEnchantedBooks() {
        return this.enabled && this.allowEnchantedBooks;
    }

    public boolean allowEquipment() {
        return this.enabled && this.allowEquipment;
    }
}
