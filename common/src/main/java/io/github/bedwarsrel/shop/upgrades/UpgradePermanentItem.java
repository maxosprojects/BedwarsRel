package io.github.bedwarsrel.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerStorage;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Set;

public class UpgradePermanentItem implements Upgrade {

  private Game game;
  private Player player;
  private final UpgradePermanentItemEnum purchase;
  @Setter
  @Getter
  private UpgradeScope scope = UpgradeScope.PLAYER;
  @Setter
  @Getter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;

  public UpgradePermanentItem(UpgradePermanentItemEnum purchase) {
    this.purchase = purchase;
  }

  @Override
  public boolean isLevel(int level) {
    return this.purchase.ordinal() == level;
  }

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradePermanentItem item = new UpgradePermanentItem(this.purchase);

    item.game = game;
    item.player = player;

    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    PlayerStorage storage = this.game.getPlayerStorage(this.player);
    List<UpgradePermanentItem> existing = storage.getUpgrades(UpgradePermanentItem.class);
    if (existing != null) {
      for (UpgradePermanentItem item : existing) {
        if (item.purchase == this.purchase) {
          return false;
        }
      }
    }

    storage.addUpgrade(this);

    this.purchase.equipPlayer(this.player);

    String translation = BedwarsRel
        ._l(player, "ingame.permanentitems." + this.purchase.getTranslationKey());
    this.player.sendMessage(ChatWriter.pluginMessage(
        BedwarsRel._l(this.player, "success.permanentitempurchased",
            ImmutableMap.of("type", translation))));

    return true;
  }

  public Game getGame() {
    return this.game;
  }

}
