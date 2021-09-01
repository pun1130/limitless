package net.auoeke.limitless

import com.mojang.brigadier.CommandDispatcher
import net.auoeke.limitless.config.Configuration
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object LimitlessCommand : CommandRegistrationCallback {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource?>, dedicated: Boolean) {
        dispatcher.register(CommandManager.literal("limitless").requires {it.hasPermissionLevel(2)}.then(CommandManager.literal("reload").executes {context ->
            Configuration.holder.load()
            Configuration.refresh()
            context.source.player.sendMessage(Text.of("Done."), true)

            return@executes 0
        }))
    }
}
