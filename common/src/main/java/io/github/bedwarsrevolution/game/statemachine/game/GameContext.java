package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.GameJoinSign;
import io.github.bedwarsrel.game.GameLobbyCountdown;
import io.github.bedwarsrel.game.Region;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.RespawnProtectionRunnable;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.MerchantCategory;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrevolution.game.GameManagerReworked;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by {maxos} 2017
 */
public class GameContext {
  public static final int MAX_OBJECTIVE_DISPLAY_LENGTH = 32;
  public static final int MAX_SCORE_LENGTH = 40;

  @Getter
  @Setter
  private GameState state = new GameStateWaiting();
  private boolean autobalance = false;
  private boolean hungerEnabled = false;
  private String builder = null;
  private YamlConfiguration config = null;
  private GameLobbyCountdown gameLobbyCountdown = null;
  private Location hologramLocation = null;
  private Map<Location, GameJoinSign> joinSigns = new HashMap<>();
  private int length = 0;
  @Getter
  private Location lobby = null;
  private Location loc1 = null;
  private Location loc2 = null;
  private Location mainLobby = null;
  private int minPlayers = 0;
  @Getter
  private String name = null;
  @Getter
  private List<MerchantCategory> shopCategories = null;
  private Map<Player, PlayerContext> playerContexts = new HashMap<>();
  private int record = 0;
  private List<String> recordHolders = new ArrayList<>();
  @Getter
  private Region region = null;
  private List<ResourceSpawner> resourceSpawners = new ArrayList<>();
  private Map<Player, RespawnProtectionRunnable> respawnProtections = new HashMap<>();
  private List<BukkitTask> runningTasks = new ArrayList<>();
  @Getter
  private Scoreboard scoreboard = null;
  private Material targetMaterial = null;
  private Map<String, Team> teams = new HashMap<>();
  private int time = 1000;
  private int timeLeft = 0;
  private List<Map<String, Object>> defaultUpgrades;

  public GameContext(String name) {
    this.name = name;
    this.scoreboard = BedwarsRel.getInstance().getScoreboardManager().getNewScoreboard();
    this.timeLeft = BedwarsRel.getInstance().getMaxLength();
    this.record = BedwarsRel.getInstance().getMaxLength();
    this.length = BedwarsRel.getInstance().getMaxLength();
    this.autobalance = BedwarsRel.getInstance().getBooleanConfig("global-autobalance", false);

    if (BedwarsRel.getInstance().isBungee()) {
//      this.cycle = new BungeeGameCycle(this);
    } else {
//      this.cycle = new SingleGameCycle(this);
    }
  }

  public PlayerContext addPlayer(Player player) {
    PlayerContext playerCtx = new PlayerContext(player, this);
    this.playerContexts.put(player, playerCtx);
    return playerCtx;
  }

  public PlayerContext getPlayerContext(Player player) {
    return this.playerContexts.get(player);
  }

  public Team getTeamByDyeColor(DyeColor color) {
    for (Team t : this.teams.values()) {
      if (t.getColor().getDyeColor().equals(color)) {
        return t;
      }
    }

    return null;
  }

  public Material getTargetMaterial() {
    if (this.targetMaterial == null) {
      return Utils.getMaterialByConfig("game-block", Material.BED_BLOCK);
    }
    return this.targetMaterial;
  }

  public boolean isFull() {
    return this.playerContexts.size() >= this.getMaxPlayers();
  }

  public int getMaxPlayers() {
    int maxPlayers = 0;
    for (Team team : this.teams.values()) {
      maxPlayers += team.getMaxPlayers();
    }
    return maxPlayers;
  }

  public List<PlayerContext> getNonVipPlayers() {
    List<PlayerContext> nonVip = new ArrayList<>();
    for (PlayerContext ctx : this.playerContexts.values()) {
      Player player = ctx.getPlayer();
      if (!player.hasPermission("bw.vip.joinfull")
          && !player.hasPermission("bw.vip.forcestart")
          && !player.hasPermission("bw.vip")) {
        nonVip.add(ctx);
      }
    }
    return nonVip;
  }

  public Collection<PlayerContext> getPlayers() {
    return this.playerContexts.values();
  }

  public Team getLowestTeam() {
    Team lowest = this.teams.values().iterator().next();
    for (Team team : this.teams.values()) {
      if (team.getPlayers().size() < lowest.getPlayers().size()) {
        lowest = team;
      }
    }
    return lowest;
  }

  public void removePlayer(PlayerContext playerCtx) {
    this.playerContexts.remove(playerCtx.getPlayer());
  }

  public void updateSigns() {
    boolean removedItem = false;
    Iterator<GameJoinSign> iterator = this.joinSigns.values().iterator();
    while (iterator.hasNext()) {
      GameJoinSign sign = iterator.next();
      Chunk signChunk = sign.getSign().getLocation().getChunk();
      if (!signChunk.isLoaded()) {
        signChunk.load(true);
      }
      if (sign.getSign() == null) {
        iterator.remove();
        removedItem = true;
        continue;
      }
      Block signBlock = sign.getSign().getLocation().getBlock();
      if (!(signBlock.getState() instanceof Sign)) {
        iterator.remove();
        removedItem = true;
        continue;
      }
      sign.updateSign();
    }
    if (removedItem) {
      this.updateSignConfig();
    }
  }

  private void updateSignConfig() {
    try {
      File config = new File(
          BedwarsRel.getInstance().getDataFolder() + "/"
              + GameManagerReworked.gamesPath + "/" + this.name + "/sign.yml");

      YamlConfiguration cfg = new YamlConfiguration();
      if (config.exists()) {
        cfg = YamlConfiguration.loadConfiguration(config);
      }

      List<Map<String, Object>> locList = new ArrayList<>();
      for (Location loc : this.joinSigns.keySet()) {
        locList.add(Utils.locationSerialize(loc));
      }

      cfg.set("signs", locList);
      cfg.save(config);
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      BedwarsRel.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(BedwarsRel.getInstance().getServer().getConsoleSender(), "errors.savesign")));
    }
  }

}
