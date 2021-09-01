package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.config.enchantment.entry.radius.HorizontalRadius
import net.auoeke.limitless.config.enchantment.entry.radius.VerticalRadius
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.EnchantingTableBlock
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Environment(EnvType.CLIENT)
@Mixin(value = [EnchantingTableBlock::class])
internal abstract class EnchantingTableBlockMixin(settings: Settings) : BlockWithEntity(settings) {
    @Inject(at = [At("HEAD")], method = ["randomDisplayTick"], cancellable = true)
    fun expandEnchantmentParticles(enchantingTableState: BlockState?, world: World, enchantingTablePos: BlockPos, random: Random, info: CallbackInfo) {
        super.randomDisplayTick(enchantingTableState, world, enchantingTablePos, random)

        val configuration = Configuration.instance
        val particleConfiguration = configuration.enchantment.particles

        if (particleConfiguration.enabled) {
            val horizontalRadiusRange: HorizontalRadius
            val verticalRadiusRange: VerticalRadius

            if (particleConfiguration.inherit) {
                horizontalRadiusRange = configuration.enchantment.enchantingBlocks.radius.xz
                verticalRadiusRange = configuration.enchantment.enchantingBlocks.radius.y
            } else {
                horizontalRadiusRange = particleConfiguration.radius.xz
                verticalRadiusRange = particleConfiguration.radius.y
            }

            val maxVerticalRadius = verticalRadiusRange.max
            val maxHorizontalRadius = horizontalRadiusRange.max
            val enchantingBlocks = configuration.enchantment.enchantingBlocks

            for (direction in -1..1 step 2) {
                for (verticalRadius in verticalRadiusRange.min..maxVerticalRadius) {
                    for (horizontalRadius in horizontalRadiusRange.min..maxHorizontalRadius) {
                        val end = direction * horizontalRadius

                        if (random.nextInt(16) == 0) {
                            for (distance in -horizontalRadius..horizontalRadius) {
                                val displacement = direction * distance

                                if (enchantingBlocks.enchantingPower(world.getBlockState(enchantingTablePos.add(displacement, verticalRadius, end)).block) != 0F) {
                                    world.addParticle(
                                        ParticleTypes.ENCHANT,
                                        enchantingTablePos.x + 0.5, (
                                        enchantingTablePos.y + 2).toDouble(),
                                        enchantingTablePos.z + 0.5,
                                        displacement + random.nextFloat() - 0.5, (
                                        verticalRadius - random.nextFloat() - 1).toDouble(),
                                        end + random.nextFloat() - 0.5
                                    )
                                }

                                if (distance != -horizontalRadius && distance != horizontalRadius && enchantingBlocks.enchantingPower(world.getBlockState(enchantingTablePos.add(end, verticalRadius, displacement)).block) != 0F) {
                                    world.addParticle(
                                        ParticleTypes.ENCHANT,
                                        enchantingTablePos.x + 0.5, (
                                        enchantingTablePos.y + 2).toDouble(),
                                        enchantingTablePos.z + 0.5,
                                        end + random.nextFloat() - 0.5, (
                                        verticalRadius - random.nextFloat() - 1).toDouble(),
                                        displacement + random.nextFloat() - 0.5
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        info.cancel()
    }
}
