package io.github.bedwarsrevolution.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SetSpawnerCommand extends BaseCommand {

  public SetSpawnerCommand(BedwarsRevol plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, List<String> args) {
//    if (!super.hasPermission(sender)) {
//      return false;
//    }
//
//    Player player = (Player) sender;
//    ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(this.getResources()));
//    String material = args.get(1).toLowerCase();
//    Game game = this.getPlugin().getGameManager().getGameContext(args.get(0));
//
//    if (game == null) {
//      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//          + BedwarsRel
//          ._l(player, "errors.gamenotfound", ImmutableMap.of("game", args.get(0)))));
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
//    if (!arguments.contains(material)) {
//      player
//          .sendMessage(
//              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//                  ._l(player, "errors.spawnerargument")));
//      return false;
//    }
//
//    Location location = player.getLocation();
//    ResourceSpawner spawner = new ResourceSpawner(game, material, location);
//    game.addResourceSpawner(spawner);
//
//    if (args.size() == 3) {
//      Team team = game.getTeam(args.get(2));
//      if (team == null) {
//        player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
//            + BedwarsRel._l(player, "errors.teamnotfound")));
//        return false;
//      }
//      spawner.setTeam(team.getName());
//    }
//    player.sendMessage(
//        ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(player, "success.spawnerset",
//            ImmutableMap.of("name", material + ChatColor.GREEN))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "ressource"};
  }

  @Override
  public String getCommand() {
    return "setspawner";
  }

  @Override
  public String getDescription() {
    return BedwarsRevol._l("commands.setspawner.desc");
  }

  @Override
  public String getName() {
    return BedwarsRevol._l("commands.setspawner.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

  private String[] getResources() {
    ConfigurationSection section =
        BedwarsRevol.getInstance().getConfig().getConfigurationSection("resource");
    if (section == null) {
      return new String[]{};
    }

    List<String> resources = new ArrayList<String>();
    for (String key : section.getKeys(false)) {
      resources.add(key.toLowerCase());
    }

    return resources.toArray(new String[resources.size()]);
  }

}
