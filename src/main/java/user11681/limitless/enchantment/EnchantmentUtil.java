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

        boolean conflict = false;

        for (final CompoundTag enchantmentTag : Classes.<Iterable<CompoundTag>>cast(itemStack.getEnchantments())) {
            if (new Identifier(enchantmentTag.getString("id")).equals(Registry.ENCHANTMENT.getId(enchantment))) {
                final int tagLevel = enchantmentTag.getInt("lvl");;

                if (tagLevel == level) {
                    enchantmentTag.putInt("lvl", Math.min(tagLevel + 1, enchantment.getMaxLevel()));
                } else {
                    enchantmentTag.putInt("lvl", Math.max(tagLevel, level));
                }

                return;
            } else if (!conflict && !enchantment.canCombine(Registry.ENCHANTMENT.get(new Identifier(enchantmentTag.getString("id"))))) {
                conflict = true;
            }
        }

        if (!conflict) {
            itemStack.addEnchantment(enchantment, level);
        }
    }
}
