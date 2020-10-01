package user11681.limitless.asm.mixin.enchantment;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.asm.access.EnchantmentAccess;
import user11681.limitless.config.LimitlessConfiguration;

@SuppressWarnings({"unused", "RedundantSuppression"})
@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
    @ModifyConstant(method = "calculateRequiredExperienceLevel",
                    constant = @Constant(intValue = 15))
    private static int modifyMaxBookshelves(final int fifteen) {
        return LimitlessConfiguration.instance.maxEnchantingBlocks;
    }

    @Redirect(method = "getPossibleEntries",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/enchantment/Enchantment;isTreasure()Z"))
    private static boolean allowTreasure(final Enchantment enchantment) {
        return LimitlessConfiguration.instance.allowTreasure ? enchantment.isCursed() : enchantment.isTreasure();
    }

    private static void limitless_getHighestSuitableLevel(final int power, final Enchantment enchantment, final List<EnchantmentLevelEntry> entries) {
        final int maxLevel = enchantment.getMaxLevel();
        boolean found = false;
        int lastCandidate = 0;

        for (int i = enchantment.getMinLevel(); i <= maxLevel; i++) {
            if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                lastCandidate = i;
                found = true;

                if (((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel() == 1) {
                    break;
                }
            } else {
                break;
            }
        }

        if (found) {
            entries.add(new EnchantmentLevelEntry(enchantment, lastCandidate));
        }
    }
}
