package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionNameCommand extends BaseCommand implements ICommand {

  public RegionNameCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
//    if (!sender.hasPermission("bw." + this.getPermission())) {
//      return false;
//    }
//
//    Player player = (Player) sender;
//
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    String name = args.get(1).toString();
//
//    if (game == null) {
//      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//          + BedwarsRel
//          ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
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
//    if (name.length() > 15) {
//      player.sendMessage(
//          ChatWriter
//              .pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.toolongregionname")));
//      return true;
//    }
//
//    game.setRegionName(name);
//    player
//        .sendMessage(
//            ChatWriter
//                .pluginMessage(ChatColor.GREEN + BedwarsRel._l(player, "success.regionnameset")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "name"};
  }

  @Override
  public String getCommand() {
    return "regionname";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.regionname.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.regionname.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
