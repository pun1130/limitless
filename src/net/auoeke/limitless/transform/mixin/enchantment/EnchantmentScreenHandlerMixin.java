package net.auoeke.limitless.transform.mixin.enchantment;

import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.enchantment.EnchantmentUtil;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.auoeke.limitless.Limitless;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(EnchantmentScreenHandler.class)
abstract class EnchantmentScreenHandlerMixin {
    @Redirect(method = "method_17410", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V"))
    public void mergeEnchantments(ItemStack stack, Enchantment enchantment, int level) {
        EnchantmentUtil.INSTANCE.mergeEnchantment(stack, enchantment, level, Configuration.instance.enchantment.getConflicts().getMerge());
    }

    @Inject(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;"))
    public void markItemForConflictRemoval(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> info) {
        if (stack.hasEnchantments() && Configuration.instance.enchantment.getReenchanting().getRemoveConflicts()) {
            Limitless.INSTANCE.getForConflictRemoval().add(stack);
        }
    }

    @Redirect(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    public boolean fixEnchantedBook(ItemStack stack, Item item) {
        return stack.getItem() == item || stack.getItem() == Items.ENCHANTED_BOOK && Configuration.instance.enchantment.getReenchanting().allowEnchantedBooks();
    }
}
