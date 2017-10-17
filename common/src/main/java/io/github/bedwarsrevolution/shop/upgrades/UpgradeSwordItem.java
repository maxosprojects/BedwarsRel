package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.entity.Player;

public class UpgradeSwordItem extends UpgradeItem {
  private static final String TYPE = "SWORD_ITEM";

  private int level;

  public UpgradeSwordItem(int level) {
    super();
    this.level = level;
  }

  @Override
  public Upgrade create(GameContext gameCtx, TeamNew team, PlayerContext playerCtx) {
    UpgradeSwordItem item = new UpgradeSwordItem(this.level);
    item.gameCtx = gameCtx;
    item.playerCtx = playerCtx;
    item.purchase = this.purchase;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    this.installItem(cycle, true);
    TeamNew team = this.playerCtx.getTeam();
    UpgradeSwordSharpness sharpness = team.getUpgrade(UpgradeSwordSharpness.class);
    if (sharpness != null) {
      sharpness.getUpgrade().equipPlayer(this.playerCtx);
    }
    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public boolean isLevel(int lev) {
    return this.level == lev;
  }

}
