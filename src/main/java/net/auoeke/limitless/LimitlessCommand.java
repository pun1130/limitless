package net.auoeke.limitless;

import com.mojang.brigadier.CommandDispatcher;
import net.auoeke.limitless.config.Configuration;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class LimitlessCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("limitless").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("reload").executes(context -> {
            Configuration.holder.load();
            Configuration.refresh();

            context.getSource().getPlayer().sendMessage(Text.of("Done."), true);

            return 0;
        })));
    }
}
