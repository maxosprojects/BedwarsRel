package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand implements ICommand {

  public SetSpawnCommand(BedwarsRevol plugin) {
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
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.teamnotfound")));
//      return false;
//    }
//
//    team.setSpawnLocation(player.getLocation());
//    player
//        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel
//            ._l(player, "success.spawnset",
//                ImmutableMap
//                    .of("team", team.getChatColor() + team.getDisplayName() + ChatColor.GREEN))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "team"};
  }

  @Override
  public String getCommand() {
    return "setspawn";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.setspawn.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.setspawn.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}