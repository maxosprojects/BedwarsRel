package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Specials.Trap;
import io.github.bedwarsrel.utils.ChatWriter;
import io.github.bedwarsrel.utils.SoundMachine;
import io.github.bedwarsrel.utils.TitleWriter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Serves as a trap base-wise, i.e. when a player enters another team's base (crosses
 * the boundary of the base) that has this trap installed the trap is activated.
 * This is superior to the @{@link Trap} because it can't be jumped over nor anything
 * can be placed on top of the trap.
 */
public class UpgradeBaseAlarm implements Upgrade {
  private static final String TYPE = "ALARM";

  private List<PotionEffect> effects = null;
  private Game game = null;
  private int maxDuration = 5;
  private boolean playSound = true;
  private Team team = null;
  private int x1;
  private int z1;
  private int x2;
  private int z2;
  @Setter
  @Getter
  private UpgradeScope scope = UpgradeScope.TEAM;
  @Setter
  @Getter
  private UpgradeCycle cycle = UpgradeCycle.ONCE;
  private boolean expired = false;
  @Getter
  @Setter
  private boolean permanent = false;
  @Getter
  @Setter
  private boolean multiple = false;

  public UpgradeBaseAlarm() {
    this.effects = new ArrayList<>();
  }

  public void trigger(final Player player) {
    this.expired = true;
    try {
      ConfigurationSection section =
          BedwarsRel.getInstance().getConfig().getConfigurationSection("specials.base-alarm");

      if (section.contains("play-sound")) {
        this.playSound = section.getBoolean("play-sound");
      }

      for (Object effect : section.getList("effects")) {
        effects.add((PotionEffect) effect);

        if (((PotionEffect) effect).getDuration() / 20 > this.maxDuration) {
          this.maxDuration = ((PotionEffect) effect).getDuration() / 20;
        }
      }

      this.game.addRunningTask(new BukkitRunnable() {

        private int counter = 0;

        @Override
        public void run() {
          if (this.counter >= UpgradeBaseAlarm.this.maxDuration) {
            UpgradeBaseAlarm.this.game.removeRunningTask(this);
            this.cancel();
            return;
          }
          this.counter++;
        }
      }.runTaskTimer(BedwarsRel.getInstance(), 0L, 20L));

      if (effects.size() > 0) {
        for (PotionEffect effect : effects) {
          if (player.hasPotionEffect(effect.getType())) {
            player.removePotionEffect(effect.getType());
          }

          player.addPotionEffect(effect);
        }
      }

      player.playSound(player.getLocation(), SoundMachine.get("FUSE", "ENTITY_TNT_PRIMED"),
          Float.valueOf("1.0"), Float.valueOf("1.0"));

      for (Player aPlayer : this.team.getPlayers()) {
        if (aPlayer.isOnline()) {
          String chatMsg = ChatWriter
              .pluginMessage(BedwarsRel._l(aPlayer, "ingame.specials.trapbase.trapped"));
          String titleMsg = TitleWriter
              .pluginMessage(BedwarsRel._l(aPlayer, "ingame.specials.trapbase.trapped"));
          aPlayer.sendMessage(chatMsg);
          aPlayer.sendTitle(titleMsg, null, 10, 70, 20);
        }
      }
      if (this.playSound) {
        this.game.broadcastSound(SoundMachine.get("SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"),
            Float.valueOf("1.0"), Float.valueOf("1.0"), this.team.getPlayers());
      }

      this.team.removeUpgrade(this);
    } catch (Exception ex) {
      BedwarsRel.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
    }
  }

  @Override
  public boolean isLevel(int level) {
    return true;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeBaseAlarm item = new UpgradeBaseAlarm();

    item.game = game;
    item.team = team;

    Location loc1 = item.team.getBaseLoc1();
    Location loc2 = item.team.getBaseLoc2();
    item.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
    item.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
    item.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
    item.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle != UpgradeCycle.ONCE) {
      return false;
    }
    // Check if another instance already exists in the game for the team
    UpgradeBaseAlarm existing = this.team.getUpgrade(UpgradeBaseAlarm.class);
    if (existing != null && !existing.expired) {
      return false;
    }
    this.team.setUpgrade(this);
    for (Player player : this.team.getPlayers()) {
      player.sendMessage(ChatWriter.pluginMessage(
          BedwarsRel._l(player, "success.basealarmpurchased")));
    }
    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public boolean isLocationIn(Location loc) {
    int x = loc.getBlockX();
    int z = loc.getBlockZ();
    return (x >= x1 && x <= x2 && z >= z1 && z <= z2);
  }

  public Game getGame() {
    return this.game;
  }

  public Team getPlacedTeam() {
    return this.team;
  }

}
