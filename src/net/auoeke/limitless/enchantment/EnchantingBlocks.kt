package net.auoeke.limitless.enchantment

import net.auoeke.limitless.config.Configuration
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import kotlin.math.max
import kotlin.math.min

object EnchantingBlocks {
    const val INTERNAL_NAME = "net/auoeke/limitless/enchantment/EnchantingBlocks"

    @Suppress("NAME_SHADOWING")
    @JvmStatic
    fun calculateRequiredExperienceLevel(random: Random, slotIndex: Int, enchantingPower: Float, stack: ItemStack): Int {
        if (stack.item.enchantability <= 0) {
            return 0
        }

        val enchantingPower = min(enchantingPower, Configuration.instance.enchantment.enchantingBlocks.maxPower.toFloat())
        val level = random.nextInt(8) + 1 + enchantingPower / 4 + random.nextInt(enchantingPower.toInt() / 2 + 1)

        return when (slotIndex) {
            0 -> max(level / 3, 1f).toInt()
            else -> when (slotIndex) {
                1 -> level * 2 / 3 + 1
                else -> max(level, enchantingPower)
            }.toInt()
        }
    }

    @JvmStatic
    fun countEnchantingPower(world: World, enchantingTablePos: BlockPos): Float {
        val config = Configuration.instance.enchantment.enchantingBlocks
        val horizontalRadiusRange = config.radius.xz
        val verticalRadiusRange = config.radius.y
        var power = 0F

        forBlockInRange(world, enchantingTablePos, horizontalRadiusRange.min, horizontalRadiusRange.max, verticalRadiusRange.min, verticalRadiusRange.max) {tableBlockState, _, _, _, _ ->
            power += config.enchantingPower(tableBlockState.block)
        }

        return power
    }

    private fun forBlockInRange(world: World, center: BlockPos, minHorizontalRadius: Int, maxHorizontalRadius: Int, minVerticalRadius: Int, maxVerticalRadius: Int, action: EnchantingBlockConsumer) {
        var verticalRadius: Int
        var horizontalRadius: Int
        var end: Int
        var displacement: Int
        var blockPos: BlockPos
        var blockState: BlockState

        for (k in -1..1 step 2) {
            verticalRadius = minVerticalRadius

            while (verticalRadius <= maxVerticalRadius) {
                horizontalRadius = minHorizontalRadius

                while (horizontalRadius <= maxHorizontalRadius) {
                    end = k * horizontalRadius

                    for (distance in -horizontalRadius..horizontalRadius) {
                        displacement = k * distance
                        blockState = world.getBlockState(center.add(displacement, verticalRadius, end).also {blockPos = it})
                        action.accept(blockState, blockPos, displacement, verticalRadius, end)

                        if (distance != -horizontalRadius && distance != horizontalRadius) {
                            blockState = world.getBlockState(center.add(end, verticalRadius, displacement).also {blockPos = it})
                            action.accept(blockState, blockPos, end, verticalRadius, displacement)
                        }
                    }

                    horizontalRadius++
                }

                verticalRadius++
            }
        }
    }
}
