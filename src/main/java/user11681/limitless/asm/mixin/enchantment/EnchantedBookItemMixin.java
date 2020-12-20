package user11681.limitless.asm.mixin.enchantment;

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
import user11681.limitless.asm.access.EnchantmentAccess;
import user11681.limitless.config.LimitlessConfiguration;

@Mixin(EnchantedBookItem.class)
abstract class EnchantedBookItemMixin extends Item {
    @Redirect(method = "appendStacks",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int fixStacks(final Enchantment enchantment) {
        return ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel();
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @ModifyConstant(method = "isEnchantable",
                    constant = @Constant(intValue = 0))
    public int makeReenchantable(final int previous) {
        return LimitlessConfiguration.instance.enchantment.reenchanting.allowEnchantedBooks() ? 1 : previous;
    }

    @Intrinsic
    @Override
    public int getEnchantability() {
        return LimitlessConfiguration.instance.enchantment.reenchanting.allowEnchantedBooks() ? Items.BOOK.getEnchantability() : 0;
    }

    public EnchantedBookItemMixin(final Settings settings) {
        super(settings);
    }
}
