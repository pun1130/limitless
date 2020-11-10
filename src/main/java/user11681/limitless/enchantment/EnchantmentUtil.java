package user11681.limitless.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.reflect.Classes;

public interface EnchantmentUtil {
    int SUCCESS = 0;
    int ADD = 1;
    int CONFLICT = 2;

    static void mergeEnchantment(final ItemStack itemStack, final EnchantmentLevelEntry enchantment, final boolean mergeConflicts) {
        final boolean book = itemStack.getItem() == Items.ENCHANTED_BOOK;

        if (book || itemStack.hasEnchantments()) {
            if (tryMerge(itemStack, enchantment, mergeConflicts) == ADD) {
                if (book) {
                    EnchantedBookItem.addEnchantment(itemStack, enchantment);
                } else {
                    itemStack.addEnchantment(enchantment.enchantment, enchantment.level);
                }
            }
        } else {
            itemStack.addEnchantment(enchantment.enchantment, enchantment.level);
        }
    }

    static void mergeEnchantment(final ItemStack itemStack, final Enchantment enchantment, final int level, final boolean mergeConflicts) {
        final boolean book = itemStack.getItem() == Items.ENCHANTED_BOOK;

        if (book || itemStack.hasEnchantments()) {
            if (tryMerge(itemStack, enchantment, level, mergeConflicts) == ADD) {
                if (book) {
                    EnchantedBookItem.addEnchantment(itemStack, new EnchantmentLevelEntry(enchantment, level));
                } else {
                    itemStack.addEnchantment(enchantment, level);
                }
            }
        } else {
            itemStack.addEnchantment(enchantment, level);
        }
    }

    static int tryMerge(final ItemStack itemStack, final EnchantmentLevelEntry enchantment, final boolean mergeConflicts) {
        return tryMerge(itemStack, enchantment.enchantment, enchantment.level, mergeConflicts);
    }

    static int tryMerge(final ItemStack itemStack, final Enchantment enchantment, final int level, final boolean mergeConflicts) {
        int status = ADD;

        final boolean book = itemStack.getItem() == Items.ENCHANTED_BOOK;
        final ListTag enchantments = book ? EnchantedBookItem.getEnchantmentTag(itemStack) : itemStack.getEnchantments();

        for (final CompoundTag enchantmentTag : Classes.<Iterable<CompoundTag>>cast(enchantments)) {
            if (new Identifier(enchantmentTag.getString("id")).equals(Registry.ENCHANTMENT.getId(enchantment))) {
                final int tagLevel = enchantmentTag.getInt("lvl");

                if (tagLevel == level) {
                    enchantmentTag.putInt("lvl", Math.min(tagLevel + 1, enchantment.getMaxLevel()));
                } else {
                    enchantmentTag.putInt("lvl", Math.max(tagLevel, level));
                }

                return SUCCESS;
            } else if (!mergeConflicts && status != CONFLICT && !enchantment.canCombine(Registry.ENCHANTMENT.get(new Identifier(enchantmentTag.getString("id"))))) {
                status = CONFLICT;
            }
        }

        return status;
    }
}
