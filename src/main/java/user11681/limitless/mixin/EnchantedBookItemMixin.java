package user11681.limitless.mixin;

import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantedBookItem.class)
public abstract class EnchantedBookItemMixin {
    @Redirect(method = "addEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putShort(Ljava/lang/String;S)V"))
    private static void removeLimit(final CompoundTag compoundTag, final String key, final short value, final ItemStack itemStack, final InfoEnchantment info) {
        compoundTag.putInt("lvl", info.level);
    }
}
