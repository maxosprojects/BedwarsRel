package io.github.bedwarsrevolution.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameStateOld;
import io.github.bedwarsrel.game.Region;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GameManagerReworked {
  public static String gamesPath = "gamesContexts";
  private Map<Player, GameContext> playerToGame = null;
  private List<GameContext> gamesContexts = null;

  public GameManagerReworked() {
    this.gamesContexts = new ArrayList<>();
    this.playerToGame = new HashMap<>();
  }

  public GameContext addGame(String name) {
    GameContext existing = this.getGameContext(name);
    if (existing != null) {
      return null;
    }

    GameContext newCtx = new GameContext(name);
    this.gamesContexts.add(newCtx);
    return newCtx;
  }

  public void addGamePlayer(Player player, Game game) {
    if (this.playerToGame.containsKey(player)) {
      this.playerToGame.remove(player);
    }

    this.playerToGame.put(player, game);
  }

  public GameContext getGameContext(String name) {
    for (GameContext ctx : this.gamesContexts) {
      if (ctx.getName().equals(name)) {
        return ctx;
      }
    }
    return null;
  }

  public Game getGameByChunkLocation(int x, int z) {
    for (Game game : this.gamesContexts) {
      if (game.getRegion().chunkIsInRegion(x, z)) {
        return game;
      }
    }

    return null;
  }

  public Game getGameByLocation(Location loc) {
    for (Game game : this.gamesContexts) {
      if (game.getRegion() == null) {
        continue;
      }

      if (game.getRegion().getWorld() == null) {
        continue;
      }

      if (game.getRegion().isInRegion(loc)) {
        return game;
      }
    }

    return null;
  }

  public GameContext getGameBySignLocation(Location location) {
    for (Game game : this.gamesContexts) {
      if (game.getSigns().containsKey(location)) {
        return game;
      }
    }

    return null;
  }

  public GameContext getGameOfPlayer(Player player) {
    return this.playerToGame.get(player);
  }

  public int getGamePlayerAmount() {
    return this.playerToGame.size();
  }

  public ArrayList<Game> getGamesContexts() {
    return this.gamesContexts;
  }

  public List<Game> getGamesByWorld(World world) {
    List<Game> games = new ArrayList<Game>();

    for (Game game : this.gamesContexts) {
      if (game.getRegion() == null) {
        continue;
      }

      if (game.getRegion().getWorld() == null) {
        continue;
      }

      if (game.getRegion().getWorld().equals(world)) {
        games.add(game);
      }
    }

    return games;
  }

  @SuppressWarnings("unchecked")
  private void loadGame(File configFile) {
    try {

      YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
      String name = cfg.get("name").toString();
      if (name.isEmpty()) {
        return;
      }

      Game game = new Game(name);
      game.setConfig(cfg);

      Map<String, Object> teams = new HashMap<String, Object>();
      Map<String, Object> spawner = new HashMap<String, Object>();
      String targetMaterialObj = null;

      if (cfg.contains("teams")) {
        teams = cfg.getConfigurationSection("teams").getValues(false);
      }

      if (cfg.contains("spawner")) {
        if (cfg.isConfigurationSection("spawner")) {
          spawner = cfg.getConfigurationSection("spawner").getValues(false);

          for (Object obj : spawner.values()) {
            if (!(obj instanceof ResourceSpawner)) {
              continue;
            }

            ResourceSpawner rs = (ResourceSpawner) obj;
            rs.setGame(game);
            game.addResourceSpawner(rs);
          }
        }

        if (cfg.isList("spawner")) {
          for (Object rs : cfg.getList("spawner")) {
            if (!(rs instanceof ResourceSpawner)) {
              continue;
            }

            ResourceSpawner rsp = (ResourceSpawner) rs;
            rsp.setGame(game);
            game.addResourceSpawner(rsp);
          }
        }
      }

      for (Object obj : teams.values()) {
        if (!(obj instanceof Team)) {
          continue;
        }

        game.addTeam((Team) obj);
      }

      Location loc1 = Utils.locationDeserialize(cfg.get("loc1"));
      Location loc2 = Utils.locationDeserialize(cfg.get("loc2"));

      File signFile = new File(BedwarsRevol.getInstance().getDataFolder() + File.separator
          + GameManagerReworked.gamesPath + File.separator + game.getName(), "sign.yml");
      if (signFile.exists()) {
        YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);

        List<Object> signs = (List<Object>) signConfig.get("signs");
        for (Object sign : signs) {
          Location signLocation = Utils.locationDeserialize(sign);
          if (signLocation == null) {
            continue;
          }

          signLocation.getChunk().load(true);

          Block signBlock = signLocation.getBlock();
          if (!(signBlock.getState() instanceof Sign)) {
            continue;
          }

          signBlock.getState().update(true, true);
          game.addJoinSign(signBlock.getLocation());
        }
      }

      game.setLoc(loc1, "loc1");
      game.setLoc(loc2, "loc2");
      game.setLobby(Utils.locationDeserialize(cfg.get("lobby")));

      String regionName = "";

      if (loc1.getWorld() != null) {
        regionName = loc1.getWorld().getName();
      }

      if (cfg.contains("regionname")) {
        regionName = cfg.getString("regionname");
      }

      if (cfg.contains("time") && cfg.isInt("time")) {
        game.setTime(cfg.getInt("time"));
      }

      game.setRegionName(regionName);
      game.setRegion(new Region(loc1, loc2, regionName));

      if (cfg.contains("autobalance")) {
        game.setAutobalance(cfg.getBoolean("autobalance"));
      }

      if (cfg.contains("hunger-enabled")) {
        game.setHungerEnabled(cfg.getBoolean("hunger-enabled"));
      }

      if (cfg.contains("minplayers")) {
        game.setMinPlayers(cfg.getInt("minplayers"));
      }

      if (cfg.contains("mainlobby")) {
        game.setMainLobby(Utils.locationDeserialize(cfg.get("mainlobby")));
      }

      if (cfg.contains("record")) {
        game.setRecord(cfg.getInt("record", BedwarsRevol.getInstance().getMaxLength()));
      }

      if (cfg.contains("targetmaterial")) {
        targetMaterialObj = cfg.getString("targetmaterial");
        if (targetMaterialObj != null && !targetMaterialObj.equals("")) {
          game.setTargetMaterial(Utils.parseMaterial(targetMaterialObj));
        }
      }

      if (cfg.contains("builder")) {
        game.setBuilder(cfg.getString("builder"));
      }

      if (cfg.contains("record-holders")) {
        List<Object> list = (List<Object>) cfg.getList("record-holders", new ArrayList<Object>());
        for (Object holder : list) {
          game.addRecordHolder(holder.toString());
        }
      }

      game.getFreePlayers().clear();
      game.updateSigns();

      this.gamesContexts.add(game);
      BedwarsRevol.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRevol
              ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(), "success.gameloaded",
                  ImmutableMap.of("game", game.getRegion().getName()))));
    } catch (Exception ex) {
      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      BedwarsRevol.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRevol
              ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(), "errors.gameloaderror",
                  ImmutableMap.of("game", configFile.getParentFile().getName()))));
    }
  }

  public void loadGames() {
    String path = BedwarsRevol.getInstance().getDataFolder() + File.separator + GameManagerReworked.gamesPath;
    File file = new File(path);

    if (!file.exists()) {
      return;
    }

    File[] files = file.listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return pathname.isDirectory();
      }
    });

    if (files.length > 0) {
      for (File dir : files) {
        File[] configFiles = dir.listFiles();
        for (File cfg : configFiles) {
          if (!cfg.isFile()) {
            continue;
          }

          if (cfg.getName().equals("game.yml")) {
            this.loadGame(cfg);
          }
        }
      }
    }

    for (Game g : this.gamesContexts) {
      if (!g.run(BedwarsRevol.getInstance().getServer().getConsoleSender())) {
        BedwarsRevol.getInstance().getServer().getConsoleSender()
            .sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRevol
                ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(),
                    "errors.gamenotloaded")));
      } else {
        g.getCycle().onGameLoaded();
      }
    }
  }

  public void reloadGames() {
    this.unloadGames();

    this.playerToGame.clear();
    this.loadGames();
  }

  public void removeGame(Game game) {
    if (game == null) {
      return;
    }

    File configs = new File(BedwarsRevol.getInstance().getDataFolder() + File.separator
        + GameManagerReworked.gamesPath + File.separator + game.getName());

    if (configs.exists()) {
      configs.delete();
    }

    this.gamesContexts.remove(game);
  }

  public void removePlayer(PlayerContext playerCtx) {
    GameContext ctx = this.playerToGame.remove(playerCtx.getPlayer());
    ctx.removePlayer(playerCtx);
  }

  public void unloadGame(Game game) {
    if (game.getState() != GameStateOld.STOPPED) {
      game.stop();
    }

    game.setState(GameStateOld.STOPPED);
    game.setScoreboard(BedwarsRevol.getInstance().getScoreboardManager().getNewScoreboard());

    try {
      game.kickAllPlayers();
    } catch (Exception e) {
      BedwarsRevol.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }
    game.resetRegion();
    game.updateSigns();
  }

  public void unloadGames() {
    for (Game g : this.gamesContexts) {
      this.unloadGame(g);
    }

    this.gamesContexts.clear();
  }

}
