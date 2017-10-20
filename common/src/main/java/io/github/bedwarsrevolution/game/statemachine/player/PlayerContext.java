package io.github.bedwarsrevolution.game.statemachine.player;

import io.github.bedwarsrevolution.game.DamageHolder;
import io.github.bedwarsrevolution.game.PlayerStorageNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.shop.Shop;
import io.github.bedwarsrevolution.shop.upgrades.Upgrade;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeCycle;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeScope;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

/**
 * Created by {maxos} 2017
 */
public class PlayerContext {

  private final PlayerStorageNew storage;
  @Getter
  private Player player;
  @Getter
  @Setter
  private PlayerState state = new PlayerStateWaitingGame(this);
  @Getter
  @Setter
  private boolean protectd;
  private Shop shop;
  @Getter
  private GameContext gameContext;
  @Getter
  private DamageHolder lastDamagedBy;
  @Getter
  @Setter
  private TeamNew team;
  @Getter
  @Setter
  private boolean teleporting;
  @Getter
  @Setter
  private boolean virtuallyAlive = true;
  private Map<Class<? extends Upgrade>, List<Upgrade>> upgrades = new HashMap<>();
  @Getter
  @Setter
  private boolean oneStackPerShift = true;

  public PlayerContext(Player player, GameContext gameContext) {
    this.player = player;
    this.gameContext = gameContext;
    this.storage = new PlayerStorageNew(player);
    this.shop = new Shop(gameContext.getShopCategories(), this);
  }

  public void setDamager(Player damager) {
    this.lastDamagedBy = new DamageHolder(damager);
  }

  public Shop getShop() {
    return shop;
  }

  public void storeInventory() {
    this.storage.store();
  }

  public void clear(boolean deep) {
    PlayerInventory inv = this.player.getInventory();
    inv.setArmorContents(new ItemStack[4]);
    inv.setContents(new ItemStack[]{});

    this.player.setExp(0.0F);
    for (PotionEffect effect : this.player.getActivePotionEffects()) {
      this.player.removePotionEffect(effect.getType());
    }
    this.player.setGameMode(GameMode.SURVIVAL);
    this.player.setAllowFlight(false);
    this.player.setFlying(false);
    this.player.setLevel(0);
    this.player.setSneaking(false);
    this.player.setSprinting(false);
    this.player.setSaturation(10);
    this.player.setExhaustion(0);
    this.player.setMaxHealth(20.0D);
    this.player.setHealth(20.0D);
    this.player.setFireTicks(0);
    this.player.setFoodLevel(20);
    this.player.updateInventory();
    if (this.player.isInsideVehicle()) {
      this.player.leaveVehicle();
    }

//      boolean teamnameOnTab = BedwarsRel.getInstance().getBooleanConfig("teamname-on-tab", true);
//      boolean overwriteNames = BedwarsRel.getInstance().getBooleanConfig("overwrite-names", false);
//
//      String displayName = this.player.getDisplayName();
//      String playerListName = this.player.getPlayerListName();
//
//      if (overwriteNames || teamnameOnTab) {
//        if (game != null) {
//
//          game.setPlayerGameMode(player);
//          Team team = game.getPlayerTeam(this.player);
//
//          if (overwriteNames) {
//            if (team != null) {
//              displayName = team.getChatColor() + ChatColor.stripColor(this.player.getName());
//            } else {
//              displayName = ChatColor.stripColor(this.player.getName());
//            }
//          }
//
//          if (teamnameOnTab) {
//            if (team != null) {
//              playerListName = team.getChatColor() + team.getName() + ChatColor.WHITE + " | "
//                  + team.getChatColor() + ChatColor.stripColor(this.player.getDisplayName());
//            } else {
//              playerListName = ChatColor.stripColor(this.player.getDisplayName());
//            }
//          }
//
//          BedwarsPlayerSetNameEvent playerSetNameEvent =
//              new BedwarsPlayerSetNameEvent(team, displayName, playerListName, player);
//          BedwarsRel.getInstance().getServer().getPluginManager().callEvent(playerSetNameEvent);
//
//          if (!playerSetNameEvent.isCancelled()) {
//            this.player.setDisplayName(playerSetNameEvent.getDisplayName());
//            this.player.setPlayerListName(playerSetNameEvent.getPlayerListName());
//          }
//        }
//      }
  }

  public void restoreInventory() {
    this.storage.restore();
    this.teleporting = true;
    this.player.teleport(this.storage.getLocation());
  }

  public void restoreLocation() {
    this.player.teleport(this.storage.getLocation());
  }

  public void respawn() {
    for (List<Upgrade> list : this.upgrades.values()) {
      for (Upgrade upgrade : list) {
        upgrade.activate(UpgradeScope.PLAYER, UpgradeCycle.RESPAWN);
      }
    }
    for (Upgrade up : this.team.getUpgrades().values()) {
      if (up.getApplyTo() == UpgradeScope.PLAYER) {
        up.activate(UpgradeScope.PLAYER, UpgradeCycle.RESPAWN);
      }
    }
    this.player.getInventory().setHeldItemSlot(0);
    this.player.updateInventory();
  }

  public void setTeleportingIfWorldChange(Location location) {
    if (!this.player.getWorld().getName().equals(location.getWorld().getName())) {
      this.teleporting = true;
    }
  }

  public <T extends Upgrade> List<T> getUpgrades(Class<T> upgradeClass) {
    return (List<T>) this.upgrades.get(upgradeClass);
  }

  /**
   * Replaces any existing upgrade of the same java class
   * @param upgrade
   * @param <T>
   */
  public <T extends Upgrade> void setUpgrade(T upgrade) {
    List<Upgrade> list = new ArrayList<>();
    list.add(upgrade);
    this.upgrades.put(upgrade.getClass(), list);
  }

  /**
   * Adds an upgrade.
   * Multiple upgrades of the same java class are possible
   * @param upgrade
   * @param <T>
   */
  public <T extends Upgrade> void addUpgrade(T upgrade) {
    List<Upgrade> list = this.upgrades.get(upgrade.getClass());
    if (list == null) {
      list = new ArrayList<>();
      this.upgrades.put(upgrade.getClass(), list);
    }
    list.add(upgrade);
  }

}