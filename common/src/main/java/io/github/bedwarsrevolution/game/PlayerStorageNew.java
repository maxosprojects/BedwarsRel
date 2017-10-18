package io.github.bedwarsrevolution.game;

import java.util.Collection;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Stores player state (inventory, health...) when player joins game and restores
 * it back when player leaves
 */
public class PlayerStorageNew {

  private final Player player;
  private ItemStack[] inventory = null;
  private ItemStack[] armor = null;
  private float xp = 0.0F;
  private Collection<PotionEffect> effects = null;
  private GameMode mode = null;
  private boolean allowFlight;
  private boolean flying;
  @Getter
  private Location location = null;
  private int level = 0;
  private String listName = null;
  private String displayName = null;
  private float saturation;
  private float exhaustion;
  private double maxHealth;
  private double health;
  private int fireticks;
  private int foodLevel = 0;

  public PlayerStorageNew(Player player) {
    this.player = player;
  }

  public void restore() {
    this.player.getInventory().setContents(this.inventory);
    this.player.getInventory().setArmorContents(this.armor);
    this.player.setExp(this.xp);
    for (PotionEffect effect : this.player.getActivePotionEffects()) {
      this.player.removePotionEffect(effect.getType());
    }
    this.player.addPotionEffects(this.effects);
    this.player.setGameMode(this.mode);
    this.player.setAllowFlight(this.allowFlight);
    this.player.setFlying(this.flying);
    this.player.setLevel(this.level);
    this.player.setSaturation(this.saturation);
    this.player.setExhaustion(this.exhaustion);
    this.player.setMaxHealth(this.maxHealth);
    this.player.setHealth(this.health);
    this.player.setFireTicks(this.fireticks);
    this.player.setFoodLevel(this.foodLevel);
    this.player.setPlayerListName(this.listName);
    this.player.setDisplayName(this.displayName);
    this.player.updateInventory();
  }

  public void store() {
    this.inventory = this.player.getInventory().getContents();
    this.armor = this.player.getInventory().getArmorContents();
    this.xp = this.player.getExp();
    this.effects = this.player.getActivePotionEffects();
    this.mode = this.player.getGameMode();
    this.allowFlight = this.player.getAllowFlight();
    this.flying = this.player.isFlying();
    this.level = this.player.getLevel();
    this.saturation = this.player.getSaturation();
    this.exhaustion = this.player.getExhaustion();
    this.maxHealth = this.player.getMaxHealth();
    this.health = this.player.getHealth();
    this.fireticks = this.player.getFireTicks();
    this.foodLevel = this.player.getFoodLevel();
    this.listName = this.player.getPlayerListName();
    this.displayName = this.player.getDisplayName();
    this.location = this.player.getLocation();
  }

}
