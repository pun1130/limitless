package net.auoeke.limitless.enchantment

import net.auoeke.limitless.asm.access.EnchantmentAccess
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.max
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
object EnchantmentUtil {
    const val INTERNAL_NAME = "net/auoeke/limitless/enchantment/EnchantmentUtil"
    private const val SUCCESS = 0
    private const val ADD = 1
    private const val CONFLICT = 2

    @JvmStatic
    fun getHighestSuitableLevel(power: Int, enchantment: Enchantment, entries: MutableList<EnchantmentLevelEntry?>) {
            var found = false
            var lastCandidate = 0
            val maxLevel = enchantment.maxLevel
            for (i in enchantment.minLevel..maxLevel) {
                if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                    lastCandidate = i
                    found = true
                    if ((enchantment as EnchantmentAccess).limitless_getOriginalMaxLevel() == 1) {
                        break
                    }
                } else {
                    break
                }
            }
            if (found) {
                entries.add(EnchantmentLevelEntry(enchantment, lastCandidate))
            }
        }

        fun mergeEnchantment(itemStack: ItemStack, enchantment: EnchantmentLevelEntry, mergeConflicts: Boolean) {
            val book = itemStack.item === Items.ENCHANTED_BOOK

            if (book || itemStack.hasEnchantments()) {
                if (this.tryMerge(itemStack, enchantment, mergeConflicts) == this.ADD) {
                    if (book) {
                        EnchantedBookItem.addEnchantment(itemStack, enchantment)
                    } else {
                        itemStack.addEnchantment(enchantment.enchantment, enchantment.level)
                    }
                }
            } else {
                itemStack.addEnchantment(enchantment.enchantment, enchantment.level)
            }
        }

        fun mergeEnchantment(itemStack: ItemStack, enchantment: Enchantment, level: Int, mergeConflicts: Boolean) {
            val book = itemStack.item === Items.ENCHANTED_BOOK

            if (book || itemStack.hasEnchantments()) {
                if (this.tryMerge(itemStack, enchantment, level, mergeConflicts) == this.ADD) {
                    if (book) {
                        EnchantedBookItem.addEnchantment(itemStack, EnchantmentLevelEntry(enchantment, level))
                    } else {
                        itemStack.addEnchantment(enchantment, level)
                    }
                }
            } else {
                itemStack.addEnchantment(enchantment, level)
            }
        }

        private fun tryMerge(itemStack: ItemStack, enchantment: EnchantmentLevelEntry, mergeConflicts: Boolean): Int = this.tryMerge(itemStack, enchantment.enchantment, enchantment.level, mergeConflicts)

        private fun tryMerge(itemStack: ItemStack, enchantment: Enchantment, level: Int, mergeConflicts: Boolean): Int {
            val book = itemStack.item === Items.ENCHANTED_BOOK
            val enchantments = when {
                book -> EnchantedBookItem.getEnchantmentNbt(itemStack)
                else -> itemStack.enchantments
            } as Any as Iterable<NbtCompound>
            var status = this.ADD

            for (enchantmentTag in enchantments) {
                if (Identifier(enchantmentTag.getString("id")) == Registry.ENCHANTMENT.getId(enchantment)) {
                    enchantmentTag.putInt("lvl", when (val tagLevel = enchantmentTag.getInt("lvl")) {
                        level -> min(tagLevel + 1, enchantment.maxLevel)
                        else -> max(tagLevel, level)
                    })

                    return this.SUCCESS
                } else if (!mergeConflicts && status != this.CONFLICT && !enchantment.canCombine(Registry.ENCHANTMENT[Identifier(enchantmentTag.getString("id"))])) {
                    status = this.CONFLICT
                }
            }

            return status
        }
}
