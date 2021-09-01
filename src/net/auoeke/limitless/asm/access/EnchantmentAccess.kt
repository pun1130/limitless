package net.auoeke.limitless.asm.access

@Suppress("FunctionName")
interface EnchantmentAccess {
    fun limitless_setMaxLevel(level: Int)

    fun limitless_getOriginalMaxLevel(): Int

    fun limitless_setUseGlobalMaxLevel(useGlobalMaxLevel: Boolean)

    // fun limitless_useGlobalMaxLevel(): Boolean

    fun limitless_getOriginalMaxPower(level: Int): Int
}
