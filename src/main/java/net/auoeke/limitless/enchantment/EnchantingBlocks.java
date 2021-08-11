package net.auoeke.limitless.enchantment;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Collection;
import java.util.Random;
import net.auoeke.limitless.config.LimitlessConfiguration;
import net.auoeke.limitless.config.enchantment.entry.radius.HorizontalRadius;
import net.auoeke.limitless.config.enchantment.entry.radius.VerticalRadius;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EnchantingBlocks {
    String INTERNAL_NAME = "net/auoeke/limitless/enchantment/EnchantingBlocks";

    Tag<Block> tag = new EnchantingBlockTag();

    static int calculateRequiredExperienceLevel(Random random, int slotIndex, float enchantingPower, ItemStack stack) {
        if (stack.getItem().getEnchantability() <= 0) {
            return 0;
        }

        final int maxEnchantingPower = LimitlessConfiguration.instance.enchantment.enchantingBlocks.maxPower;

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

    static float countEnchantingPower(EnchantingBlockEntry... enchantingBlocks) {
        float power = 0;

        for (EnchantingBlockEntry enchantingBlock : enchantingBlocks) {
            power += enchantingBlock.power;
        }

        return power;
    }

    static float countEnchantingPower(World world, BlockPos enchantingTablePos) {
        float power = 0;

        for (EnchantingBlockEntry enchantingBlock : searchEnchantingBlocks(world, enchantingTablePos)) {
            power += enchantingBlock.power;
        }

        return power;
    }

    static float countEnchantingPower(Collection<EnchantingBlockEntry> enchantingBlocks) {
        float power = 0;

        for (EnchantingBlockEntry enchantingBlock : enchantingBlocks) {
            power += enchantingBlock.power;
        }

        return power;
    }

    static ReferenceArrayList<EnchantingBlockEntry> searchEnchantingBlocks(World world, BlockPos enchantingTablePos) {
        final ReferenceArrayList<EnchantingBlockEntry> enchantingBlocks = ReferenceArrayList.wrap(new EnchantingBlockEntry[20], 0);

        forEnchantingBlockInRange(world, enchantingTablePos, (BlockState enchantingBlockState, BlockPos pos, int dX, int dY, int dZ) ->
            enchantingBlocks.add(LimitlessConfiguration.instance.enchantment.enchantingBlockToEntry.get(enchantingBlockState.getBlock()))
        );

        return enchantingBlocks;
    }

    static void forEnchantingBlockInRange(World world, BlockPos center, EnchantingBlockConsumer action) {
        final HorizontalRadius horizontalRadiusRange = LimitlessConfiguration.instance.enchantment.enchantingBlocks.radius.xz;
        final VerticalRadius verticalRadiusRange = LimitlessConfiguration.instance.enchantment.enchantingBlocks.radius.y;

        forEnchantingBlockInRange(world, center, horizontalRadiusRange.min, horizontalRadiusRange.max, verticalRadiusRange.min, verticalRadiusRange.max, action);
    }

    static void forEnchantingBlockInRange(World world, BlockPos center, int minHorizontalRadius, int maxHorizontalRadius, int minVerticalRadius, int maxVerticalRadius, EnchantingBlockConsumer action) {
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

                        if (blockState.isIn(tag)) {
                            action.accept(blockState, blockPos, displacement, verticalRadius, end);
                        }

                        if (distance != -horizontalRadius && distance != horizontalRadius) {
                            blockState = world.getBlockState(blockPos = center.add(end, verticalRadius, displacement));

                            if (blockState.isIn(tag)) {
                                action.accept(blockState, blockPos, end, verticalRadius, displacement);
                            }
                        }
                    }
                }
            }
        }
    }
}
