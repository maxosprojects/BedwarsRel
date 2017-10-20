package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AddTeamCommand extends BaseCommand {

  public AddTeamCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
//    if (!sender.hasPermission("bw." + this.getPermission())) {
//      return false;
//    }
//
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    String name = args.get(1);
//    String color = args.get(2);
//    String maxPlayers = args.get(3);
//    TeamColor tColor = null;
//
//    try {
//      tColor = TeamColor.valueOf(color.toUpperCase());
//    } catch (Exception e) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//              ._l(sender, "errors.teamcolornotallowed")));
//      return false;
//    }
//
//    if (game == null) {
//      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//          + BedwarsRel
//          ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
//      return false;
//    }
//
//    if (game.getState() != GameStateOld.STOPPED) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//              ._l(sender, "errors.notwhilegamerunning")));
//      return false;
//    }
//
//    int playerMax = Integer.parseInt(maxPlayers);
//
//    if (playerMax < 1 || playerMax > 24) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.playeramount")));
//      return false;
//    }
//
//    if (name.length() < 3 || name.length() > 20) {
//      sender
//          .sendMessage(
//              ChatWriter
//                  .pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.teamnamelength")));
//      return false;
//    }
//
//    if (game.getTeam(name) != null) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.teamnameinuse")));
//      return false;
//    }
//
//    game.addTeam(name, tColor, playerMax);
//    sender.sendMessage(ChatWriter.pluginMessage(
//        ChatColor.GREEN + BedwarsRel
//            ._l(sender, "success.teamadded", ImmutableMap.of("team", name))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "name", "color", "maxplayers"};
  }

  @Override
  public String getCommand() {
    return "addteam";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.addteam.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.addteam.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}