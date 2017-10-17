package io.github.bedwarsrevolution.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class UpgradeArmorItems implements Upgrade {
  private static final String TYPE = "ARMOR_ITEM";

  private GameContext gameContext;
  private PlayerContext playerCtx;
  private final UpgradeArmorItemsEnum purchase;
  @Setter
  @Getter
  private UpgradeScope scope = UpgradeScope.PLAYER;
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

  public UpgradeArmorItems(UpgradeArmorItemsEnum purchase) {
    this.purchase = purchase;
  }

  @Override
  public boolean isLevel(int level) {
    return this.purchase.ordinal() == level;
  }

  @Override
  public Upgrade create(GameContext gameContext, TeamNew team, PlayerContext playerCtx) {
    UpgradeArmorItems item = new UpgradeArmorItems(this.purchase);
    item.gameContext = gameContext;
    item.playerCtx = playerCtx;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle == UpgradeCycle.ONCE) {
      List<UpgradeArmorItems> existing = this.playerCtx.getUpgrades(UpgradeArmorItems.class);
      if (existing != null) {
        for (UpgradeArmorItems item : existing) {
          if (!this.purchase.isHigherThan(item.purchase)) {
            return false;
          }
        }
      }
      playerCtx.setUpgrade(this);
    }

    TeamNew team = this.playerCtx.getTeam();
    this.purchase.equipPlayer(this.playerCtx, team);

    Player player = this.playerCtx.getPlayer();
    if (cycle == UpgradeCycle.ONCE) {
      String translation = BedwarsRevol
          ._l(player, "ingame.armor." + this.purchase.getTranslationKey());
      player.sendMessage(ChatWriterNew.pluginMessage(
          BedwarsRevol._l(player, "success.armorpurchased",
              ImmutableMap.of("type", translation))));
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

}
