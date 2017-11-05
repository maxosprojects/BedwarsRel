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

public class UpgradeSwordSharpness extends Upgrade {
  public static final String TYPE = "SWORD_SHARPNESS";

  private TeamNew team;
  @Getter
  private final UpgradeSwordSharpnessEnum upgrade;
  @Getter
  @Setter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;
  @Getter
  @Setter
  private UpgradeScope scope = UpgradeScope.TEAM;
  @Setter
  @Getter
  private UpgradeScope applyTo = UpgradeScope.PLAYER;
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
  public Upgrade build(GameContext gameContext, TeamNew team, PlayerContext playerCtx) {
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
      if (existingUpgrade != null && !this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
        return false;
      }
      team.setUpgrade(this);
    }

    this.upgrade.equipTeam(this.team);

    if (cycle == UpgradeCycle.ONCE) {
      for (PlayerContext playerCtx : this.team.getPlayers()) {
        Player player = playerCtx.getPlayer();
        if (!player.isOnline()) {
          continue;
        }
        String translation = BedwarsRevol
            ._l(player, "ingame.swordupgrade." + this.upgrade.getTranslationKey());
        player.sendMessage(ChatWriterNew.pluginMessage(
            BedwarsRevol._l(player, "success.swordsupgraded",
                ImmutableMap.of("upgrade", translation))));
      }
    }

    return true;
  }

  @Override
  public boolean alreadyOwn(PlayerContext playerCtx) {
    UpgradeSwordSharpness existing = playerCtx.getTeam().getUpgrade(UpgradeSwordSharpness.class);
    return existing != null && existing.upgrade == this.upgrade;
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
