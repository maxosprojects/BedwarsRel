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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeItem implements Upgrade {
  private static final String TYPE = "ITEM";

  @Getter
  @Setter
  protected UpgradeScope scope = UpgradeScope.PLAYER;
  @Getter
  @Setter
  protected UpgradeCycle cycle = UpgradeCycle.ONCE;
  @Setter
  @Getter
  private UpgradeScope applyTo = UpgradeScope.PLAYER;
  @Setter
  protected ItemStack purchase;
  @Getter
  @Setter
  protected boolean permanent = false;
  @Getter
  @Setter
  protected boolean multiple = false;
  protected Player player;
  protected Game game;

  @Override
  public Upgrade create(Game game, Team team, Player player) {
    UpgradeItem item = new UpgradeItem();
    item.game = game;
    item.player = player;
    item.purchase = this.purchase;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    PlayerStorage storage = this.game.getPlayerStorage(this.player);
    // Only add to storage first time (not on respawn
    if (cycle == UpgradeCycle.ONCE) {
      // If multiple not allowed check storage for any existing
      if (!this.multiple) {
        List<UpgradeItem> existing = storage.getUpgrades(UpgradeItem.class);
        if (existing != null) {
          for (UpgradeItem item : existing) {
            if (item.purchase.equals(this.purchase)) {
              return false;
            }
          }
        }
      }
      storage.addUpgrade(this);
    }

    installItem(cycle, false);

    return true;
  }

  protected void installItem(UpgradeCycle cycle, boolean forceChat) {
    Inventory inv = this.player.getInventory();
    ItemStack item = this.purchase.clone();
    inv.addItem(item);
    this.player.updateInventory();

    if (forceChat || cycle == UpgradeCycle.ONCE) {
      String name = item.getType().name();
      if (item.hasItemMeta()) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
          name = meta.getDisplayName();
        }
      }
      this.player.sendMessage(ChatWriter.pluginMessage(
          BedwarsRel._l(player, "success.itempurchased",
              ImmutableMap.of("item", name))));
    }
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public boolean isLevel(int level) {
    return true;
  }

  public void setItem(ItemStack item) {
    this.purchase = item;
  }

}
