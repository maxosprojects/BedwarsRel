package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameStateOld;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrel.utils.ChatWriter;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTeamChestCommand extends BaseCommand implements ICommand {

  public SetTeamChestCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    Player player = (Player) sender;

    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
    if (game == null) {
      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel
          ._l(player, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
      return false;
    }

    if (game.getState() == GameStateOld.RUNNING) {
      sender.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(sender, "errors.notwhilegamerunning")));
      return false;
    }

    Team team = game.getTeam(args.get(1));
    if (team == null) {
      player.sendMessage(
              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.teamnotfound")));
      return false;
    }

    Block block = player.getLocation().getBlock();
    if (block.getType() != Material.CHEST) {
      player.sendMessage(
              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.notstandingonchest")));
      return false;
    }

    team.setChestLoc(block.getLocation());
    player.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN
        + BedwarsRel._l(player, "success.teamchestset",
        ImmutableMap.of("team", team.getName(), "game", game.getName()))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "team"};
  }

  @Override
  public String getCommand() {
    return "setteamchest";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.setteamchest.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.setteamchest.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
