package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.block.Blocks;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.RadiusConfiguration;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Mixin(EnchantmentScreenHandler.class)
abstract class EnchantmentScreenHandlerMixin {
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
