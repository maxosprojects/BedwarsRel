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

public class UpgradeArmorItems implements Upgrade {
  private static final String TYPE = "ARMOR_ITEM";

  private Game game;
  private Player player;
  private final UpgradeArmorItemsEnum purchase;
  @Setter
  @Getter
  private UpgradeScope scope = UpgradeScope.PLAYER;
  @Setter
  @Getter
  private UpgradeCycle cycle = UpgradeCycle.RESPAWN;
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
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeArmorItems item = new UpgradeArmorItems(this.purchase);

    item.game = game;
    item.player = player;

    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    if (cycle.gte(this.cycle)) {
      return false;
    }
    PlayerStorage storage = this.game.getPlayerStorage(this.player);
    List<UpgradeArmorItems> existing = storage.getUpgrades(UpgradeArmorItems.class);
    if (existing != null) {
      for (UpgradeArmorItems item : existing) {
        if (!this.purchase.isHigherThan(item.purchase)) {
          return false;
        }
      }
    }
    storage.setUpgrade(this);

    Team team = this.game.getPlayerTeam(this.player);
    this.purchase.equipPlayer(this.player, team);

    String translation = BedwarsRel._l(player, "ingame.armor." + this.purchase.getTranslationKey());
    this.player.sendMessage(ChatWriter.pluginMessage(
        BedwarsRel._l(this.player, "success.armorpurchased",
            ImmutableMap.of("type", translation))));

    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public Game getGame() {
    return this.game;
  }

}
