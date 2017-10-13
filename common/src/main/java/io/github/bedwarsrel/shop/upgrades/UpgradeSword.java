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

public class UpgradeSword implements Upgrade {

  private Game game;
  private Team team;
  private final UpgradeSwordEnum upgrade;
  @Getter
  @Setter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;
  @Getter
  @Setter
  private UpgradeScope scope = UpgradeScope.PLAYER;

  public UpgradeSword(UpgradeSwordEnum upgrade) {
    this.upgrade = upgrade;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeSword item = new UpgradeSword(this.upgrade);

    item.game = game;
    item.team = team;

    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    UpgradeSword existingUpgrade = this.team.getUpgrade(UpgradeSword.class);

    if (!this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
      return false;
    }

    team.setUpgrade(this);

    this.upgrade.equipTeam(this.team);

    for (Player player : this.team.getPlayers()) {
      if (!player.isOnline()) {
        continue;
      }
      String translation = BedwarsRel
          ._l(player, "ingame.swordupgrade." + this.upgrade.getTranslationKey());
      player.sendMessage(ChatWriter.pluginMessage(
          BedwarsRel._l(player, "success.swordsupgraded",
              ImmutableMap.of("upgrade", translation))));
    }

    return true;
  }

  public Game getGame() {
    return this.game;
  }

  @Override
  public boolean isLevel(int level) {
    return this.upgrade.getLevel() == level;
  }

}
