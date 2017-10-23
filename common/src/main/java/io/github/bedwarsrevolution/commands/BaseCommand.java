package io.github.bedwarsrevolution.commands;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements ICommand {

  private BedwarsRevol plugin = null;

  public BaseCommand(BedwarsRevol plugin) {
    this.plugin = plugin;
  }

  protected BedwarsRevol getPlugin() {
    return this.plugin;
  }

  @Override
  public boolean hasPermission(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatWriterNew.pluginMessage("Only players should execute this command!"));
      return false;
    }

    if (!sender.hasPermission("bw." + this.getPermission())) {
      sender.sendMessage(ChatWriterNew
          .pluginMessage(ChatColor.RED + "You don't have permission to execute this command!"));
      return false;
    }

    return true;
  }

}
