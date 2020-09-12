package user11681.limitless.asm.access;

import net.minecraft.enchantment.Enchantment;

public interface EnchantmentAccess {
    /**
     * This method assigns the enchantment's maximum level to <b>{@code level}</b>.
     */
    void limitless_setMaxLevel(int level);

    /**
     * This method is the original {@link Enchantment#getMaxLevel()} implementation with a different name.
     *
     * @return the original maximum level.
     */
    int limitless_getOriginalMaxLevel();

    /**
     * This method is the original {@link Enchantment#getMaxPower(int)} implementation with a different name.
     *
     * @return the original maximum power.
     */
    int limitless_getOriginalMaxPower(int level);
}
