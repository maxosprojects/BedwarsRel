package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetBuilderCommand extends BaseCommand implements ICommand {

  public SetBuilderCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
//    if (!sender.hasPermission("bw." + this.getPermission())) {
//      return false;
//    }
//
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//    String builder = args.get(1).toString();
//
//    if (game == null) {
//      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//          + BedwarsRel
//          ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
//      return false;
//    }
//
//    game.setBuilder(builder);
//    sender.sendMessage(
//        ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(sender, "success.builderset")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "builder"};
  }

  @Override
  public String getCommand() {
    return "setbuilder";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.setbuilder.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.setbuilder.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
