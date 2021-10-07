package net.auoeke.limitless.transform.mixin.enchantment;

import java.util.Random;
import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.config.enchantment.entry.radius.HorizontalRadius;
import net.auoeke.limitless.config.enchantment.entry.radius.VerticalRadius;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = EnchantingTableBlock.class)
abstract class EnchantingTableBlockMixin extends BlockWithEntity {
    protected EnchantingTableBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomDisplayTick", cancellable = true)
    public void expandEnchantmentParticles(BlockState enchantingTableState, World world, BlockPos enchantingTablePos, Random random, CallbackInfo info) {
        super.randomDisplayTick(enchantingTableState, world, enchantingTablePos, random);

        var configuration = Configuration.instance;
        var particleConfiguration = configuration.enchantment.getParticles();

        if (particleConfiguration.getEnabled()) {
            HorizontalRadius horizontalRadiusRange;
            VerticalRadius verticalRadiusRange;

            if (particleConfiguration.getInherit()) {
                horizontalRadiusRange = configuration.enchantment.getEnchantingBlocks().getRadius().getXz();
                verticalRadiusRange = configuration.enchantment.getEnchantingBlocks().getRadius().getY();
            } else {
                horizontalRadiusRange = particleConfiguration.getRadius().getXz();
                verticalRadiusRange = particleConfiguration.getRadius().getY();
            }

            int maxVerticalRadius = verticalRadiusRange.getMax();
            int maxHorizontalRadius = horizontalRadiusRange.getMax();
            var enchantingBlocks = configuration.enchantment.getEnchantingBlocks();

            for (int direction = -1; direction <= 1; direction += 2) {
                for (int verticalRadius = verticalRadiusRange.getMin(); verticalRadius <= maxVerticalRadius; verticalRadius++) {
                    for (int horizontalRadius = horizontalRadiusRange.getMin(); horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                        int end = direction * horizontalRadius;

                        if (random.nextInt(16) == 0) {
                            for (int distance = -horizontalRadius; distance <= horizontalRadius; distance++) {
                                int displacement = direction * distance;

                                if (enchantingBlocks.enchantingPower(world.getBlockState(enchantingTablePos.add(displacement, verticalRadius, end)).getBlock()) != 0) {
                                    world.addParticle(
                                        ParticleTypes.ENCHANT,
                                        enchantingTablePos.getX() + 0.5,
                                        enchantingTablePos.getY() + 2,
                                        enchantingTablePos.getZ() + 0.5,
                                        displacement + random.nextFloat() - 0.5,
                                        verticalRadius - random.nextFloat() - 1,
                                        end + random.nextFloat() - 0.5
                                    );
                                }

                                if (distance != -horizontalRadius && distance != horizontalRadius
                                    && enchantingBlocks.enchantingPower(world.getBlockState(enchantingTablePos.add(end, verticalRadius, displacement)).getBlock()) != 0) {
                                    world.addParticle(
                                        ParticleTypes.ENCHANT,
                                        enchantingTablePos.getX() + 0.5,
                                        enchantingTablePos.getY() + 2,
                                        enchantingTablePos.getZ() + 0.5,
                                        end + random.nextFloat() - 0.5,
                                        verticalRadius - random.nextFloat() - 1,
                                        displacement + random.nextFloat() - 0.5
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }

        info.cancel();
    }
}
