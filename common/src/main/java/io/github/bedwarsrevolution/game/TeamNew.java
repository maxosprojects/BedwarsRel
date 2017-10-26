package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.upgrades.Upgrade;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeBaseAlarm;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@Data
@SerializableAs("Team")
public class TeamNew implements ConfigurationSerializable {
  private List<Block> chests = null;
  private TeamColorNew color = null;
  private Inventory inventory = null;
  private int maxPlayers = 0;
  private String name = null;
  private org.bukkit.scoreboard.Team scoreboardTeam = null;
  private Location spawnLocation = null;
  private Location targetFeetBlock = null;
  private Location targetHeadBlock = null;
  private Location baseLoc1;
  private Location baseLoc2;
  private Location chestLoc;
  private Map<Class<? extends Upgrade>, Upgrade> upgrades = new HashMap<>();
  private GameContext gameCtx;
  private List<PlayerContext> players = new ArrayList<>();
  private Map<String, Long> itemsLastUsed = new HashMap<>();

  public TeamNew(Map<String, Object> deserialize) {
    this.reset();
    this.setName(deserialize.get("name").toString());
    this.setMaxPlayers(Integer.parseInt(deserialize.get("maxplayers").toString()));
    this.setColor(TeamColorNew.valueOf(deserialize.get("color").toString().toUpperCase()));
    this.setSpawnLocation(UtilsNew.locationDeserialize(deserialize.get("spawn")));
    this.setChests(new ArrayList<Block>());
    this.setBaseLoc1(UtilsNew.locationDeserialize(deserialize.get("baseloc1")));
    this.setBaseLoc2(UtilsNew.locationDeserialize(deserialize.get("baseloc2")));
    this.setChestLoc(UtilsNew.locationDeserialize(deserialize.get("chestloc")));

    if (deserialize.containsKey("bedhead")) {
      this.setTargetHeadBlock(UtilsNew.locationDeserialize(deserialize.get("bedhead")));

      if (this.getTargetHeadBlock() != null && deserialize.containsKey("bedfeed")
          && this.getTargetHeadBlock().getBlock().getType().equals(Material.BED_BLOCK)) {
        this.setTargetFeetBlock(UtilsNew.locationDeserialize(deserialize.get("bedfeed")));
      }
    }
  }

  public TeamNew(String name, TeamColorNew color, int maxPlayers,
      org.bukkit.scoreboard.Team scoreboardTeam) {
    this.reset();
    this.setName(name);
    this.setColor(color);
    this.setMaxPlayers(maxPlayers);
    this.setScoreboardTeam(scoreboardTeam);
    this.setChests(new ArrayList<Block>());
  }

  public void reset() {
    Inventory inventory =
        Bukkit.createInventory(null, InventoryType.ENDER_CHEST, BedwarsRevol._l("ingame.teamchest"));
    this.setInventory(inventory);
    this.chests = new ArrayList<>();
    this.upgrades = new HashMap<>();
  }

  public void addChest(Block chestBlock) {
    this.getChests().add(chestBlock);
  }

  public void addPlayer(PlayerContext playerCtx) {

//    BedwarsPlayerJoinTeamEvent playerJoinTeamEvent = new BedwarsPlayerJoinTeamEvent(this, player);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(playerJoinTeamEvent);
//
//    if (playerJoinTeamEvent.isCancelled()) {
//      return false;
//    }

//    if (BedwarsRevol.getInstance().isSpigot()) {
//      if (this.getScoreboardTeam().getEntries().size() >= this.getMaxPlayers()) {
//        return false;
//      }
//    } else {
//      if (this.getScoreboardTeam().getPlayers().size() >= this.getMaxPlayers()) {
//        return false;
//      }
//    }

//    String displayName = player.getDisplayName();
//    String playerListName = player.getPlayerListName();
//
//    if (BedwarsRevol.getInstance().getBooleanConfig("overwrite-names", false)) {
//      displayName = this.getChatColor() + ChatColor.stripColor(player.getName());
//      playerListName = this.getChatColor() + ChatColor.stripColor(player.getName());
//    }
//
//    if (BedwarsRevol.getInstance().getBooleanConfig("teamname-on-tab", true)) {
//      playerListName = this.getChatColor() + this.getName() + ChatColor.WHITE + " | "
//          + this.getChatColor() + ChatColor.stripColor(player.getDisplayName());
//    }
//
//    BedwarsPlayerSetNameEvent playerSetNameEvent =
//        new BedwarsPlayerSetNameEvent(this, displayName, playerListName, player);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(playerSetNameEvent);
//
//    if (!playerSetNameEvent.isCancelled()) {
//      player.setDisplayName(playerSetNameEvent.getDisplayName());
//      player.setPlayerListName(playerSetNameEvent.getPlayerListName());
//    }

    this.players.add(playerCtx);
    Player player = playerCtx.getPlayer();
    if (BedwarsRevol.getInstance().isSpigot()) {
      this.getScoreboardTeam().addEntry(player.getName());
    } else {
      this.getScoreboardTeam().addPlayer(player);
    }
//
//    return true;
  }

  public ChatColor getChatColor() {
    return this.getColor().getChatColor();
  }

  public String getDisplayName() {
    return this.getScoreboardTeam().getDisplayName();
  }

  public Block getFeetTarget() {
    if (this.getTargetFeetBlock() == null) {
      return null;
    }

    this.getTargetFeetBlock().getBlock().getChunk().load(true);
    return this.getTargetFeetBlock().getBlock();
  }

  public Block getHeadTarget() {
    if (this.targetHeadBlock == null) {
      return null;
    }

    this.getTargetHeadBlock().getBlock().getChunk().load(true);
    return this.getTargetHeadBlock().getBlock();
  }

  public boolean isBedDestroyed() {
    Material targetMaterial = this.gameCtx.getTargetMaterial();

    this.getTargetHeadBlock().getBlock().getChunk().load(true);
    if (this.getTargetFeetBlock() == null) {
      return this.getTargetHeadBlock().getBlock().getType() != targetMaterial;
    }

    this.getTargetFeetBlock().getBlock().getChunk().load(true);
    return (this.getTargetHeadBlock().getBlock().getType() != targetMaterial
        && this.getTargetFeetBlock().getBlock().getType() != targetMaterial);
  }

  @SuppressWarnings("deprecation")
  public boolean isInTeam(Player p) {
    if (BedwarsRevol.getInstance().isSpigot()) {
      return this.getScoreboardTeam().hasEntry(p.getName());
    } else {
      return this.getScoreboardTeam().hasPlayer(p);
    }
  }

//  public void removeChest(Block chest) {
//    this.getChests().remove(chest);
//    if (this.getChests().size() == 0) {
//      this.setInventory(null);
//    }
//  }

  public void removePlayer(PlayerContext playerCtx) {
    Player player = playerCtx.getPlayer();
    this.players.remove(playerCtx);
    if (BedwarsRevol.getInstance().isSpigot()) {
      if (this.getScoreboardTeam().hasEntry(player.getName())) {
        this.getScoreboardTeam().removeEntry(player.getName());
      }
    } else {
      if (this.getScoreboardTeam().hasPlayer(player)) {
        this.getScoreboardTeam().removePlayer(player);
      }
    }

    if (BedwarsRevol.getInstance().getBooleanConfig("overwrite-names", false) && player.isOnline()) {
      player.setDisplayName(ChatColor.RESET + ChatColor.stripColor(player.getName()));
      player.setPlayerListName(ChatColor.RESET + player.getPlayer().getName());
    }
  }

  @Override
  public Map<String, Object> serialize() {
    HashMap<String, Object> team = new HashMap<>();

    team.put("name", this.getName());
    team.put("color", this.getColor().toString());
    team.put("maxplayers", this.getMaxPlayers());
    team.put("spawn", UtilsNew.locationSerialize(this.getSpawnLocation()));
    team.put("bedhead", UtilsNew.locationSerialize(this.getTargetHeadBlock()));
    team.put("baseloc1", UtilsNew.locationSerialize(this.baseLoc1));
    team.put("baseloc2", UtilsNew.locationSerialize(this.baseLoc2));
    team.put("chestloc", UtilsNew.locationSerialize(this.chestLoc));

    if (this.targetFeetBlock != null) {
      team.put("bedfeed", UtilsNew.locationSerialize(this.targetFeetBlock));
    }

    return team;
  }

  public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
    scoreboardTeam.setDisplayName(this.getChatColor() + this.name);
    this.scoreboardTeam = scoreboardTeam;
  }

  public void setTargets(Block headBlock, Block feetBlock) {
    this.setTargetHeadBlock(headBlock.getLocation());
    if (feetBlock != null) {
      this.setTargetFeetBlock(feetBlock.getLocation());
    } else {
      this.setTargetFeetBlock(null);
    }
  }

  public void setBaseLoc(Location loc, String type) {
    if (type.equalsIgnoreCase("loc1")) {
      this.baseLoc1 = loc;
    } else {
      this.baseLoc2 = loc;
    }
  }

  public <T extends Upgrade> T getUpgrade(Class<T> upgradeClass) {
    return (T) this.upgrades.get(upgradeClass);
  }

  public <T extends Upgrade> void setUpgrade(T upgrade) {
    this.upgrades.put(upgrade.getClass(), upgrade);
  }

  public void removeUpgrade(UpgradeBaseAlarm upgrade) {
    this.upgrades.remove(upgrade.getClass());
  }

  public List<PlayerContext> getPlayers() {
    return Collections.unmodifiableList(this.players);
  }

  public int getFunctionalPlayers() {
    int count = 0;
    for (PlayerContext playerCtx : this.players) {
      if (playerCtx.isActive() && !playerCtx.getState().isSpectator()) {
        count++;
      }
    }
    return count;
  }

  public Long getItemLastUsed(String itemName) {
    return this.itemsLastUsed.get(itemName);
  }

  public void setItemUsed(String itemName) {
    this.itemsLastUsed.put(itemName, System.currentTimeMillis());
  }
}
