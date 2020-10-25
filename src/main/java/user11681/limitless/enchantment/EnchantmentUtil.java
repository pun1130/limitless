package user11681.limitless.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.reflect.Classes;

public interface EnchantmentUtil {
    static void mergeEnchantment(final ItemStack itemStack, final EnchantmentLevelEntry enchantment) {
        mergeEnchantment(itemStack, enchantment.enchantment, enchantment.level);
    }

    /**
     * merge enchantments in the same way as anvil screen handlers do so
     */
    static void mergeEnchantment(final ItemStack itemStack, final Enchantment enchantment, final int level) {
        if (!itemStack.hasEnchantments()) {
            itemStack.addEnchantment(enchantment, level);

            return;
        }

        final Iterable<CompoundTag> enchantments = Classes.cast(itemStack.getEnchantments());

        for (final CompoundTag enchantmentTag : enchantments) {
            if (Registry.ENCHANTMENT.get(new Identifier(enchantmentTag.getString("id"))) == enchantment) {
                final int tagLevel = enchantmentTag.getInt("lvl");

                if (tagLevel == level) {
                    enchantmentTag.putInt("lvl", tagLevel + 1);
                } else {
                    enchantmentTag.putInt("lvl", Math.max(tagLevel, level));
                }

                return;
            }
        }

        itemStack.addEnchantment(enchantment, level);
    }
}
