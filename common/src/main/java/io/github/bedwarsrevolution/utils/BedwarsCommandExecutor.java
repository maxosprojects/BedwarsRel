package io.github.bedwarsrevolution.utils;

import io.github.bedwarsrel.events.BedwarsCommandExecutedEvent;
import io.github.bedwarsrel.events.BedwarsExecuteCommandEvent;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.commands.BaseCommand;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BedwarsCommandExecutor implements CommandExecutor {

  private BedwarsRevol plugin = null;

  public BedwarsCommandExecutor(BedwarsRevol plugin) {
    super();

    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (!cmd.getName().equals("bw")) {
      return false;
    }

    if (args.length < 1) {
      return false;
    }

    String command = args[0];
    ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
    arguments.remove(0);

    for (BaseCommand bCommand : this.plugin.getCommands()) {
      if (bCommand.getCommand().equalsIgnoreCase(command)) {
        if (bCommand.getArguments().length > arguments.size()) {
          sender.sendMessage(
              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRevol
                  ._l(sender, "errors.argumentslength")));
          return false;
        }

        BedwarsExecuteCommandEvent commandEvent =
            new BedwarsExecuteCommandEvent(sender, bCommand, arguments);
        BedwarsRevol.getInstance().getServer().getPluginManager().callEvent(commandEvent);

        if (commandEvent.isCancelled()) {
          return true;
        }

        boolean result = bCommand.execute(sender, arguments);

        BedwarsCommandExecutedEvent executedEvent =
            new BedwarsCommandExecutedEvent(sender, bCommand, arguments, result);
        BedwarsRevol.getInstance().getServer().getPluginManager().callEvent(executedEvent);

        return result;
      }
    }

    return false;
  }

}
