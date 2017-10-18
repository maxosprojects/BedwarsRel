package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBaseRegionCommand extends BaseCommand implements ICommand {

  public SetBaseRegionCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
//    if (!super.hasPermission(sender)) {
//      return false;
//    }
//
//    Player player = (Player) sender;
//
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    if (game == null) {
//      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//          + BedwarsRel
//          ._l(player, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
//      return false;
//    }
//
//    if (game.getState() == GameStateOld.RUNNING) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//              ._l(sender, "errors.notwhilegamerunning")));
//      return false;
//    }
//
//    Team team = game.getTeam(args.get(1));
//    if (team == null) {
//      player.sendMessage(
//              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.teamnotfound")));
//      return false;
//    }
//
//    String loc = args.get(2);
//    if (!loc.equalsIgnoreCase("loc1") && !loc.equalsIgnoreCase("loc2")) {
//      player
//          .sendMessage(
//              ChatWriter
//                  .pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.regionargument")));
//      return false;
//    }
//
//    team.setBaseLoc(player.getLocation(), loc);
//    player.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN
//        + BedwarsRel._l(player, "success.baseregionset",
//        ImmutableMap.of("location", loc, "team", team.getName(), "game", game.getName()))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "team", "loc1;loc2"};
  }

  @Override
  public String getCommand() {
    return "setbaseregion";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.setbaseregion.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.setbaseregion.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
