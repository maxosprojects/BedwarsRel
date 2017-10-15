package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHoloCommand extends BaseCommand implements ICommand {

  public AddHoloCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    if (!BedwarsRevol.getInstance().isHologramsEnabled()) {
      String missingholodependency = BedwarsRevol.getInstance().getMissingHoloDependency();

      sender.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
          + BedwarsRevol._l(sender, "errors.holodependencynotfound",
          ImmutableMap.of("dependency", missingholodependency))));
      return true;
    }

//    Player player = (Player) sender;
//    BedwarsRevol.getInstance().getHolographicInteractor()
//        .addHologramLocation(player.getEyeLocation());
//    BedwarsRevol.getInstance().getHolographicInteractor().updateHolograms();
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "addholo";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.addholo.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.addholo.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
