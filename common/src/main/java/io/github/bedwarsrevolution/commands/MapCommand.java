package io.github.bedwarsrevolution.commands;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MapCommand extends BaseCommand {

  public MapCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
    if (!super.hasPermission(sender) || args.size() < 1) {
      return false;
    }
    String game = args.get(0);
    if (BedwarsRevol.getInstance().getMapsManager().store(game)) {
      sender.sendMessage(ChatWriterNew.pluginMessage(ChatColor.GREEN + "Saved map for " + game));
    } else {
      sender.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + "Error saving map for " + game));
    }
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "map";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.map.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.map.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
