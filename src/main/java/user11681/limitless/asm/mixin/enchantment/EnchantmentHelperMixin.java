package user11681.limitless.asm.mixin.enchantment;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import org.spongepowered.asm.mixin.Mixin;
import user11681.limitless.asm.access.EnchantmentAccess;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
    private static void limitless_getHighestSuitableLevel(final int power, final Enchantment enchantment, final List<EnchantmentLevelEntry> entries) {
        final int maxLevel = enchantment.getMaxLevel();
        int lastCandidate = Integer.MIN_VALUE; // none yet

        for (int i = enchantment.getMinLevel(); i <= maxLevel; i++) {
            if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                lastCandidate = i;

                if (((EnchantmentAccess) enchantment).limitless_getOriginalMaxLevel() == 1) {
                    break;
                }
            } else {
                break;
            }
        }

        if (lastCandidate != Integer.MIN_VALUE) {
            entries.add(new EnchantmentLevelEntry(enchantment, lastCandidate));
        }
    }
}
