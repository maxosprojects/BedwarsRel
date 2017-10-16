package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.DamageHolder;
import io.github.bedwarsrel.game.GameJoinSign;
import io.github.bedwarsrel.game.GameLobbyCountdown;
import io.github.bedwarsrel.game.Region;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.RespawnProtectionRunnable;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.MerchantCategory;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by {maxos} 2017
 */
public class GameContext {
  private static final int MAX_OBJECTIVE_DISPLAY_LENGTH = 32;
  private static final int MAX_SCORE_LENGTH = 40;

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
  private String name = null;
  // Itemshops
  private HashMap<Player, Shop> shops = new HashMap<>();
  private List<MerchantCategory> shopCategories = null;
  private Map<Player, PlayerContext> playerContexts = new HashMap<>();
  private int record = 0;
  private List<String> recordHolders = new ArrayList<>();
  private Region region = null;
  private List<ResourceSpawner> resourceSpawners = new ArrayList<>();
  private Map<Player, RespawnProtectionRunnable> respawnProtections = new HashMap<>();
  private List<BukkitTask> runningTasks = new ArrayList<>();
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

  public void addPlayer(Player player) {
    this.playerContexts.put(player, new PlayerContext(player, this));
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

}
