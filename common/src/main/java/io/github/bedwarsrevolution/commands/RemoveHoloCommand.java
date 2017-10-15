package io.github.bedwarsrevolution.commands;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class RemoveHoloCommand extends BaseCommand implements ICommand {

  public RemoveHoloCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

//    final Player player = (Player) sender;
//    player.setMetadata("bw-remove-holo", new FixedMetadataValue(BedwarsRevol.getInstance(), true));
//    if (BedwarsRevol.getInstance().getHolographicInteractor().getType()
//        .equalsIgnoreCase("HolographicDisplays")) {
//      player.sendMessage(
//          ChatWriterNew
//              .pluginMessage(
//                  ChatColor.GREEN + BedwarsRevol._l(player, "commands.removeholo.explain")));
//
//    } else if (BedwarsRevol.getInstance().getHolographicInteractor().getType()
//        .equalsIgnoreCase("HologramAPI")) {
//
//      for (Location location : BedwarsRevol.getInstance().getHolographicInteractor()
//          .getHologramLocations()) {
//        if (player.getEyeLocation().getBlockX() == location.getBlockX()
//            && player.getEyeLocation().getBlockY() == location.getBlockY()
//            && player.getEyeLocation().getBlockZ() == location.getBlockZ()) {
//          BedwarsRevol.getInstance().getHolographicInteractor().onHologramTouch(player, location);
//        }
//      }
//      BedwarsRevol.getInstance().getServer().getScheduler().runTaskLater(BedwarsRevol.getInstance(),
//          new Runnable() {
//
//            @Override
//            public void run() {
//              if (player.hasMetadata("bw-remove-holo")) {
//                player.removeMetadata("bw-remove-holo", BedwarsRevol.getInstance());
//              }
//            }
//
//          }, 10L * 20L);
//
//    }
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "removeholo";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.removeholo.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.removeholo.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
