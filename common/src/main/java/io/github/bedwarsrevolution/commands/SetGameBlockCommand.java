package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class SetGameBlockCommand extends BaseCommand implements ICommand {

  public SetGameBlockCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
//    if (!sender.hasPermission("bw." + this.getPermission())) {
//      return false;
//    }
//
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    String material = args.get(1).toString();
//
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
//    Material targetMaterial = Utils.parseMaterial(material);
//    if (targetMaterial == null && !"DEFAULT".equals(material)) {
//      sender
//          .sendMessage(
//              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//                  ._l(sender, "errors.novalidmaterial")));
//      return true;
//    }
//
//    if ("DEFAULT".equalsIgnoreCase(material)) {
//      game.setTargetMaterial(null);
//    } else {
//      game.setTargetMaterial(targetMaterial);
//    }
//
//    sender.sendMessage(
//        ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(sender, "success.materialset")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "blocktype"};
  }

  @Override
  public String getCommand() {
    return "setgameblock";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.setgameblock.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.setgameblock.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}