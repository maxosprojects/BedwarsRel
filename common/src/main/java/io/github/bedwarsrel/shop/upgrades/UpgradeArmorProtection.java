package io.github.bedwarsrel.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class UpgradeArmorProtection implements Upgrade {
  private static final String TYPE = "ARMOR_PROTECTION";

  private Game game;
  private Team team;
  @Getter
  private final UpgradeArmorProtectionEnum upgrade;
  @Setter
  @Getter
  private UpgradeScope scope = UpgradeScope.TEAM;
  @Setter
  @Getter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;
  @Getter
  @Setter
  private boolean permanent = false;
  @Getter
  @Setter
  private boolean multiple = false;

  public UpgradeArmorProtection(UpgradeArmorProtectionEnum upgrade) {
    this.upgrade = upgrade;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeArmorProtection item = new UpgradeArmorProtection(this.upgrade);
    item.game = game;
    item.team = team;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle == UpgradeCycle.ONCE) {
      UpgradeArmorProtection existingUpgrade = this.team.getUpgrade(UpgradeArmorProtection.class);
      if (existingUpgrade != null && !this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
        return false;
      }
      team.setUpgrade(this);
    }

    this.upgrade.equipTeam(this.team);

    if (cycle == UpgradeCycle.ONCE) {
      for (Player player : this.team.getPlayers()) {
        if (!player.isOnline()) {
          continue;
        }
        String translation = BedwarsRel
            ._l(player, "ingame.armorupgrade." + this.upgrade.getTranslationKey());
        player.sendMessage(ChatWriter.pluginMessage(
            BedwarsRel._l(player, "success.armorupgraded",
                ImmutableMap.of("upgrade", translation))));
      }
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
    return level == this.upgrade.getLevel();
  }

}
