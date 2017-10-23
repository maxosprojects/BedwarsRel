package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AddGameCommand extends BaseCommand {

  public AddGameCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
//    if (!sender.hasPermission("bw." + this.getPermission())) {
//      return false;
//    }
//
//    Game addGame = this.getPlugin().getGameManager().addGame(args.get(0));
//    String minPlayers = args.get(1);
//
//    if (!Utils.isNumber(minPlayers)) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//              ._l(sender, "errors.minplayersmustnumber")));
//      return false;
//    }
//
//    if (addGame == null) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.gameexists")));
//      return false;
//    }
//
//    int min = Integer.parseInt(minPlayers);
//    if (min <= 0) {
//      min = 1;
//    }
//
//    addGame.setMinPlayers(min);
//    sender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN
//        + BedwarsRel
//        ._l(sender, "success.gameadded", ImmutableMap.of("game", args.get(0).toString()))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"name", "minplayers"};
  }

  @Override
  public String getCommand() {
    return "addgame";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.addgame.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.addgame.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
