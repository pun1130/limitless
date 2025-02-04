package net.auoeke.limitless.transform.access

@Suppress("FunctionName")
interface EnchantmentAccess {
    fun limitless_setMaxLevel(level: Int)

    fun limitless_getOriginalMaxLevel(): Int

    fun limitless_setUseGlobalMaxLevel(useGlobalMaxLevel: Boolean)

    fun limitless_getOriginalMaxPower(level: Int): Int
}
