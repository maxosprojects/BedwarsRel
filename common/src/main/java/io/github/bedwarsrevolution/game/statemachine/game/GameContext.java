package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.GameCheckResult;
import io.github.bedwarsrevolution.game.GameJoinSignNew;
import io.github.bedwarsrevolution.game.GameLobbyCountdownNew;
import io.github.bedwarsrevolution.game.GameManagerNew;
import io.github.bedwarsrevolution.game.RegionNew;
import io.github.bedwarsrevolution.game.ResourceSpawnerNew;
import io.github.bedwarsrevolution.game.RespawnProtectionRunnableNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.MerchantCategory;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
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
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by {maxos} 2017
 */
public class GameContext {

  public static final int MAX_OBJECTIVE_DISPLAY_LENGTH = 32;
  public static final int MAX_SCORE_LENGTH = 40;
  public static final String BED_EXISTS = "\u2714";
  public static final String BED_DESTROYED= "\u2718";

  @Getter
  @Setter
  private GameStateNew state = new GameStateWaiting(this);
  @Setter
  private boolean autobalance = false;
  @Setter
  private boolean hungerEnabled = false;
  @Setter
  private String builder = null;
  @Setter
  private YamlConfiguration config = null;
  private GameLobbyCountdownNew gameLobbyCountdown = null;
  private Location hologramLocation = null;
  @Getter
  private Map<Location, GameJoinSignNew> joinSigns = new HashMap<>();
  private int length = 0;
  @Getter
  private Location lobby = null;
  private Location loc1 = null;
  private Location loc2 = null;
  @Setter
  private Location mainLobby = null;
  @Getter
  @Setter
  private int minPlayers = 0;
  @Getter
  private String name = null;
  @Getter
  private List<MerchantCategory> shopCategories = null;
  private Map<Player, PlayerContext> playerContexts = new HashMap<>();
  private int record = 0;
  private List<String> recordHolders = new ArrayList<>();
  @Setter
  private String regionName;
  @Getter
  @Setter
  private RegionNew region = null;
  @Getter
  private List<ResourceSpawnerNew> resourceSpawners = new ArrayList<>();
  private Map<Player, RespawnProtectionRunnableNew> respawnProtections = new HashMap<>();
  private List<BukkitTask> runningTasks = new ArrayList<>();
  @Getter
  private Scoreboard scoreboard = null;
  @Setter
  private Material targetMaterial = null;
  @Getter
  private Map<String, TeamNew> teams = new HashMap<>();
  @Getter
  @Setter
  private int time = 1000;
  private List<Map<String, Object>> defaultUpgrades;

  public GameContext(String name) {
    this.name = name;
    this.scoreboard = BedwarsRevol.getInstance().getScoreboardManager().getNewScoreboard();
    this.record = BedwarsRevol.getInstance().getMaxLength();
    this.length = BedwarsRevol.getInstance().getMaxLength();
    this.autobalance = BedwarsRevol.getInstance().getBooleanConfig("global-autobalance", false);

    if (BedwarsRevol.getInstance().isBungee()) {
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

  public TeamNew getTeamByDyeColor(DyeColor color) {
    for (TeamNew t : this.teams.values()) {
      if (t.getColor().getDyeColor().equals(color)) {
        return t;
      }
    }

    return null;
  }

  public Material getTargetMaterial() {
    if (this.targetMaterial == null) {
      return UtilsNew.getMaterialByConfig("game-block", Material.BED_BLOCK);
    }
    return this.targetMaterial;
  }

  public boolean isFull() {
    return this.playerContexts.size() >= this.getMaxPlayers();
  }

  public int getMaxPlayers() {
    int maxPlayers = 0;
    for (TeamNew team : this.teams.values()) {
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

  public TeamNew getLowestTeam() {
    TeamNew lowest = this.teams.values().iterator().next();
    for (TeamNew team : this.teams.values()) {
      if (team.getPlayers().size() < lowest.getPlayers().size()) {
        lowest = team;
      }
    }
    return lowest;
  }

  public void removePlayer(PlayerContext playerCtx) {
    this.playerContexts.remove(playerCtx.getPlayer());
    TeamNew team = playerCtx.getTeam();
    if (team != null) {
      team.removePlayer(playerCtx);
    }
  }

  public void updateSigns() {
    boolean removedItem = false;
    Iterator<GameJoinSignNew> iterator = this.joinSigns.values().iterator();
    while (iterator.hasNext()) {
      GameJoinSignNew sign = iterator.next();
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
          BedwarsRevol.getInstance().getDataFolder() + "/"
              + GameManagerNew.gamesPath + "/" + this.name + "/sign.yml");

      YamlConfiguration cfg = new YamlConfiguration();
      if (config.exists()) {
        cfg = YamlConfiguration.loadConfiguration(config);
      }

      List<Map<String, Object>> locList = new ArrayList<>();
      for (Location loc : this.joinSigns.keySet()) {
        locList.add(UtilsNew.locationSerialize(loc));
      }

      cfg.set("signs", locList);
      cfg.save(config);
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      BedwarsRevol.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
              ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(), "errors.savesign")));
    }
  }

  public TeamNew getTeamOfEnderChest(Block chest) {
    for (TeamNew team : this.teams.values()) {
      if (team.getChests().contains(chest)) {
        return team;
      }
    }

    return null;
  }

  public void reset() {
//    // clear protections
//    this.clearProtections();

    // reset region
    this.resetRegion();
  }

  public void resetRegion() {
    if (this.region == null) {
      return;
    }
    this.region.reset(this);
  }

  public void stopWorkers() {
    for (BukkitTask task : this.runningTasks) {
      try {
        task.cancel();
      } catch (Exception ex) {
//        BedwarsRevol.getInstance().getBugsnag().notify(ex);
        ex.printStackTrace();
        // already cancelled
      }
    }

    this.runningTasks.clear();
  }

  public void sendTeamDeadMessage(TeamNew team) {
//    if (deathTeam.getPlayers().size() == 1 && deathTeam.isBedDestroyed(this.getGame())) {
//      for (Player aPlayer : this.getGame().getPlayers()) {
//        if (aPlayer.isOnline()) {
//          aPlayer.sendMessage(
//              ChatWriter.pluginMessage(
//                  BedwarsRevol._l(aPlayer, "ingame.team-dead", ImmutableMap.of("team",
//                      deathTeam.getChatColor() + deathTeam.getDisplayName()))));
//        }
//      }
//    }
  }

  public Location getTopMiddle() {
    return this.region.getTopMiddle();
  }

  public void addWorker(BukkitTask task) {
    this.runningTasks.add(task);
  }

  public void stop() {
    this.stopWorkers();
//    this.clearProtections();

//    try {
    for (PlayerContext playerCtx : this.getPlayers()) {
      this.state.playerLeaves(playerCtx, false);
    }
//    } catch (Exception e) {
//      BedwarsRevol.getInstance().getBugsnag().notify(e);
//      e.printStackTrace();
//    }
    this.resetRegion();
    this.state = new GameStateStopped(this);
    this.updateSigns();
  }

  public void addResourceSpawner(ResourceSpawnerNew rs) {
    this.resourceSpawners.add(rs);
  }

  public void addTeam(TeamNew team) {
    org.bukkit.scoreboard.Team newTeam = this.scoreboard.registerNewTeam(team.getName());
    newTeam.setDisplayName(team.getName());
    newTeam.setPrefix(team.getChatColor().toString());
    team.setScoreboardTeam(newTeam);
    this.teams.put(team.getName(), team);
  }

  public void addJoinSign(Location location) {
    if (this.joinSigns.containsKey(location)) {
      this.joinSigns.remove(location);
    }
    this.joinSigns.put(location, new GameJoinSignNew(this, location));
    this.updateSignConfig();
  }

  public void setLoc(Location loc, String type) {
    if (type.equalsIgnoreCase("loc1")) {
      this.loc1 = loc;
    } else {
      this.loc2 = loc;
    }
  }

  public void setLobby(Location lobby) {
    if (this.region != null) {
      if (this.region.getWorld().equals(lobby.getWorld())) {
        BedwarsRevol.getInstance().getServer().getConsoleSender().sendMessage(
            ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
                ._l(BedwarsRevol.getInstance().getServer().getConsoleSender(),
                    "errors.lobbyongameworld")));
        return;
      }
    }

    this.lobby = lobby;
  }

  public GameCheckResult checkGame() {
    if (this.loc1 == null || this.loc2 == null) {
      return GameCheckResult.LOC_NOT_SET_ERROR;
    }

    if (this.teams == null || this.teams.size() <= 1) {
      return GameCheckResult.TEAM_SIZE_LOW_ERROR;
    }

    GameCheckResult teamCheck = this.checkTeams();
    if (teamCheck != GameCheckResult.OK) {
      return teamCheck;
    }

    if (this.getResourceSpawners().size() == 0) {
      return GameCheckResult.NO_RES_SPAWNER_ERROR;
    }

    if (this.lobby == null) {
      return GameCheckResult.NO_LOBBY_SET;
    }

    if (BedwarsRevol.getInstance().toMainLobby() && this.mainLobby == null) {
      return GameCheckResult.NO_MAIN_LOBBY_SET;
    }

    return GameCheckResult.OK;
  }

  private GameCheckResult checkTeams() {
    for (TeamNew t : this.teams.values()) {
      if (t.getSpawnLocation() == null) {
        return GameCheckResult.TEAMS_WITHOUT_SPAWNS;
      }

      Material targetMaterial = this.getTargetMaterial();

      if (targetMaterial.equals(Material.BED_BLOCK)) {
        if ((t.getHeadTarget() == null || t.getFeetTarget() == null)
            || (!UtilsNew.isBedBlock(t.getHeadTarget()) || !UtilsNew.isBedBlock(t.getFeetTarget()))) {
          return GameCheckResult.TEAM_NO_WRONG_BED;
        }
      } else {
        if (t.getHeadTarget() == null) {
          return GameCheckResult.TEAM_NO_WRONG_TARGET;
        }

        if (!t.getHeadTarget().getType().equals(targetMaterial)) {
          return GameCheckResult.TEAM_NO_WRONG_TARGET;
        }
      }

      if (t.getBaseLoc1() == null || t.getBaseLoc2() == null) {
        return GameCheckResult.LOC_BASE_NOT_SET_ERROR;
      }

      if (t.getChestLoc() == null) {
        return GameCheckResult.LOC_TEAM_CHEST_NOT_SET_ERROR;
      }

    }
    return GameCheckResult.OK;
  }

  public boolean start(CommandSender sender) {
//    if (this.state != GameState.STOPPED) {
//      sender
//          .sendMessage(
//              ChatWriter
//                  .pluginMessage(ChatColor.RED + BedwarsRevol._l(sender, "errors.cantstartagain")));
//      return false;
//    }

    GameCheckResult gcc = this.checkGame();
    if (gcc != GameCheckResult.OK) {
      sender.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + gcc.getCodeMessage()));
      return false;
    }

    this.loadItemShopCategories();

    if (sender instanceof Player) {
      sender.sendMessage(
          ChatWriterNew.pluginMessage(ChatColor.GREEN + BedwarsRevol._l(sender, "success.gamerun")));
    }

    this.state = new GameStateWaiting(this);
    this.updateSigns();
    return true;
  }

  public void loadItemShopCategories() {
    this.shopCategories = MerchantCategory.loadCategories(BedwarsRevol.getInstance().getShopConfig());
  }

  public void addRunningTask(BukkitTask task) {
    this.runningTasks.add(task);
  }

  public void removeRunningTask(BukkitTask task) {
    this.runningTasks.remove(task);
  }

  public void removeRunningTask(BukkitRunnable bukkitRunnable) {
    this.runningTasks.remove(bukkitRunnable);
  }

  public void broadcastSound(Sound sound, float volume, float pitch) {
    for (PlayerContext p : this.getPlayers()) {
      Player player = p.getPlayer();
      if (player.isOnline()) {
        player.playSound(player.getLocation(), sound, volume, pitch);
      }
    }
  }

  public void broadcastSound(Sound sound, float volume, float pitch, List<PlayerContext> playerContexts) {
    for (PlayerContext p : playerContexts) {
      Player player = p.getPlayer();
      if (player.isOnline()) {
        player.playSound(player.getLocation(), sound, volume, pitch);
      }
    }
  }

  public void removeJoinSign(Location location) {
    this.joinSigns.remove(location);
    this.updateSignConfig();
  }

  public TeamNew getTeamOfBed(Block bed) {
    for (TeamNew team : this.getTeams().values()) {
      if (team.getFeetTarget() == null) {
        if (team.getHeadTarget().equals(bed)) {
          return team;
        }
      } else {
        if (team.getHeadTarget().equals(bed) || team.getFeetTarget().equals(bed)) {
          return team;
        }
      }
    }
    return null;
  }

}
