package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinGameCommand extends BaseCommand {

  public JoinGameCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
//    if (!super.hasPermission(sender)) {
//      return false;
//    }
//
//    Player player = (Player) sender;
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    Game gameOfPlayer = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
//
//    if (gameOfPlayer != null) {
//      if (gameOfPlayer.getState() == GameStateOld.RUNNING) {
//        sender.sendMessage(
//            ChatWriter
//                .pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.notwhileingame")));
//        return false;
//      }
//
//      if (gameOfPlayer.getState() == GameStateOld.WAITING) {
//        gameOfPlayer.playerLeave(player, false);
//      }
//    }
//
//    if (game == null) {
//      if (!args.get(0).equalsIgnoreCase("random")) {
//        sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//            + BedwarsRel
//            ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
//        return true;
//      }
//
//      ArrayList<Game> games = new ArrayList<>();
//      for (Game g : this.getPlugin().getGameManager().getGamesContexts()) {
//        if (g.getState() == GameStateOld.WAITING) {
//          games.add(g);
//        }
//      }
//      if (games.size() == 0) {
//        sender.sendMessage(
//            ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.nofreegames")));
//        return true;
//      }
//      game = games.get(Utils.randInt(0, games.size() - 1));
//    }
//
//    if (game.playerJoins(player)) {
//      sender.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(sender, "success.joined")));
//    }
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "join";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.join.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.join.name");
  }

  @Override
  public String getPermission() {
    return "base";
  }

}
