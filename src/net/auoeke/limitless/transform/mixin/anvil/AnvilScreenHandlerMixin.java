package net.auoeke.limitless.transform.mixin.anvil;

import net.auoeke.limitless.config.Configuration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AnvilScreenHandler.class, priority = -1000)
abstract class AnvilScreenHandlerMixin {
    @Inject(method = "getNextCost", at = @At("HEAD"), cancellable = true)
    private static void getNextCost(int cost, CallbackInfoReturnable<Integer> info) {
        if (Configuration.instance.getAnvil().getFixedCost()) {
            info.setReturnValue(0);
        }
    }

    @Redirect(method = "updateResult",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean configureAcceptableItem(Enchantment enchantment, ItemStack stack) {
        return enchantment.isAcceptableItem(stack) || Configuration.instance.getAnvil().getMergeIncompatible();

    }

    @Redirect(method = "updateResult",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;canCombine(Lnet/minecraft/enchantment/Enchantment;)Z"))
    public boolean configureConflict(Enchantment enchantment, Enchantment other) {
        return enchantment.canCombine(other) || Configuration.instance.getAnvil().getMergeConflicts();

    }

    @ModifyConstant(method = "updateResult",
                    constant = @Constant(intValue = 40))
    public int modifyLimit(int originalLimit) {
        return Configuration.instance.getAnvil().getLevelLimit();
    }
}
