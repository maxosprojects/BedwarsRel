package io.github.bedwarsrevolution.commands;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends BaseCommand implements ICommand {

  public KickCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender) && !sender.isOp()) {
      return false;
    }

    Player player = (Player) sender;
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);

    // find player
    Player kickPlayer = BedwarsRevol.getInstance().getServer().getPlayer(args.get(0).toString());

    if (ctx == null) {
      player
          .sendMessage(ChatWriterNew.pluginMessage(BedwarsRevol._l(player, "errors.notingameforkick")));
      return true;
    }

    if (kickPlayer == null || !kickPlayer.isOnline()) {
      player.sendMessage(ChatWriterNew.pluginMessage(BedwarsRevol._l(player, "errors.playernotfound")));
      return true;
    }

    if (ctx != BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(kickPlayer)) {
      player.sendMessage(ChatWriterNew.pluginMessage(BedwarsRevol._l(player, "errors.playernotingame")));
      return true;
    }

    ctx.getState().playerLeaves(ctx.getPlayerContext(kickPlayer), true);
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"player"};
  }

  @Override
  public String getCommand() {
    return "kick";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.kick.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.kick.name");
  }

  @Override
  public String getPermission() {
    return "kick";
  }

}
