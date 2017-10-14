package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.entity.Player;

public class UpgradeSwordItem extends UpgradeItem {
  private static final String TYPE = "SWORD_ITEM";

  private int level;

  public UpgradeSwordItem(int level) {
    super();
    this.level = level;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeSwordItem item = new UpgradeSwordItem(this.level);
    item.game = game;
    item.player = player;
    item.purchase = this.purchase;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    this.installItem(cycle, true);
    Team team = this.game.getPlayerTeam(this.player);
    UpgradeSwordSharpness sharpness = team.getUpgrade(UpgradeSwordSharpness.class);
    if (sharpness != null) {
      sharpness.getUpgrade().equipPlayer(this.player);
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
