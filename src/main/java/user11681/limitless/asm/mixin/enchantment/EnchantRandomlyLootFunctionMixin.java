package user11681.limitless.asm.mixin.enchantment;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.asm.access.EnchantmentAccess;

@Mixin(EnchantRandomlyLootFunction.class)
abstract class EnchantRandomlyLootFunctionMixin {
    @Redirect(method = "method_26266",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/util/math/MathHelper;nextInt(Ljava/util/Random;II)I"))
    private static int generateSaneLevel(Random random, int min, int max, ItemStack stack, Enchantment enchantment) {
        return max == min
            ? min
            : min + random.nextInt(Math.min(max, ((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel()) - min);
    }
}
