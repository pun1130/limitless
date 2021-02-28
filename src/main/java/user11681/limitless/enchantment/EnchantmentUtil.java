package user11681.limitless.enchantment;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface EnchantmentUtil {
    String INTERNAL_NAME = "user11681/limitless/enchantment/EnchantmentUtil";

    int SUCCESS = 0;
    int ADD = 1;
    int CONFLICT = 2;

    @SuppressWarnings("unused") // invoked by bytecode manipulation
    static void getHighestSuitableLevel(int power, Enchantment enchantment, List<EnchantmentLevelEntry> entries) {
        boolean found = false;
        int lastCandidate = 0;
        int maxLevel = enchantment.getMaxLevel();

        for (int i = enchantment.getMinLevel(); i <= maxLevel; i++) {
            if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                lastCandidate = i;
                found = true;

                if (((EnchantmentWrapper) enchantment).originalMaxLevel() == 1) {
                    break;
                }
            } else {
                break;
            }
        }

        if (found) {
            entries.add(new EnchantmentLevelEntry(enchantment, lastCandidate));
        }
    }

    static void mergeEnchantment(ItemStack itemStack, EnchantmentLevelEntry enchantment, boolean mergeConflicts) {
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

    static void mergeEnchantment(ItemStack itemStack, Enchantment enchantment, int level, boolean mergeConflicts) {
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

    static int tryMerge(ItemStack itemStack, EnchantmentLevelEntry enchantment, boolean mergeConflicts) {
        return tryMerge(itemStack, enchantment.enchantment, enchantment.level, mergeConflicts);
    }

    @SuppressWarnings("unchecked")
    static int tryMerge(ItemStack itemStack, Enchantment enchantment, int level, boolean mergeConflicts) {
        boolean book = itemStack.getItem() == Items.ENCHANTED_BOOK;
        Iterable<CompoundTag> enchantments = (Iterable<CompoundTag>) (Object) (
           book
            ? EnchantedBookItem.getEnchantmentTag(itemStack)
            : itemStack.getEnchantments()
        );

        int status = ADD;

        for (CompoundTag enchantmentTag : enchantments) {
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
