package net.auoeke.limitless.enchantment

import net.fabricmc.fabric.api.tag.TagFactory
import net.minecraft.block.Block
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

object EnchantingBlockTag : Tag<Block> {
    private val enchantingBlocks = TagFactory.BLOCK.create(Identifier("c", "enchanting_blocks"))
    private val bookshelves = TagFactory.BLOCK.create(Identifier("c", "bookshelves"))

    override operator fun contains(entry: Block): Boolean = enchantingBlocks.contains(entry) || bookshelves.contains(entry)
    override fun values(): List<Block> = enchantingBlocks.values().toMutableList().also {it += bookshelves.values()}
}
