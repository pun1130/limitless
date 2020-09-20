package user11681.limitless.tag;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Collection;
import java.util.Random;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.RadiusConfiguration;

public interface EnchantingBlocks {
    Tag<Block> tag = TagRegistry.block(new Identifier("c", "enchanting_blocks"));

    static int calculateRequiredExperienceLevel(final Random random, final int slotIndex, float enchantingPower, final ItemStack stack) {
        if (stack.getItem().getEnchantability() <= 0) {
            return 0;
        }

        if (enchantingPower > LimitlessConfiguration.instance.maxEnchantingPower) {
            enchantingPower = LimitlessConfiguration.instance.maxEnchantingPower;
        }

        float level = random.nextInt(8) + 1 + enchantingPower / 4 + random.nextInt((int) enchantingPower / 2 + 1);

        if (slotIndex == 0) {
            return (int) Math.max(level / 3, 1);
        } else {
            return (int) (slotIndex == 1 ? level * 2 / 3 + 1 : Math.max(level, enchantingPower));
        }
    }

    static float countEnchantingPower(final EnchantingBlockEntry... enchantingBlocks) {
        float power = 0;

        for (final EnchantingBlockEntry enchantingBlock : enchantingBlocks) {
            power += enchantingBlock.power;
        }

        return power;
    }

    static float countEnchantingPower(final World world, final BlockPos enchantingTablePos) {
        float power = 0;

        for (final EnchantingBlockEntry enchantingBlock : searchEnchantingBlocks(world, enchantingTablePos)) {
            power += enchantingBlock.power;
        }

        return power;
    }

    static float countEnchantingPower(final Collection<EnchantingBlockEntry> enchantingBlocks) {
        float power = 0;

        for (final EnchantingBlockEntry enchantingBlock : enchantingBlocks) {
            power += enchantingBlock.power;
        }

        return power;
    }

    static ReferenceArrayList<EnchantingBlockEntry> searchEnchantingBlocks(final World world, final BlockPos enchantingTablePos) {
        final ReferenceArrayList<EnchantingBlockEntry> enchantingBlocks = ReferenceArrayList.wrap(new EnchantingBlockEntry[20], 0);
        final RadiusConfiguration horizontalRadiusRange = LimitlessConfiguration.instance.enchantingBlockRadiusXZ;
        final RadiusConfiguration verticalRadiusRange = LimitlessConfiguration.instance.enchantingBlockRadiusY;
        final int maxVerticalRadius = verticalRadiusRange.max;
        int maxHorizontalRadius = horizontalRadiusRange.max;
        int verticalRadius;
        int horizontalRadius;
        int end;
        int displacement;
        BlockState blockState;

        for (int k = -1; k <= 1; k += 2) {
            for (verticalRadius = verticalRadiusRange.min; verticalRadius <= maxVerticalRadius; verticalRadius++) {
                for (horizontalRadius = horizontalRadiusRange.min; horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                    end = k * horizontalRadius;

                    for (int distance = -horizontalRadius; distance <= horizontalRadius; distance++) {
                        displacement = k * distance;
                        blockState = world.getBlockState(enchantingTablePos.add(displacement, verticalRadius, end));

                        if (blockState.isIn(tag)) {
                            enchantingBlocks.add(LimitlessConfiguration.instance.enchantingBlockToEntry.get(blockState.getBlock()));
                        }

                        if (distance != -horizontalRadius && distance != horizontalRadius) {
                            blockState = world.getBlockState(enchantingTablePos.add(end, verticalRadius, displacement));

                            if (blockState.isIn(tag)) {
                                enchantingBlocks.add(LimitlessConfiguration.instance.enchantingBlockToEntry.get(blockState.getBlock()));
                            }
                        }
                    }
                }
            }
        }

        return enchantingBlocks;
    }
}
