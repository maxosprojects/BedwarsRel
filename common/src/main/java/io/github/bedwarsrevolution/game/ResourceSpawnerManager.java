package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by {maxos} 2017
 */
public class ResourceSpawnerManager {
  private List<ResourceSpawnerNew> resourceSpawners = new ArrayList<>();
  private Set<Material> types = new HashSet<>();
  private GameContext ctx;
  private int yCount = -25;
  private double yCoef = 1;
  private int yawCount = -25;
  private int yawCoef = 1;

  public void remove() {
    for (ResourceSpawnerNew spawner : this.resourceSpawners) {
      spawner.remove();
    }
  }

  public void add(ResourceSpawnerNew rs) {
    this.resourceSpawners.add(rs);
    for (ItemStack item : rs.getResources()) {
      types.add(item.getType());
    }
  }

  public GameCheckResult check() {
    if (this.resourceSpawners.size() == 0) {
      return GameCheckResult.NO_RES_SPAWNER_ERROR;
    }
    return GameCheckResult.OK;
  }

  public Set<Material> getTypes() {
    return Collections.unmodifiableSet(this.types);
  }

  public void loadChunks() {
    for (ResourceSpawnerNew spawner : this.resourceSpawners) {
      spawner.getLocation().getChunk().load();
      spawner.getLocation().clone().add(0, 1, 0).getChunk().load();
      spawner.getLocation().clone().add(0, 2, 0).getChunk().load();
    }
  }

  public void start(GameContext ctx) {
    this.ctx = ctx;
    for (ResourceSpawnerNew rs : this.resourceSpawners) {
      rs.setCtx(this.ctx);
      rs.restart(rs.getInterval());
    }
    this.ctx.addRunningTask(new BukkitRunnable() {
      @Override
      public void run() {
        ResourceSpawnerManager.this.update();
      }
    }.runTaskTimer(BedwarsRevol.getInstance(), 0, 1));
  }

  private void update() {
    if (this.yCount > 24) {
      yCoef = -1;
    } else if (this.yCount < -24) {
      yCoef = 1;
    }
    if (this.yawCount > 24) {
      yawCoef = -1;
    } else if (this.yawCount < -24) {
      yawCoef = 1;
    }
    this.yCount += yCoef;
    this.yawCount += this.yawCoef;
    double dyFromOrigin = this.yCount * this.yCount * yCoef / 2500.0D - yCoef * 0.25D;
    float yaw = this.yawCount * this.yawCount * yawCoef / 1.7361f;
    if (yawCoef == -1) {
      yaw += 719f;
    }
//    float yaw = this.yawCount;
//    System.out.println(yaw);
    for (ResourceSpawnerNew spawner : ResourceSpawnerManager.this.resourceSpawners) {
      spawner.update(dyFromOrigin, yaw);
    }
  }

  public void restart(Map<String, Integer> intervals, TeamNew team) {
    for (ResourceSpawnerNew spawner : this.resourceSpawners) {
      Integer interval = intervals.get(spawner.getName());
      if (interval != null
          && (team == null || team.getName().equals(spawner.getTeam()))) {
          spawner.restart(interval);
      }
    }
  }
}
