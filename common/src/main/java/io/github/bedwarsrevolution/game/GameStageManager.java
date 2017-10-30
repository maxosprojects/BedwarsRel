package io.github.bedwarsrevolution.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.NmsUtils;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by {maxos} 2017
 */
public class GameStageManager {
  private final GameContext ctx;
  private int timeLeft;
  private GameStage currentStage;
  private int timeOverall = 0;

  private enum GameStage {
    DIAMOND_II(5, "diamond", 15000, "Diamond Tier II", "&b&lDiamond Generators &6upgraded to &eTier II"),
    EMERALD_II(5, "emerald", 25000, "Emerald Tier II", "&a&lEmerald Generators &6upgraded to &eTier II"),
    DIAMOND_III(5, "diamond", 7000, "Diamond Tier III", "&b&lDiamond Generators &6upgraded to &eTier III"),
    EMERALD_III(5, "emerald", 12000, "Emerald Tier III", "&a&lEmerald Generators &6upgraded to &eTier III"),
    BED_DESTRUCTION(3, "Bed destruction") {
      @Override
      protected void end(GameStageManager manager) {
        for (TeamNew team : manager.ctx.getTeams().values()) {
          team.getHeadTarget().breakNaturally();
          if (BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_12")) {
            team.getHeadTarget().breakNaturally();
          } else {
            team.getFeetTarget().breakNaturally();
          }
        }
        this.tellAllPlayers(manager, "&c&lSudden death! All beds destroyed!");
      }
    },
    DRAGONS_ATTACK(3, "Dragons attack") {
      @Override
      protected void end(GameStageManager manager) {
        Random rnd = new Random();
        for (TeamNew team : manager.ctx.getTeams().values()) {
          Set<Player> friendlyPlayers = new HashSet<>();
          for (PlayerContext otherPlayerCtx : team.getPlayers()) {
            friendlyPlayers.add(otherPlayerCtx.getPlayer());
          }
          new DelayedDragonSpawn(manager.ctx.getTopMiddle(), friendlyPlayers, rnd)
              .runTaskLater(BedwarsRevol.getInstance(), rnd.nextInt(60));
        }
        this.tellAllPlayers(manager, "&c&lDragons attack!");
      }
    },
    TIE(4, "Tie") {
      @Override
      protected void end(GameStageManager manager) {
      }
    };

    private class DelayedDragonSpawn extends BukkitRunnable {
      private final Location loc;
      private final Set<Player> friendlyPlayers;
      private final Random rnd;

      DelayedDragonSpawn(Location defaultLocation, Set<Player> friendlyPlayers, Random rnd) {
        this.loc = defaultLocation;
        this.friendlyPlayers = friendlyPlayers;
        this.rnd = rnd;
      }
      @Override
      public void run() {
        Location respLoc = new Location(this.loc.getWorld(),
            this.improv(this.loc.getX()),
            this.loc.getY(),
            this.improv(this.loc.getZ()),
            this.improv(this.loc.getYaw()),
            this.improv(this.loc.getPitch()));
        NmsUtils.spawnCustomEnderDragon(respLoc, friendlyPlayers);
      }
      private double improv(double input) {
        return input + rnd.nextDouble() * 20.0D - 10.0D;
      }
      private float improv(float input) {
        return input + rnd.nextFloat() * 20.0F - 10.0F;
      }
    }

    @Getter
    private final int length;
    private String resourceName;
    private int newInterval;
    private final String title;
    private String msg;

    GameStage(int length, String resourceName, int newInterval, String title, String msg) {
      this(length, title);
      this.resourceName = resourceName;
      this.newInterval = newInterval;
      this.msg = msg;
    }

    GameStage(int length, String title) {
      this.length = length * 60;
      this.title = title;
    }

    public void tick(GameStageManager manager) {
      manager.timeLeft--;
      if (this.isFinished(manager)) {
        this.next(manager);
      }
    }

    private boolean isFinished(GameStageManager manager) {
      return manager.timeLeft <= 0;
    }

    private void next(GameStageManager manager) {
      if (this.ordinal() < GameStage.values().length - 1) {
        this.end(manager);
        manager.currentStage = GameStage.values()[this.ordinal() + 1];
        manager.timeLeft = manager.currentStage.length;
      }
    }

    protected void end(GameStageManager manager) {
      Map<String, Integer> map = ImmutableMap.of(this.resourceName, this.newInterval);
      manager.ctx.getResourceSpawnerManager().restart(map, null);
      this.tellAllPlayers(manager, this.msg);
    }

    public String getTitle() {
      return this.title + " in:";
    }

    public String getTime(GameStageManager manager) {
      return UtilsNew.getFormattedTime(manager.timeLeft);
    }

    protected void tellAllPlayers(GameStageManager manager, String string) {
      for (PlayerContext playerCtx : manager.ctx.getPlayers()) {
        playerCtx.getPlayer().sendMessage(ChatWriterNew.pluginMessage(string));
      }
    }
  }

  public GameStageManager(GameContext ctx) {
    this.ctx = ctx;
    this.currentStage = GameStage.DIAMOND_II;
    this.timeLeft = this.currentStage.getLength();
  }

  /**
   * To be called every second of the game
   */
  public void tick() {
    this.currentStage.tick(this);
    this.timeOverall++;
  }

  public boolean isFinished() {
    return this.currentStage.isFinished(this);
  }

  public String getTitle() {
    return this.currentStage.getTitle();
  }

  public String getTime() {
    return this.currentStage.getTime(this);
  }

  public int getPlaytime() {
    return this.timeOverall;
  }

}
