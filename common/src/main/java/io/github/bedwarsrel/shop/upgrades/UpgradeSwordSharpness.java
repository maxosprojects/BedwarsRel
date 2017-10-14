package io.github.bedwarsrel.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class UpgradeSwordSharpness implements Upgrade {
  public static final String TYPE = "SWORD_SHARPNESS";

  private Team team;
  @Getter
  private final UpgradeSwordSharpnessEnum upgrade;
  @Getter
  @Setter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;
  @Getter
  @Setter
  private UpgradeScope scope = UpgradeScope.PLAYER;
  @Getter
  @Setter
  private boolean permanent = false;
  @Getter
  @Setter
  private boolean multiple = false;

  public UpgradeSwordSharpness(UpgradeSwordSharpnessEnum upgrade) {
    this.upgrade = upgrade;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeSwordSharpness item = new UpgradeSwordSharpness(this.upgrade);
    item.team = team;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle == UpgradeCycle.ONCE) {
      UpgradeSwordSharpness existingUpgrade = this.team.getUpgrade(UpgradeSwordSharpness.class);
      if (!this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
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
            ._l(player, "ingame.swordupgrade." + this.upgrade.getTranslationKey());
        player.sendMessage(ChatWriter.pluginMessage(
            BedwarsRel._l(player, "success.swordsupgraded",
                ImmutableMap.of("upgrade", translation))));
      }
    }

    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public boolean isLevel(int level) {
    return this.upgrade.getLevel() == level;
  }

}
