package io.github.bedwarsrevolution.commands;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.updater.ConfigUpdaterNew;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

  public ReloadCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission(this.getPermission())) {
      return false;
    }

    File config = new File(BedwarsRevol.getInstance().getDataFolder(), "config.yml");
    String command = "";

    if (args.size() > 0) {
      command = args.get(0);
    } else {
      command = "all";
    }

    if (command.equalsIgnoreCase("all")) {
      // save default config
      if (!config.exists()) {
        BedwarsRevol.getInstance().saveDefaultConfig();
      }

      BedwarsRevol.getInstance().loadConfigInUTF();

      BedwarsRevol.getInstance().getConfig().options().copyDefaults(true);
      BedwarsRevol.getInstance().getConfig().options().copyHeader(true);

      ConfigUpdaterNew configUpdater = new ConfigUpdaterNew();
      configUpdater.addConfigs();
      BedwarsRevol.getInstance().saveConfiguration();
      BedwarsRevol.getInstance().loadConfigInUTF();
      BedwarsRevol.getInstance().loadShop();

//      if (BedwarsRevol.getInstance().isHologramsEnabled()
//          && BedwarsRevol.getInstance().getHolographicInteractor() != null) {
//        BedwarsRevol.getInstance().getHolographicInteractor().loadHolograms();
//      }

      BedwarsRevol.getInstance().reloadLocalization();
      BedwarsRevol.getInstance().getGameManager().reloadGames();
    } else if (command.equalsIgnoreCase("shop")) {
      BedwarsRevol.getInstance().loadShop();
    } else if (command.equalsIgnoreCase("games")) {
      BedwarsRevol.getInstance().getGameManager().reloadGames();
//    } else if (command.equalsIgnoreCase("holo")) {
//      if (BedwarsRevol.getInstance().isHologramsEnabled()) {
//        BedwarsRevol.getInstance().getHolographicInteractor().loadHolograms();
//      }
    } else if (command.equalsIgnoreCase("config")) {
      // save default config
      if (!config.exists()) {
        BedwarsRevol.getInstance().saveDefaultConfig();
      }

      BedwarsRevol.getInstance().loadConfigInUTF();

      BedwarsRevol.getInstance().getConfig().options().copyDefaults(true);
      BedwarsRevol.getInstance().getConfig().options().copyHeader(true);

      ConfigUpdaterNew configUpdater = new ConfigUpdaterNew();
      configUpdater.addConfigs();
      BedwarsRevol.getInstance().saveConfiguration();
      BedwarsRevol.getInstance().loadConfigInUTF();
    } else if (command.equalsIgnoreCase("locale")) {
      BedwarsRevol.getInstance().reloadLocalization();
    } else {
      return false;
    }

    sender.sendMessage(
        ChatWriterNew.pluginMessage(ChatColor.GREEN + BedwarsRevol._l(sender, "success.reloadconfig")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "reload";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.reload.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.reload.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
