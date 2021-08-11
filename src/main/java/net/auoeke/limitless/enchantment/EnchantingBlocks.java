package net.auoeke.limitless.enchantment;

import java.util.Random;
import net.auoeke.limitless.config.LimitlessConfiguration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

@SuppressWarnings("unused")
public interface EnchantingBlocks {
    String INTERNAL_NAME = "net/auoeke/limitless/enchantment/EnchantingBlocks";

    Tag<Block> tag = new EnchantingBlockTag();

    static int calculateRequiredExperienceLevel(Random random, int slotIndex, float enchantingPower, ItemStack stack) {
        if (stack.getItem().getEnchantability() <= 0) {
            return 0;
        }

        int maxEnchantingPower = LimitlessConfiguration.instance.enchantment.enchantingBlocks.maxPower;

        if (enchantingPower > maxEnchantingPower) {
            enchantingPower = maxEnchantingPower;
        }

        float level = random.nextInt(8) + 1 + enchantingPower / 4 + random.nextInt((int) enchantingPower / 2 + 1);

        return slotIndex == 0
            ? (int) Math.max(level / 3, 1)
            : (int) (slotIndex == 1
                ? level * 2 / 3 + 1
                : Math.max(level, enchantingPower));
    }

    static float countEnchantingPower(World world, BlockPos enchantingTablePos) {
        var horizontalRadiusRange = LimitlessConfiguration.instance.enchantment.enchantingBlocks.radius.xz;
        var verticalRadiusRange = LimitlessConfiguration.instance.enchantment.enchantingBlocks.radius.y;
        var power = new MutableFloat();

        forBlockInRange(world, enchantingTablePos, horizontalRadiusRange.min, horizontalRadiusRange.max, verticalRadiusRange.min, verticalRadiusRange.max, (tableBlockState, pos, dX, dY, dZ) ->
            power.add(LimitlessConfiguration.instance.enchantment.enchantingBlocks.enchantingPower(tableBlockState.getBlock()))
        );

        return power.floatValue();
    }

    static void forBlockInRange(World world, BlockPos center, int minHorizontalRadius, int maxHorizontalRadius, int minVerticalRadius, int maxVerticalRadius, EnchantingBlockConsumer action) {
        int verticalRadius;
        int horizontalRadius;
        int end;
        int displacement;
        BlockPos blockPos;
        BlockState blockState;

        for (int k = -1; k <= 1; k += 2) {
            for (verticalRadius = minVerticalRadius; verticalRadius <= maxVerticalRadius; verticalRadius++) {
                for (horizontalRadius = minHorizontalRadius; horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                    end = k * horizontalRadius;

                    for (int distance = -horizontalRadius; distance <= horizontalRadius; distance++) {
                        displacement = k * distance;
                        blockState = world.getBlockState(blockPos = center.add(displacement, verticalRadius, end));

                        action.accept(blockState, blockPos, displacement, verticalRadius, end);

                        if (distance != -horizontalRadius && distance != horizontalRadius) {
                            blockState = world.getBlockState(blockPos = center.add(end, verticalRadius, displacement));

                            action.accept(blockState, blockPos, end, verticalRadius, displacement);
                        }
                    }
                }
            }
        }
    }
}
