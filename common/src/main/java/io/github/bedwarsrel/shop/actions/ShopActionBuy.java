package io.github.bedwarsrel.shop.actions;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerFlags;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrel.shop.ShopReward;
import io.github.bedwarsrel.shop.ShopTrade;
import io.github.bedwarsrel.shop.upgrades.Upgrade;
import io.github.bedwarsrel.shop.upgrades.UpgradeCycle;
import io.github.bedwarsrel.shop.upgrades.UpgradeScope;
import io.github.bedwarsrel.shop.upgrades.UpgradeSword;
import io.github.bedwarsrel.utils.ChatWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopActionBuy extends ShopAction {
  private static final String EVENT = "InventoryClickEvent";

  private ShopTrade trade;

  public ShopActionBuy(Player p, Shop s) {
    super(p, s);
  }

  public void setTrade(ShopTrade t) {
    this.trade = t;
  }

  @Override
  public void execute(Map<String, Object> args) {
    this.shop.playPickupSound();
    if (args == null || args.get(EVENT) == null) {
      BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(
          ChatColor.RED + "ShopActionCategory received no InventoryClickEvent!"));
    }

    InventoryClickEvent event = (InventoryClickEvent) args.get(EVENT);
    event.setCancelled(true);
    if (!this.hasEnoughResources()) {
      this.player.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(this.player, "errors.notenoughress")));
      return;
    }

    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(this.player);
    PlayerFlags flags = game.getPlayerFlags(this.player);
    boolean oneStackPerShift = flags.isOneStackPerShift();
    boolean cancel = false;
    int bought = 0;
    ItemStack item = this.trade.getReward().getItem();
    if (event.isShiftClick()) {
      while (this.hasEnoughResources() && !cancel) {
        cancel = !this.buyItem();
        if (!cancel && oneStackPerShift) {
          bought += item.getAmount();
          cancel = ((bought + item.getAmount()) > 64);
        }
      }
    } else {
      this.buyItem();
    }
  }

  private boolean hasEnoughResources() {
    ItemStack item1 = this.trade.getItem1();
    ItemStack item2 = this.trade.getItem2();
    PlayerInventory inventory = this.player.getInventory();
    if (item2 != null) {
      if (!inventory.contains(item1.getType(), item1.getAmount())
          || !inventory.contains(item2.getType(), item2.getAmount())) {
        return false;
      }
    } else {
      if (!inventory.contains(item1.getType(), item1.getAmount())) {
        return false;
      }
    }
    return true;
  }

  // Caller must ensure that the player has enough resources to pay for the item
  private boolean buyItem() {
    PlayerInventory inventory = this.player.getInventory();
    boolean success = true;
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(this.player);
    Team team = game.getPlayerTeam(this.player);
    ShopReward reward = this.trade.getReward();
    if (reward.isUpgrade()) {
      Upgrade builder = reward.getUpgrade();
      Upgrade upgrade = builder.create(game, team, this.player);
      if (!upgrade.activate(UpgradeScope.TEAM, UpgradeCycle.ONCE)) {
        this.player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
            ._l(this.player, "errors.alreadypurchased")));
        return false;
      }
    }

    int item1ToPay = this.trade.getItem1().getAmount();
    Iterator<?> stackIterator = inventory.all(this.trade.getItem1().getType()).entrySet().iterator();

    int firstItem1 = inventory.first(this.trade.getItem1());
    if (firstItem1 > -1) {
      inventory.clear(firstItem1);
    } else {
      // pay
      while (stackIterator.hasNext()) {
        Entry<Integer, ? extends ItemStack> entry =
            (Entry<Integer, ? extends ItemStack>) stackIterator.next();
        ItemStack stack = entry.getValue();

        int endAmount = stack.getAmount() - item1ToPay;
        if (endAmount < 0) {
          endAmount = 0;
        }

        item1ToPay = item1ToPay - stack.getAmount();
        stack.setAmount(endAmount);
        inventory.setItem(entry.getKey(), stack);

        if (item1ToPay <= 0) {
          break;
        }
      }
    }

    if (trade.getItem2() != null) {
      int item2ToPay = trade.getItem2().getAmount();
      stackIterator = inventory.all(trade.getItem2().getType()).entrySet().iterator();

      int firstItem2 = inventory.first(trade.getItem2());
      if (firstItem2 > -1) {
        inventory.clear(firstItem2);
      } else {
        // pay item2
        while (stackIterator.hasNext()) {
          Entry<Integer, ? extends ItemStack> entry =
              (Entry<Integer, ? extends ItemStack>) stackIterator.next();
          ItemStack stack = entry.getValue();

          int endAmount = stack.getAmount() - item2ToPay;
          if (endAmount < 0) {
            endAmount = 0;
          }

          item2ToPay = item2ToPay - stack.getAmount();
          stack.setAmount(endAmount);
          inventory.setItem(entry.getKey(), stack);

          if (item2ToPay <= 0) {
            break;
          }
        }
      }
    }

    // If the item is an upgrade at this point it was added to the game and paid for.
    // Then prevent adding an item to the inventory.
    if (reward.isUpgrade()) {
      return true;
    }

    ItemStack addingItem = reward.getItem().clone();
    ItemMeta meta = addingItem.getItemMeta();
    List<String> lore = meta.getLore();

    if (lore.size() > 0) {
      lore.remove(lore.size() - 1);
      if (this.trade.getItem2() != null) {
        lore.remove(lore.size() - 1);
      }
    }

    meta.setLore(lore);
    addingItem.setItemMeta(meta);

    HashMap<Integer, ItemStack> notStored = inventory.addItem(addingItem);
    if (notStored.size() > 0) {
      ItemStack notAddedItem = notStored.get(0);
      int removingAmount = addingItem.getAmount() - notAddedItem.getAmount();
      addingItem.setAmount(removingAmount);
      inventory.removeItem(addingItem);

      // restore
      inventory.addItem(this.trade.getItem1());
      if (this.trade.getItem2() != null) {
        inventory.addItem(this.trade.getItem2());
      }

      success = false;
    }

    BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player).getPlayerTeam(player)
        .getUpgrade(UpgradeSword.class).activate(UpgradeScope.PLAYER, UpgradeCycle.RESPAWN);

    player.updateInventory();
    return success;
  }

}
