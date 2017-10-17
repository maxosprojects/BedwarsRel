package io.github.bedwarsrevolution.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GameManagerNew {
  public static String gamesPath = "gamesContexts";
  private Map<Player, GameContext> playerToGame = null;
  private List<GameContext> gamesContexts = null;

  public GameManagerNew() {
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

  public GameContext getGameContext(String name) {
    for (GameContext ctx : this.gamesContexts) {
      if (ctx.getName().equals(name)) {
        return ctx;
      }
    }
    return null;
  }

//  public Game getGameByChunkLocation(int x, int z) {
//    for (Game game : this.gamesContexts) {
//      if (game.getRegion().chunkIsInRegion(x, z)) {
//        return game;
//      }
//    }
//
//    return null;
//  }
//
//  public Game getGameByLocation(Location loc) {
//    for (Game game : this.gamesContexts) {
//      if (game.getRegion() == null) {
//        continue;
//      }
//
//      if (game.getRegion().getWorld() == null) {
//        continue;
//      }
//
//      if (game.getRegion().isInRegion(loc)) {
//        return game;
//      }
//    }
//
//    return null;
//  }

  public GameContext getGameBySignLocation(Location location) {
    for (GameContext ctx : this.gamesContexts) {
      if (ctx.getJoinSigns().containsKey(location)) {
        return ctx;
      }
    }

    return null;
  }

  public GameContext getGameOfPlayer(Player player) {
    return this.playerToGame.get(player);
  }

//  public int getGamePlayerAmount() {
//    return this.playerToGame.size();
//  }

  public List<GameContext> getGamesContexts() {
    return this.gamesContexts;
  }

//  public List<Game> getGamesByWorld(World world) {
//    List<Game> games = new ArrayList<Game>();
//
//    for (Game game : this.gamesContexts) {
//      if (game.getRegion() == null) {
//        continue;
//      }
//
//      if (game.getRegion().getWorld() == null) {
//        continue;
//      }
//
//      if (game.getRegion().getWorld().equals(world)) {
//        games.add(game);
//      }
//    }
//
//    return games;
//  }

  @SuppressWarnings("unchecked")
  private void loadGame(File configFile) {
    try {

      YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
      String name = cfg.get("name").toString();
      if (name.isEmpty()) {
        return;
      }

      GameContext ctx = new GameContext(name);
      ctx.setConfig(cfg);

      Map<String, Object> teams = new HashMap<>();
      Map<String, Object> spawner = new HashMap<>();
      String targetMaterialObj = null;

      if (cfg.contains("teams")) {
        teams = cfg.getConfigurationSection("teams").getValues(false);
      }

      if (cfg.contains("spawner")) {
        if (cfg.isConfigurationSection("spawner")) {
          spawner = cfg.getConfigurationSection("spawner").getValues(false);

          for (Object obj : spawner.values()) {
            if (!(obj instanceof ResourceSpawnerNew)) {
              continue;
            }

            ResourceSpawnerNew rs = (ResourceSpawnerNew) obj;
            rs.setCtx(ctx);
            ctx.addResourceSpawner(rs);
          }
        }

        if (cfg.isList("spawner")) {
          for (Object rs : cfg.getList("spawner")) {
            if (!(rs instanceof ResourceSpawnerNew)) {
              continue;
            }

            ResourceSpawnerNew rsp = (ResourceSpawnerNew) rs;
            rsp.setCtx(ctx);
            ctx.addResourceSpawner(rsp);
          }
        }
      }

      for (Object obj : teams.values()) {
        if (!(obj instanceof TeamNew)) {
          continue;
        }

        ctx.addTeam((TeamNew) obj);
      }

      Location loc1 = UtilsNew.locationDeserialize(cfg.get("loc1"));
      Location loc2 = UtilsNew.locationDeserialize(cfg.get("loc2"));

      File signFile = new File(BedwarsRevol.getInstance().getDataFolder() + File.separator
          + GameManagerNew.gamesPath + File.separator + ctx.getName(), "sign.yml");
      if (signFile.exists()) {
        YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);

        List<Object> signs = (List<Object>) signConfig.get("signs");
        for (Object sign : signs) {
          Location signLocation = UtilsNew.locationDeserialize(sign);
          if (signLocation == null) {
            continue;
          }

          signLocation.getChunk().load(true);

          Block signBlock = signLocation.getBlock();
          if (!(signBlock.getState() instanceof Sign)) {
            continue;
          }

          signBlock.getState().update(true, true);
          ctx.addJoinSign(signBlock.getLocation());
        }
      }

      ctx.setLoc(loc1, "loc1");
      ctx.setLoc(loc2, "loc2");
      ctx.setLobby(UtilsNew.locationDeserialize(cfg.get("lobby")));

      String regionName = "";

      if (loc1.getWorld() != null) {
        regionName = loc1.getWorld().getName();
      }

      if (cfg.contains("regionname")) {
        regionName = cfg.getString("regionname");
      }

      if (cfg.contains("time") && cfg.isInt("time")) {
        ctx.setTime(cfg.getInt("time"));
      }

      ctx.setRegionName(regionName);
      ctx.setRegion(new RegionNew(loc1, loc2, regionName));

      if (cfg.contains("autobalance")) {
        ctx.setAutobalance(cfg.getBoolean("autobalance"));
      }

      if (cfg.contains("hunger-enabled")) {
        ctx.setHungerEnabled(cfg.getBoolean("hunger-enabled"));
      }

      if (cfg.contains("minplayers")) {
        ctx.setMinPlayers(cfg.getInt("minplayers"));
      }

      if (cfg.contains("mainlobby")) {
        ctx.setMainLobby(UtilsNew.locationDeserialize(cfg.get("mainlobby")));
      }

//      if (cfg.contains("record")) {
//        ctx.setRecord(cfg.getInt("record", BedwarsRevol.getInstance().getMaxLength()));
//      }

      if (cfg.contains("targetmaterial")) {
        targetMaterialObj = cfg.getString("targetmaterial");
        if (targetMaterialObj != null && !targetMaterialObj.equals("")) {
          ctx.setTargetMaterial(UtilsNew.parseMaterial(targetMaterialObj));
        }
      }

      if (cfg.contains("builder")) {
        ctx.setBuilder(cfg.getString("builder"));
      }

//      if (cfg.contains("record-holders")) {
//        List<Object> list = (List<Object>) cfg.getList("record-holders", new ArrayList<Object>());
//        for (Object holder : list) {
//          ctx.addRecordHolder(holder.toString());
//        }
//      }

//      ctx.getFreePlayers().clear();
      ctx.updateSigns();

      this.gamesContexts.add(ctx);
      BedwarsRevol.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriterNew.pluginMessage(ChatColor.GREEN + BedwarsRevol
              ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(), "success.gameloaded",
                  ImmutableMap.of("game", ctx.getRegion().getName()))));
    } catch (Exception ex) {
      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      BedwarsRevol.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
              ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(), "errors.gameloaderror",
                  ImmutableMap.of("game", configFile.getParentFile().getName()))));
    }
  }

  public void loadGames() {
    String path = BedwarsRevol.getInstance().getDataFolder() + File.separator + GameManagerNew.gamesPath;
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

    for (GameContext ctx : this.gamesContexts) {
      if (ctx.start(BedwarsRevol.getInstance().getServer().getConsoleSender())) {
//        ctx.getCycle().onGameLoaded();
      } else {
        BedwarsRevol.getInstance().getServer().getConsoleSender()
            .sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
                ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(),
                    "errors.gamenotloaded")));
      }
    }
  }

  public void reloadGames() {
    this.stopGames();

    this.playerToGame.clear();
    this.loadGames();
  }

  public void removeGame(GameContext game) {
    if (game == null) {
      return;
    }

    File configs = new File(BedwarsRevol.getInstance().getDataFolder() + File.separator
        + GameManagerNew.gamesPath + File.separator + game.getName());

    if (configs.exists()) {
      configs.delete();
    }

    this.gamesContexts.remove(game);
  }

  public void removePlayer(PlayerContext playerCtx) {
    GameContext ctx = this.playerToGame.remove(playerCtx.getPlayer());
    ctx.removePlayer(playerCtx);
  }

  public void stopGames() {
    for (GameContext ctx : this.gamesContexts) {
      ctx.stop();
//      ctx.setScoreboard(BedwarsRevol.getInstance().getScoreboardManager().getNewScoreboard());

//      try {
//        game.kickAllPlayers();
//      } catch (Exception e) {
//        BedwarsRevol.getInstance().getBugsnag().notify(e);
//        e.printStackTrace();
//      }
    }
    this.gamesContexts.clear();
  }

  public void playerJoined(Player player, GameContext ctx) {
    if (this.playerToGame.containsKey(player)) {
      this.playerToGame.remove(player);
    }
    this.playerToGame.put(player, ctx);
  }

}
