package net.auoeke.limitless.transform.mixin.enchantment;

import net.auoeke.limitless.transform.access.EnchantmentAccess;
import net.auoeke.limitless.config.Configuration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantedBookItem.class)
abstract class EnchantedBookItemMixin extends Item {
    @Redirect(method = "appendStacks",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int appendOriginalLevelsToGroup(Enchantment enchantment) {
        return ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel();
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @ModifyConstant(method = "isEnchantable",
                    constant = @Constant(intValue = 0))
    public int makeReenchantable(int previous) {
        return Configuration.instance.enchantment.getReenchanting().allowEnchantedBooks() ? 1 : previous;
    }

    @Intrinsic
    @Override
    public int getEnchantability() {
        return Configuration.instance.enchantment.getReenchanting().allowEnchantedBooks() ? Items.BOOK.getEnchantability() : 0;
    }

    public EnchantedBookItemMixin(Settings settings) {
        super(settings);
    }
}
