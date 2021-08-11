package net.auoeke.limitless;

import com.mojang.brigadier.CommandDispatcher;
import net.auoeke.limitless.config.LimitlessConfiguration;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class LimitlessCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("limitless").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("reload").executes(context -> {
            LimitlessConfiguration.holder.load();
            LimitlessConfiguration.refresh();

            return 0;
        })));
    }
}
