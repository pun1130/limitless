package user11681.limitless.asm.mixin.enchantment;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import user11681.limitless.config.EnchantmentParticleConfiguration;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.RadiusConfiguration;
import user11681.limitless.tag.EnchantingBlocks;

@Mixin(value = EnchantingTableBlock.class,
       priority = 100)
abstract class EnchantingTableBlockMixin extends BlockWithEntity {
    protected EnchantingTableBlockMixin(final Settings settings) {
        super(settings);
    }

    /**
     * @author user11681
     * @reason replacing the loop to increase glyph particle range.
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos blockPos, Random random) {
        super.randomDisplayTick(state, world, blockPos, random);

        final LimitlessConfiguration configuration = LimitlessConfiguration.instance;
        final EnchantmentParticleConfiguration particleConfiguration = configuration.enchantmentParticles;

        if (particleConfiguration.enabled) {
            final RadiusConfiguration horizontalRadiusRange;
            final RadiusConfiguration verticalRadiusRange;

            if (particleConfiguration.inherit) {
                horizontalRadiusRange = configuration.enchantingBlockRadiusXZ;
                verticalRadiusRange = configuration.enchantingBlockRadiusY;
            } else {
                horizontalRadiusRange = particleConfiguration.radiusXZ;
                verticalRadiusRange = particleConfiguration.radiusY;
            }

            final int maxVerticalRadius = verticalRadiusRange.max;
            int maxHorizontalRadius = horizontalRadiusRange.max;
            int verticalRadius;
            int horizontalRadius;
            int end;
            int displacement;
            int j;

            for (int k = -1; k <= 1; k += 2) {
                for (verticalRadius = verticalRadiusRange.min; verticalRadius <= maxVerticalRadius; verticalRadius++) {
                    for (horizontalRadius = horizontalRadiusRange.min; horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                        end = k * horizontalRadius;

                        for (j = -horizontalRadius; j <= horizontalRadius; j++) {
                            displacement = k * j;

                            if (world.getBlockState(blockPos.add(displacement, verticalRadius, end)).isIn(EnchantingBlocks.tag)) {
                                world.addParticle(ParticleTypes.ENCHANT, blockPos.getX() + 0.5, blockPos.getY() + 2, blockPos.getZ() + 0.5, j + random.nextFloat() - 0.5, verticalRadius - random.nextFloat() - 1.0F, end + random.nextFloat() - 0.5D);
                            }

                            if (j != -horizontalRadius && j != horizontalRadius) {
                                if (world.getBlockState(blockPos.add(end, verticalRadius, displacement)).isIn(EnchantingBlocks.tag)) {
                                    world.addParticle(ParticleTypes.ENCHANT, blockPos.getX() + 0.5, blockPos.getY() + 2, blockPos.getZ() + 0.5, end + random.nextFloat() - 0.5, verticalRadius - random.nextFloat() - 1, j + random.nextFloat() - 0.5);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
