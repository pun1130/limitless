package user11681.limitless.asm.access;

import java.util.List;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;

public interface EnchantmentScreenHandlerAccess {
    List<EnchantmentLevelEntry> invokeGenerateEnchantments(ItemStack stack, int slot, int level);
}
