package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinGameCommand extends BaseCommand {

  public JoinGameCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
    if (!super.hasPermission(sender) || args.size() < 1) {
      return false;
    }
    Player player = (Player) sender;
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameContext(args.get(0));
    if (ctx == null) {
      if (!args.get(0).equalsIgnoreCase("random")) {
        sender.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
            + BedwarsRevol
            ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0)))));
        return true;
      }

//      List<GameContext> games = new ArrayList<>();
//      for (GameContext context : this.getPlugin().getGameManager().getGamesContexts()) {
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
    } else {
      ctx.getState().playerJoins(player);

//    if (game == null) {
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
    }
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
