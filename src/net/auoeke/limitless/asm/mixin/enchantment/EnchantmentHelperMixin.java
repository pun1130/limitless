package net.auoeke.limitless.asm.mixin.enchantment;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.auoeke.limitless.Limitless;
import net.auoeke.limitless.config.Configuration;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
    @ModifyConstant(method = "calculateRequiredExperienceLevel",
                    constant = @Constant(intValue = 15))
    private static int modifyMaxBookshelves(int previousMaxBookshelves) {
        return Configuration.instance.enchantment.getEnchantingBlocks().getMaxBlocks();
    }

    @Redirect(method = "getPossibleEntries",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;isTreasure()Z"))
    private static boolean allowTreasure(Enchantment enchantment) {
        return Configuration.instance.enchantment.getAllowTreasure() ? enchantment.isCursed() : enchantment.isTreasure();
    }

    @Redirect(method = "getPossibleEntries",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item replaceEnchantedBook(ItemStack stack) {
        Item item = stack.getItem();

        return item == Items.ENCHANTED_BOOK && Configuration.instance.enchantment.getReenchanting().allowEnchantedBooks() ? Items.BOOK : item;
    }

    @ModifyVariable(method = "generateEnchantments",
                    at = @At(value = "INVOKE_ASSIGN",
                             target = "Lnet/minecraft/enchantment/EnchantmentHelper;getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;"),
                    ordinal = 1)
    private static List<EnchantmentLevelEntry> removeConflictsWithItem(List<EnchantmentLevelEntry> possibleEnchantments, Random random, ItemStack itemStack) {
        if (Limitless.INSTANCE.getForConflictRemoval().remove(itemStack)) {
            ListIterator<EnchantmentLevelEntry> iterator = possibleEnchantments.listIterator();

            outer:
            while (iterator.hasNext()) {
                Enchantment enchantment = iterator.next().enchantment;
                boolean foundConflict = false;

                for (NbtElement enchantmentTag : itemStack.getEnchantments()) {
                    Enchantment other = Registry.ENCHANTMENT.get(new Identifier(((NbtCompound) enchantmentTag).getString("id")));

                    if (enchantment == other) {
                        continue outer;
                    }

                    if (!enchantment.canCombine(other)) {
                        foundConflict = true;
                    }
                }

                if (foundConflict) {
                    iterator.remove();
                }
            }
        }

        return possibleEnchantments;
    }

    @Redirect(method = "generateEnchantments",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/EnchantmentHelper;removeConflicts(Ljava/util/List;Lnet/minecraft/enchantment/EnchantmentLevelEntry;)V"))
    private static void keepConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
        if (Configuration.instance.enchantment.getConflicts().getGenerate()) {
            possibleEntries.removeIf(entry -> entry.enchantment == pickedEntry.enchantment);
        } else {
            EnchantmentHelper.removeConflicts(possibleEntries, pickedEntry);
        }
    }

    @Redirect(method = "createNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putShort(Ljava/lang/String;S)V"))
    private static void dontTruncateLevel(NbtCompound tag, String key, short truncated, Identifier id, int level) {
        tag.putInt(key, level);
    }

    @Redirect(method = "writeLevelToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putShort(Ljava/lang/String;S)V"))
    private static void writeUntruncatedLevel(NbtCompound tag, String key, short truncated, NbtCompound tag1, int level) {
        tag.putInt(key, level);
    }
}
