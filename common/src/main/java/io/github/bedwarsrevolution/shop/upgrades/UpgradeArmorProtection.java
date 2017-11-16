package io.github.bedwarsrevolution.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UpgradeArmorProtection extends Upgrade {
  private static final String TYPE = "ARMOR_PROTECTION";

  private GameContext gameCtx;
  private TeamNew team;
  @Getter
  private final UpgradeArmorProtectionEnum upgrade;
  @Setter
  @Getter
  private UpgradeScope scope = UpgradeScope.TEAM;
  @Setter
  @Getter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;
  @Setter
  @Getter
  private UpgradeScope applyTo = UpgradeScope.PLAYER;
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
  public Upgrade build(GameContext gameContext, TeamNew team, PlayerContext playerCtx) {
    UpgradeArmorProtection item = new UpgradeArmorProtection(this.upgrade);
    item.gameCtx = gameContext;
    item.team = team;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle == UpgradeCycle.ONCE) {
      UpgradeArmorProtection existingUpgrade = this.team.getUpgrade(
          UpgradeArmorProtection.class);
      if (existingUpgrade != null && !this.upgrade.isHigherThan(existingUpgrade.upgrade)) {
        return false;
      }
      this.team.setUpgrade(this);
    }

    this.upgrade.equipTeam(this.team);

    if (cycle == UpgradeCycle.ONCE) {
      for (PlayerContext playerCtx : this.team.getPlayers()) {
        Player player = playerCtx.getPlayer();
        if (!player.isOnline()) {
          continue;
        }
        String translation = BedwarsRevol
            ._l(player, "ingame.armorupgrade." + this.upgrade.getTranslationKey());
        player.sendMessage(ChatWriterNew.pluginMessage(
            BedwarsRevol._l(player, "success.armorupgraded",
                ImmutableMap.of("upgrade", translation))));
      }
    }

    return true;
  }

  @Override
  public boolean shouldRender(PlayerContext playerCtx) {
    UpgradeArmorProtection existing = playerCtx.getTeam().getUpgrade(UpgradeArmorProtection.class);
    // There are no upgrades purchased yet and this is tier 1
    return (existing == null && this.isLevel(1))
        // Or this is one tier higher than already purchased
        || (existing != null && existing.upgrade.ordinal() == this.upgrade.ordinal() - 1)
        // This was purchased and it is top tier
        || (existing != null && existing.upgrade == this.upgrade
            && UpgradeArmorProtectionEnum.values().length == this.upgrade.ordinal() + 1);
  }

  @Override
  public boolean alreadyOwn(PlayerContext playerCtx) {
    UpgradeArmorProtection existing = (UpgradeArmorProtection) playerCtx.getTeam()
        .getUpgrades().get(UpgradeArmorProtection.class);
    return existing != null && existing.upgrade == this.upgrade;
  }

  @Override
  public boolean isMaterial(Material type) {
    return false;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public GameContext getGame() {
    return this.gameCtx;
  }

  @Override
  public boolean isLevel(int level) {
    return level == this.upgrade.getLevel();
  }

}
