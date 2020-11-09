package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.enchantment.entry.radius.HorizontalRadius;
import user11681.limitless.config.enchantment.entry.radius.VerticalRadius;
import user11681.limitless.enchantment.EnchantmentUtil;

@SuppressWarnings({"unused", "RedundantSuppression", "UnresolvedMixinReference"})
@Mixin(EnchantmentScreenHandler.class)
abstract class EnchantmentScreenHandlerMixin {
    @Redirect(method = "method_17410",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V"))
    public void mergeEnchantments(final ItemStack stack, final Enchantment enchantment, final int level) {
        EnchantmentUtil.mergeEnchantment(stack, enchantment, level);
    }

    @Redirect(method = "generateEnchantments",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    public Item fixEnchantedBook(final ItemStack stack) {
        final Item item = stack.getItem();

        return item == Items.ENCHANTED_BOOK && LimitlessConfiguration.instance.enchantment.reenchanting.allowEnchantedBooks() ? Items.BOOK : item;
    }

    private static int limitless_scanEnchantingBlocks(final World world, final BlockPos blockPos) {
        final HorizontalRadius horizontalRadiusRange = LimitlessConfiguration.instance.enchantment.enchantingBlocks.radius.xz;
        final VerticalRadius verticalRadiusRange = LimitlessConfiguration.instance.enchantment.enchantingBlocks.radius.y;
        final int maxVerticalRadius = verticalRadiusRange.max;
        final int maxHorizontalRadius = horizontalRadiusRange.max;
        int bookshelfCount = 0;

        for (int k = -1; k <= 1; k += 2) {
            final int remainder = k % 2;

            for (int verticalRadius = verticalRadiusRange.min; verticalRadius <= maxVerticalRadius; verticalRadius++) {
                for (int horizontalRadius = horizontalRadiusRange.min; horizontalRadius <= maxHorizontalRadius; horizontalRadius++) {
                    final int end = k * horizontalRadius;

                    for (int j = -horizontalRadius; j <= horizontalRadius; j++) {
                        final int displacement = k * j;

                        if (world.getBlockState(blockPos.add(displacement, verticalRadius, end)).isOf(Blocks.BOOKSHELF)) {
                            ++bookshelfCount;
                        }

                        if (j != -horizontalRadius && j != horizontalRadius) {
                            if (world.getBlockState(blockPos.add(end, verticalRadius, displacement)).isOf(Blocks.BOOKSHELF)) {
                                ++bookshelfCount;
                            }
                        }
                    }
                }
            }
        }

        return bookshelfCount;
    }
}
