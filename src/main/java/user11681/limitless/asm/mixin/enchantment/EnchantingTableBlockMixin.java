package user11681.limitless.asm.mixin.enchantment;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.enchantment.entry.EnchantmentParticleConfiguration;
import user11681.limitless.config.enchantment.entry.radius.HorizontalRadius;
import user11681.limitless.config.enchantment.entry.radius.VerticalRadius;

@Mixin(value = EnchantingTableBlock.class)
abstract class EnchantingTableBlockMixin extends BlockWithEntity {
    private static final Tag<Block> bookshelvesTag = BlockTags.getTagGroup().getTag(new Identifier("c", "bookshelves"));

    protected EnchantingTableBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomDisplayTick", cancellable = true)
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState enchantingTableState, World world, BlockPos enchantingTablePos, Random random, CallbackInfo info) {
        super.randomDisplayTick(enchantingTableState, world, enchantingTablePos, random);

        LimitlessConfiguration configuration = LimitlessConfiguration.instance;
        EnchantmentParticleConfiguration particleConfiguration = configuration.enchantment.particles;

        if (particleConfiguration.enabled) {
            HorizontalRadius horizontalRadiusRange;
            VerticalRadius verticalRadiusRange;

            if (particleConfiguration.inherit) {
                horizontalRadiusRange = configuration.enchantment.enchantingBlocks.radius.xz;
                verticalRadiusRange = configuration.enchantment.enchantingBlocks.radius.y;
            } else {
                horizontalRadiusRange = particleConfiguration.radius.xz;
                verticalRadiusRange = particleConfiguration.radius.y;
            }

            int maxVerticalRadius = verticalRadiusRange.max;
            int maxHorizontalRadius = horizontalRadiusRange.max;

            for (int direction = -1; direction <= 1; direction += 2) {
                for (int verticalRadius = verticalRadiusRange.min; verticalRadius <= maxVerticalRadius; verticalRadius++) {
                    for (int horizontalRadius = horizontalRadiusRange.min; horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                        int end = direction * horizontalRadius;

                        if (random.nextInt(16) == 0) {
                            for (int distance = -horizontalRadius; distance <= horizontalRadius; distance++) {
                                int displacement = direction * distance;
                                BlockState blockState = world.getBlockState(enchantingTablePos.add(displacement, verticalRadius, end));

                                if (blockState.isIn(bookshelvesTag)) {
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

                                if (distance != -horizontalRadius && distance != horizontalRadius) {
                                    blockState = world.getBlockState(enchantingTablePos.add(end, verticalRadius, displacement));

                                    if (blockState.isIn(bookshelvesTag)) {
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
        }

        info.cancel();
    }
}
