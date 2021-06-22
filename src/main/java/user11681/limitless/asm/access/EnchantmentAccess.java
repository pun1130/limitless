package user11681.limitless.asm.access;

public interface EnchantmentAccess {
    void limitless_setMaxLevel(int level);

    int limitless_getOriginalMaxLevel();

    void limitless_setUseGlobalMaxLevel(boolean useGlobalMaxLevel);

    boolean limitless_useGlobalMaxLevel();

    int limitless_getOriginalMaxPower(int level);
}
