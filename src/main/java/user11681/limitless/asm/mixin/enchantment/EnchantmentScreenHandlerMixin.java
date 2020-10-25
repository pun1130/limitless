package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.RadiusConfiguration;
import user11681.limitless.enchantment.EnchantmentUtil;

@SuppressWarnings({"unused", "RedundantSuppression", "UnresolvedMixinReference"})
@Mixin(EnchantmentScreenHandler.class)
abstract class EnchantmentScreenHandlerMixin {
    @Redirect(method = "method_17410",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V"))
    public void mergeEnchantments(final ItemStack stack, final Enchantment enchantment, final int level) {
        EnchantmentUtil.mergeEnchantment(stack, enchantment, level);
    }

    @Redirect(method = "method_17410",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/EnchantedBookItem;addEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentLevelEntry;)V"))
    public void mergeEnchantments(final ItemStack stack, final EnchantmentLevelEntry enchantment) {
        EnchantmentUtil.mergeEnchantment(stack, enchantment);
    }

    private static int limitless_scanEnchantingBlocks(final World world, final BlockPos blockPos) {
        final RadiusConfiguration horizontalRadiusRange = LimitlessConfiguration.instance.enchantingBlockRadiusXZ;
        final RadiusConfiguration verticalRadiusRange = LimitlessConfiguration.instance.enchantingBlockRadiusY;
        final int maxVerticalRadius = verticalRadiusRange.max;
        int maxHorizontalRadius = horizontalRadiusRange.max;
        int bookshelfCount = 0;

        for (int k = -1; k <= 1; k += 2) {
            final int remainder = k % 2;

            for (int verticalRadius = verticalRadiusRange.min; verticalRadius <= maxVerticalRadius; verticalRadius++) {
                for (int horizontalRadius = horizontalRadiusRange.min; horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                    final int end = k * horizontalRadius;

                    for (int j = -horizontalRadius; j <= horizontalRadius; j++) {
                        final int displacement = k * j;

                        if (world.getBlockState(blockPos.add(displacement, verticalRadius, end)).isOf(Blocks.BOOKSHELF)) {
                            ++bookshelfCount;
                        }

                        if (j != -horizontalRadius && j != horizontalRadius) {
                            if (world.getBlockState(blockPos.add(end, verticalRadius, displacement)).isOf(Blocks.BOOKSHELF)) {
                                ++bookshelfCount;
                            }
                        }
                    }
                }
            }
        }

        return bookshelfCount;
    }
}
