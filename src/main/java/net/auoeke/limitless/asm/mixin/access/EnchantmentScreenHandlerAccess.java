package net.auoeke.limitless.asm.mixin.access;

import java.util.List;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EnchantmentScreenHandler.class)
public interface EnchantmentScreenHandlerAccess {
    @Invoker("generateEnchantments")
    List<EnchantmentLevelEntry> invokeGenerateEnchantments(ItemStack stack, int slot, int level);
}
