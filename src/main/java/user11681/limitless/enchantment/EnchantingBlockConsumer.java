package user11681.limitless.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface EnchantingBlockConsumer {
    void accept(BlockState state, BlockPos pos, int dX, int dY, int dZ);
}
