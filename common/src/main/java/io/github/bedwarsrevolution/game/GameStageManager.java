package io.github.bedwarsrevolution.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.Map;
import lombok.Getter;

/**
 * Created by {maxos} 2017
 */
public class GameStageManager {
  private final GameContext ctx;
  private int timeLeft;
  private GameStage currentStage;
  private int overall = 0;

  private enum GameStage {
    DIAMOND_II(5, "diamond", 15000, "Diamond Tier II"),
    EMERALD_II(5, "emerald", 25000, "Emerald Tier II"),
    DIAMOND_III(5, "diamond", 7000, "Diamond Tier III"),
    EMERALD_III(5, "emerald", 12000, "Emerald Tier III"),
    BED_DESTRUCTION(5, "Bed destruction") {
      @Override
      protected void end(GameStageManager manager) {
        this.tellAllPlayers(manager, "Sudden death! All beds destroyed!");
      }
    },
    TIE(5, "Tie") {
      @Override
      protected void end(GameStageManager manager) {
      }
    };

    @Getter
    private final int length;
    private String resourceName;
    private int newInterval;
    private final String title;

    GameStage(int length, String resourceName, int newInterval, String title) {
      this.length = length * 60;
//      this.length = 5;
      this.resourceName = resourceName;
      this.newInterval = newInterval;
      this.title = title;
    }

    GameStage(int length, String title) {
      this.length = length;
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
      this.tellAllPlayers(manager, String.format("%s upgrade!", this.title));
    }

    public String getTitle(GameStageManager manager) {
      return this.title + " in:";
    }

    public String getTime(GameStageManager manager) {
      return UtilsNew.getFormattedTime(manager.timeLeft);
    }

    protected void tellAllPlayers(GameStageManager manager, String string) {
      for (PlayerContext playerCtx : manager.ctx.getPlayers()) {
        playerCtx.getPlayer().sendMessage(string);
      }
    }
  }

  public GameStageManager(GameContext ctx) {
    this.ctx = ctx;
    this.currentStage = GameStage.DIAMOND_II;
    this.timeLeft = this.currentStage.getLength();
  }

  public void tick() {
    this.currentStage.tick(this);
    this.overall++;
  }

  public boolean isFinished() {
    return this.currentStage.isFinished(this);
  }

  public String getTitle() {
    return this.currentStage.getTitle(this);
  }

  public String getTime() {
    return this.currentStage.getTime(this);
  }

  public int getPlaytime() {
    return this.overall;
  }

}
