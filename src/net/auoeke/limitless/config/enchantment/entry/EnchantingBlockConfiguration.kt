package net.auoeke.limitless.config.enchantment.entry

import it.unimi.dsi.fastutil.objects.*
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded
import net.auoeke.limitless.config.enchantment.entry.radius.Radius
import net.auoeke.limitless.enchantment.EnchantingBlockTag
import net.auoeke.limitless.log.LimitlessLogger
import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class EnchantingBlockConfiguration : ConfigData {
    var maxBlocks = 512
    var maxPower = 1024

    @CollapsibleObject
    var radius = Radius()

    @Excluded
    private var whitelist: Map<String, Float> = Object2FloatOpenHashMap(arrayOf(EXAMPLE_BLOCK), floatArrayOf(5F))

    @Excluded
    private var blacklist: List<String> = ObjectArrayList(arrayOf(EXAMPLE_BLOCK))

    @Transient
    val blockWhitelist: Reference2FloatMap<Block> = Reference2FloatOpenHashMap()

    @Transient
    val blockBlacklist: MutableSet<Block> = ReferenceOpenHashSet()

    fun enchantingPower(block: Block): Float = when {
        blockWhitelist.containsKey(block) -> blockWhitelist.getFloat(block)
        blockBlacklist.contains(block) || block !in EnchantingBlockTag -> 0F
        else -> 2F
    }

    override fun validatePostLoad() {
        blockWhitelist.clear()
        blockBlacklist.clear()
        whitelist.forEach {(key, power) -> verifyBlock(key, "white")?.also {blockWhitelist[it] = power}}
        blacklist.forEach {key -> verifyBlock(key, "black")?.also {blockBlacklist.add(it)}}

        blockWhitelist.keys
            .filter {blockBlacklist.contains(it)}
            .forEach {LimitlessLogger.warn("""Block with identifier "${Registry.BLOCK.getId(it)}" was found in whitelist and blacklist; whitelist takes precedence.""")}
    }

    companion object {
        private const val EXAMPLE_BLOCK = "examplemod:example_block"

        private fun verifyBlock(key: String, type: String): Block? {
            val id = Identifier.tryParse(key)

            if (id === null) {
                LimitlessLogger.error("""Key "$key" listed in limitless' enchanting block ${type}list is not formatted correctly.""")
            } else if (key != EXAMPLE_BLOCK) {
                Registry.BLOCK.getOrEmpty(id).also {
                    if (it.isPresent) {
                        return it.get()
                    }
                }

                LimitlessLogger.warn("""A block with identifier "$id" is not registered.""")
            }

            return null
        }
    }
}
