package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StartGameCommand extends BaseCommand implements ICommand {

  public StartGameCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
    if (!sender.hasPermission("bw." + this.getPermission())) {
      return false;
    }

    GameContext game = this.getPlugin().getGameManager().getGameContext(args.get(0));
    if (game == null) {
      sender.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
          + BedwarsRevol
          ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
      return false;
    }

    game.restart();
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "start";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.start.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.start.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
