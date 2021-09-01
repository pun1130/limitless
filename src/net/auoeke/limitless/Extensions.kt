package net.auoeke.limitless

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity

val client: MinecraftClient = MinecraftClient.getInstance()
val player: PlayerEntity get() = client.player!!
