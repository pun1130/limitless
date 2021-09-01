package net.auoeke.limitless.enchantment

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

fun interface EnchantingBlockConsumer {
    fun accept(state: BlockState, pos: BlockPos, dX: Int, dY: Int, dZ: Int)
}
