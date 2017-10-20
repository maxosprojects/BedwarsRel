package io.github.bedwarsrevolution.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class UpgradeForge implements Upgrade {
  private static final String TYPE = "FORGE";

  private GameContext gameContext;
  private TeamNew team;
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
  public Upgrade create(GameContext gameContext, TeamNew team, PlayerContext playerCtx) {
    UpgradeForge item = new UpgradeForge(this.upgrade);
    item.gameContext = gameContext;
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
    UpgradeForge existingUpgrade = this.team.getUpgrade(
        UpgradeForge.class);
    if (existingUpgrade != null && !this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
      return false;
    }
    team.setUpgrade(this);

    this.upgrade.equipTeam(this.gameContext, this.team);

    for (PlayerContext playerCtx : this.team.getPlayers()) {
      Player player = playerCtx.getPlayer();
      if (!player.isOnline()) {
        continue;
      }
      String translation = BedwarsRevol._l(player, "ingame.forgeupgrade.forge" + upgrade.ordinal());
      player.sendMessage(ChatWriterNew.pluginMessage(
          BedwarsRevol._l(player, "success.forgeupgraded",
              ImmutableMap.of("upgrade", translation))));
    }

    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public GameContext getGame() {
    return this.gameContext;
  }

  @Override
  public boolean isLevel(int level) {
    return this.upgrade.ordinal() == level;
  }

}