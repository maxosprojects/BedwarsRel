package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.SoundMachineNew;
import io.github.bedwarsrevolution.utils.TitleWriterNew;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Serves as a trap base-wise, i.e. when a player enters another team's base (crosses
 * the boundary of the base) that has this trap installed the trap is activated.
 * This is superior to former "Trap" because it can't be jumped over nor anything
 * can be placed on top of the trap.
 */
public class UpgradeBaseAlarm extends Upgrade {
  private static final String TYPE = "ALARM";

  private List<PotionEffect> effects = null;
  private GameContext gameCtx = null;
  private int maxDuration = 5;
  private boolean playSound = true;
  private TeamNew team = null;
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
  @Setter
  @Getter
  private UpgradeScope applyTo = UpgradeScope.TEAM;
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
          BedwarsRevol.getInstance().getConfig().getConfigurationSection("specials.base-alarm");

      if (section.contains("play-sound")) {
        this.playSound = section.getBoolean("play-sound");
      }

      for (Object effect : section.getList("effects")) {
        effects.add((PotionEffect) effect);

        if (((PotionEffect) effect).getDuration() / 20 > this.maxDuration) {
          this.maxDuration = ((PotionEffect) effect).getDuration() / 20;
        }
      }

      this.gameCtx.addRunningTask(new BukkitRunnable() {

        private int counter = 0;

        @Override
        public void run() {
          if (this.counter >= UpgradeBaseAlarm.this.maxDuration) {
            UpgradeBaseAlarm.this.gameCtx.removeRunningTask(this);
            this.cancel();
            return;
          }
          this.counter++;
        }
      }.runTaskTimer(BedwarsRevol.getInstance(), 0L, 20L));

      if (effects.size() > 0) {
        for (PotionEffect effect : effects) {
          if (player.hasPotionEffect(effect.getType())) {
            player.removePotionEffect(effect.getType());
          }

          player.addPotionEffect(effect);
        }
      }

      player.playSound(player.getLocation(), SoundMachineNew.get("FUSE", "ENTITY_TNT_PRIMED"),
          Float.valueOf("1.0"), Float.valueOf("1.0"));

      for (PlayerContext aPlayerCtx : this.team.getPlayers()) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          String chatMsg = ChatWriterNew
              .pluginMessage(BedwarsRevol._l(aPlayer, "ingame.specials.trapbase.trapped"));
          String titleMsg = TitleWriterNew
              .pluginMessage(BedwarsRevol._l(aPlayer, "ingame.specials.trapbase.trapped"));
          aPlayer.sendMessage(chatMsg);
          aPlayer.sendTitle(titleMsg, null, 10, 70, 20);
        }
      }
      if (this.playSound) {
        this.gameCtx.broadcastSound(SoundMachineNew.get("SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"),
            Float.valueOf("1.0"), Float.valueOf("1.0"), this.team.getPlayers());
      }

      this.team.removeUpgrade(this);
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
    }
  }

  @Override
  public boolean isLevel(int level) {
    return true;
  }

  @Override
  public Upgrade build(GameContext gameContext, TeamNew team, PlayerContext playerCtx) {
    UpgradeBaseAlarm item = new UpgradeBaseAlarm();

    item.gameCtx = gameContext;
    item.team = team;
    item.permanent = this.permanent;
    item.multiple = this.multiple;

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
    UpgradeBaseAlarm existing = this.team.getUpgrade(
        UpgradeBaseAlarm.class);
    if (existing != null && !existing.expired) {
      return false;
    }
    this.team.setUpgrade(this);
    for (PlayerContext playerCtx : this.team.getPlayers()) {
      Player player = playerCtx.getPlayer();
      player.sendMessage(ChatWriterNew.pluginMessage(
          BedwarsRevol._l(player, "success.basealarmpurchased")));
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

  public GameContext getGame() {
    return this.gameCtx;
  }

  public TeamNew getPlacedTeam() {
    return this.team;
  }

}
