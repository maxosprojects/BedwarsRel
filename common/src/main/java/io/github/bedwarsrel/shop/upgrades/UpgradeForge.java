package io.github.bedwarsrel.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.ShopReward;
import io.github.bedwarsrel.shop.Specials.SpecialItem;
import io.github.bedwarsrel.utils.ChatWriter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UpgradeForge implements Upgrade {
  private static final String TYPE = "FORGE";

  private Game game;
  private Team team;
  private final UpgradeForgeEnum upgrade;
  @Getter
  @Setter
  private UpgradeScope scope = UpgradeScope.TEAM;
  @Getter
  @Setter
  private UpgradeCycle cycle = UpgradeCycle.ONCE;
  @Setter
  @Getter
  private UpgradeScope applyTo = UpgradeScope.TEAM;
  @Getter
  @Setter
  private boolean permanent = false;
  @Getter
  @Setter
  private boolean multiple = false;

  public UpgradeForge(UpgradeForgeEnum upgrade) {
    this.upgrade = upgrade;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeForge item = new UpgradeForge(this.upgrade);
    item.game = game;
    item.team = team;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle != UpgradeCycle.ONCE) {
      return false;
    }
    UpgradeForge existingUpgrade = this.team.getUpgrade(UpgradeForge.class);
    if (existingUpgrade != null && !this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
      return false;
    }
    team.setUpgrade(this);

    this.upgrade.equipTeam(this.game, this.team);

    for (Player player : this.team.getPlayers()) {
      if (!player.isOnline()) {
        continue;
      }
      String translation = BedwarsRel._l(player, "ingame.forgeupgrade.forge" + upgrade.ordinal());
      player.sendMessage(ChatWriter.pluginMessage(
          BedwarsRel._l(player, "success.forgeupgraded",
              ImmutableMap.of("upgrade", translation))));
    }

    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public Game getGame() {
    return this.game;
  }

  @Override
  public boolean isLevel(int level) {
    return this.upgrade.ordinal() == level;
  }

}