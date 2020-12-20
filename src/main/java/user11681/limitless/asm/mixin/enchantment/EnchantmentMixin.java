package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import user11681.limitless.asm.access.EnchantmentAccess;

@SuppressWarnings("ConstantConditions")
@Mixin(Enchantment.class)
abstract class EnchantmentMixin implements EnchantmentAccess {
    @Shadow
    public abstract int getMinLevel();

    @Shadow public abstract int getMinPower(int level);

    public int limitless_maxLevel = Integer.MIN_VALUE;
    public boolean limitless_useGlobalMaxLevel;

    @Override
    public void limitless_setMaxLevel(final int level) {
        this.limitless_maxLevel = level;
    }

    @Override
    public void limitless_setUseGlobalMaxLevel(final boolean useGlobalMaxLevel) {
        this.limitless_useGlobalMaxLevel = useGlobalMaxLevel;
    }

    @Override
    public boolean limitless_useGlobalMaxLevel() {
        return this.limitless_useGlobalMaxLevel;
    }

    @SuppressWarnings("unused") // invoked at the end of every Enchantment implementation's constructor
    protected void limitless_initialize() throws Throwable {
        if (this.getClass().getDeclaredConstructors().length == 0 || ((Class<?>) this.getClass()) == Enchantment.class) {
            if (this.limitless_getOriginalMaxLevel() == 1) {
                this.limitless_setMaxLevel(1);
            } else {
                final int maxIterations = Math.min(1000, this.limitless_getOriginalMaxLevel() - this.getMinLevel());
                int previousPower = 0;
                int iterations = 0;

                for (int i = this.getMinLevel(); i < maxIterations; i++, iterations++) {
                    if (previousPower != this.getMinPower(i)) {
                        this.limitless_setMaxLevel(Integer.MAX_VALUE);

                        return;
                    }

                    if (iterations == maxIterations) {
                        this.limitless_setMaxLevel(this.limitless_getOriginalMaxLevel());

                        return;
                    }
                }
            }
        }
    }
}
