package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.Limitless
import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.enchantment.EnchantmentUtil
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.EnchantmentScreenHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(EnchantmentScreenHandler::class)
internal abstract class EnchantmentScreenHandlerMixin {
    @Redirect(method = ["method_17410"], at = At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V"))
    fun mergeEnchantments(stack: ItemStack, enchantment: Enchantment, level: Int) {
        EnchantmentUtil.mergeEnchantment(stack, enchantment, level, Configuration.instance.enchantment.conflicts.merge)
    }

    @Inject(method = ["generateEnchantments"], at = [At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;")])
    fun markItemForConflictRemoval(stack: ItemStack, slot: Int, level: Int, info: CallbackInfoReturnable<List<EnchantmentLevelEntry>>) {
        if (stack.hasEnchantments() && Configuration.instance.enchantment.reenchanting.removeConflicts) {
            Limitless.forConflictRemoval.add(stack)
        }
    }

    @Redirect(method = ["generateEnchantments"], at = At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    fun fixEnchantedBook(stack: ItemStack, item: Item): Boolean = stack.item === item || stack.item === Items.ENCHANTED_BOOK && Configuration.instance.enchantment.reenchanting.allowEnchantedBooks()
}
