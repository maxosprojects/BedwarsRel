package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCommand extends BaseCommand implements ICommand {

  public SetLobbyCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
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
//    if (game.getState() != GameStateOld.STOPPED) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//              ._l(sender, "errors.notwhilegamerunning")));
//      return false;
//    }
//
//    game.setLobby(player);
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "setlobby";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.setlobby.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.setlobby.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
