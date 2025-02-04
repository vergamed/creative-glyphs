package team.unnamed.creativeglyphs.plugin.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface CommandRunnable {

    void run(CommandSender sender, ArgumentStack args);

    default CommandExecutor asExecutor() {
        return (sender, command, label, args) -> {
            run(sender, ArgumentStack.of(args));
            return true;
        };
    }

}
