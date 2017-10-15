package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveGameCommand extends BaseCommand implements ICommand {

  public SaveGameCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
//    if (!sender.hasPermission("bw." + this.getPermission())) {
//      return false;
//    }
//
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    if (game == null) {
//      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
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
//    if (!game.saveGame(sender, true)) {
//      return false;
//    }
//
//    sender
//        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel
//            ._l(sender, "success.saved")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "save";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.save.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.save.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
