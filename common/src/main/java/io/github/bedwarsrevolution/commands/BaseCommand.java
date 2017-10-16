package io.github.bedwarsrevolution.commands;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrel.utils.ChatWriter;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements ICommand {

  private BedwarsRevol plugin = null;

  public BaseCommand(BedwarsRevol plugin) {
    this.plugin = plugin;
  }

  @Override
  public abstract boolean execute(CommandSender sender, ArrayList<String> args);

  @Override
  public abstract String[] getArguments();

  @Override
  public abstract String getCommand();

  @Override
  public abstract String getDescription();

  @Override
  public abstract String getName();

  protected BedwarsRevol getPlugin() {
    return this.plugin;
  }

  @Override
  public boolean hasPermission(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatWriter.pluginMessage("Only players should execute this command!"));
      return false;
    }

    if (!sender.hasPermission("bw." + this.getPermission())) {
      sender.sendMessage(ChatWriter
          .pluginMessage(ChatColor.RED + "You don't have permission to execute this command!"));
      return false;
    }

    return true;
  }

}
