package io.github.bedwarsrel.shop;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.actions.ShopAction;
import io.github.bedwarsrel.shop.actions.ShopActionBuy;
import io.github.bedwarsrel.shop.actions.ShopActionCategory;
import io.github.bedwarsrel.shop.actions.ShopActionStackPerShift;
import io.github.bedwarsrel.utils.SoundMachine;
import io.github.bedwarsrel.utils.Utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop {

  private Player player;
  private List<MerchantCategory> categories;
  private Map<String, MerchantCategory> categoryMap = new HashMap<>();
  private String currentCategory = "";
  private List<ShopAction> actions;
  private int nextSlot = 0;

  public Shop(List<MerchantCategory> categories, Player player) {
    this.player = player;
    this.categories = categories;
    for (MerchantCategory cat : this.categories) {
      categoryMap.put(cat.getName(), cat);
    }
  }

  /**
   * Renders the shop and highlights an open category if any.
   *
   * @return slot number to render any other buttons necessary
   */
  public void render() {
    int cats = this.getAccessibleNumberOfCategories();
    int size = this.calcInventorySizeForItems(cats);

    this.actions = Arrays.asList(new ShopAction[size]);
    this.nextSlot = 0;

    Inventory inventory = Bukkit.createInventory(this.player, size, BedwarsRel
        ._l(this.player, "ingame.shop.name"));

    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    this.renderCategories(inventory, game);

    if (this.currentCategory.isEmpty()) {
      this.renderButtonStackPerShift(inventory, size - 4,
          game.getPlayerFlags(this.player).isOneStackPerShift());
    }

    this.nextSlot = Math.max(cats - 1, 0);
    int remainder = nextSlot % 9;
    if (remainder != 0) {
      nextSlot = (nextSlot / 9 + 1) * 9;
    }

    if (!this.currentCategory.isEmpty()) {
      this.renderTrades(inventory);
    }

    this.player.openInventory(inventory);
  }

  public void resetCurrentCategory() {
    this.currentCategory = "";
  }

  public void setCurrentCategory(String cat) {
    this.currentCategory = cat;
  }

  public void handleClick(InventoryClickEvent event) {
    int rawSlot = event.getRawSlot();
    if (rawSlot < this.actions.size()) {
      event.setCancelled(true);
      Map<String, Object> args = new HashMap<>();
      args.put("InventoryClickEvent", event);
      ShopAction action = this.actions.get(rawSlot);
      if (action != null) {
        this.actions.get(rawSlot).execute(args);
      }
    }
  }

  private void renderCategories(Inventory inventory, Game game) {
    for (MerchantCategory cat : this.categories) {
      if (!this.player.hasPermission(cat.getPermission())) {
        continue;
      }
      ItemStack button = cat.getButton().clone();
      ItemMeta meta = button.getItemMeta();
      if (Utils.isColorable(button)) {
        button.setDurability(game.getPlayerTeam(this.player).getColor().getDyeColor().getWoolData());
      }
      ShopActionCategory action = new ShopActionCategory(this.player, this);
      if (this.currentCategory.equals(cat.getName())) {
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        action.setActive(true);
      }
      action.setCategory(cat);
      button.setItemMeta(meta);
      inventory.addItem(button);
      this.actions.set(this.nextSlot, action);
      nextSlot++;
    }
  }

  private void renderButtonStackPerShift(Inventory inventory, int slot, boolean oneStackPerShift) {
    ItemStack stack;
    String bucketText = ChatColor.AQUA + BedwarsRel._l(
        player, "default.currently") + ": " + ChatColor.WHITE;
    if (oneStackPerShift) {
      stack = new ItemStack(Material.BUCKET, 1);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(bucketText + BedwarsRel._l(player, "ingame.shop.onestackpershift"));
      stack.setItemMeta(meta);
    } else {
      stack = new ItemStack(Material.LAVA_BUCKET, 1);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(bucketText + BedwarsRel._l(player, "ingame.shop.fullstackpershift"));
      stack.setItemMeta(meta);
    }
    ShopAction action = new ShopActionStackPerShift(this.player, this, null);
    this.actions.set(slot, action);
    inventory.setItem(slot, stack);
  }

  // Caller must ensure this.currentCategory isn't empty
  private void renderTrades(Inventory inventory) {
    List<ShopTrade> offers = categoryMap.get(this.currentCategory).getOffers();
    Team team = BedwarsRel.getInstance().getGameManager()
        .getGameOfPlayer(this.player).getPlayerTeam(this.player);
    for (ShopTrade trade : offers) {
      if (trade.getItem1().getType() == Material.AIR
          && trade.getReward().getItem().getType() == Material.AIR) {
        continue;
      }
      ItemStack tradeStack = this.toItemStack(trade, team);
      inventory.setItem(this.nextSlot, tradeStack);
      ShopActionBuy action = new ShopActionBuy(this.player, this);
      action.setTrade(trade);
      this.actions.set(this.nextSlot, action);
      this.nextSlot++;
    }
  }

  public void playButtonSound() {
    this.player.playSound(this.player.getLocation(),
        SoundMachine.get("CLICK", "UI_BUTTON_CLICK"),
        Float.valueOf("1.0"), Float.valueOf("1.0"));
  }

  public void playPickupSound() {
    player.playSound(player.getLocation(),
        SoundMachine.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"),
        Float.valueOf("1.0"), Float.valueOf("1.0"));
  }

  private int getBuyInventorySize(int sizeCategories, int sizeOffers) {
    return this.calcInventorySizeForItems(sizeCategories) + this.calcInventorySizeForItems(sizeOffers);
  }

  private int getAccessibleNumberOfCategories() {
    return this.getAccessibleCategories().size();
  }

  private List<MerchantCategory> getAccessibleCategories() {
    List<MerchantCategory> res = new ArrayList<>();
    for (MerchantCategory cat : this.categories) {
      if (this.player.hasPermission(cat.getPermission())) {
        res.add(cat);
      }
    }
    return res;
  }

  private int calcInventorySizeForItems(int items) {
    int part = items % 9;
    // Complete lines
    int size = (items / 9 + 1) * 9;
    if (part > 0) {
      size += 9;
    }
    return size;
  }

  private ItemStack toItemStack(ShopTrade trade, Team team) {
    ItemStack tradeStack = trade.getReward().getItem().clone();
    Method colorable = Utils.getColorableMethod(tradeStack.getType());
    ItemMeta meta = tradeStack.getItemMeta();
    ItemStack item1 = trade.getItem1();
    ItemStack item2 = trade.getItem2();
    if (Utils.isColorable(tradeStack)) {
      tradeStack.setDurability(team.getColor().getDyeColor().getWoolData());
    } else if (colorable != null) {
      colorable.setAccessible(true);
      try {
        colorable.invoke(meta, new Object[]{team.getColor().getColor()});
      } catch (Exception e) {
        BedwarsRel.getInstance().getBugsnag().notify(e);
        e.printStackTrace();
      }
    }
    List<String> lores = meta.getLore();
    if (lores == null) {
      lores = new ArrayList<>();
    }

    lores.add(ChatColor.WHITE + String.valueOf(item1.getAmount()) + " "
        + item1.getItemMeta().getDisplayName());
    if (item2 != null) {
      lores.add(ChatColor.WHITE + String.valueOf(item2.getAmount()) + " "
          + item2.getItemMeta().getDisplayName());
    }

    meta.setLore(lores);
    tradeStack.setItemMeta(meta);
    return tradeStack;
  }
}
