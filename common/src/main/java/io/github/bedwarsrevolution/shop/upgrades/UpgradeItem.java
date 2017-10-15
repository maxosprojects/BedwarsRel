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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeItem extends Upgrade {
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
  protected PlayerContext playerCtx;
  protected GameContext gameCtx;

  @Override
  public Upgrade create(GameContext gameCtx, TeamNew team, PlayerContext playerCtx) {
    UpgradeItem item = new UpgradeItem();
    item.gameCtx = gameCtx;
    item.playerCtx = playerCtx;
    item.purchase = this.purchase;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    // Only add to storage first time (not on respawn
    if (cycle == UpgradeCycle.ONCE) {
      // If multiple not allowed check storage for any existing
      if (!this.multiple) {
        List<UpgradeItem> existing = this.playerCtx.getUpgrades(UpgradeItem.class);
        if (existing != null) {
          for (UpgradeItem item : existing) {
            if (item.purchase.equals(this.purchase)) {
              return false;
            }
          }
        }
      }
      this.playerCtx.addUpgrade(this);
    }

    installItem(cycle, false);

    return true;
  }

  protected void installItem(UpgradeCycle cycle, boolean forceChat) {
    Player player = this.playerCtx.getPlayer();
    Inventory inv = player.getInventory();
    ItemStack item = this.purchase.clone();
    inv.addItem(item);
    player.getPlayer().updateInventory();

    if (forceChat || cycle == UpgradeCycle.ONCE) {
      String name = item.getType().name();
      if (item.hasItemMeta()) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
          name = meta.getDisplayName();
        }
      }
      player.sendMessage(ChatWriterNew.pluginMessage(
          BedwarsRevol._l(player, "success.itempurchased",
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
