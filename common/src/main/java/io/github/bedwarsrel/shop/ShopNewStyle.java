package io.github.bedwarsrel.shop;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.Specials.SpecialItem;
import io.github.bedwarsrel.shop.Specials.SwordUpgradeEnum;
import io.github.bedwarsrel.shop.Specials.VirtualItem;
import io.github.bedwarsrel.utils.ChatWriter;
import io.github.bedwarsrel.utils.SoundMachine;
import io.github.bedwarsrel.utils.Utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopNewStyle {

  private final Player player;
  private List<MerchantCategory> categories = null;
  private MerchantCategory currentCategory = null;

  public ShopNewStyle(List<MerchantCategory> categories, Player player) {
    this.categories = categories;
    this.player = player;
  }

  @SuppressWarnings("deprecation")
  private void addCategoriesToInventory(Inventory inventory, Game game) {
    for (MerchantCategory cat : this.categories) {
      if (!this.player.hasPermission(cat.getPermission())) {
        continue;
      }
      ItemStack button = cat.getButton().clone();
      ItemMeta meta = button.getItemMeta();
      if (Utils.isColorable(button)) {
        button.setDurability(game.getPlayerTeam(this.player).getColor().getDyeColor().getWoolData());
      }
      if (this.currentCategory != null && this.currentCategory == cat) {
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
      }
      button.setItemMeta(meta);
      inventory.addItem(button);
    }
  }

  // Caller must ensure that the player has enough resources to pay for the item.
  @SuppressWarnings("unchecked")
  private boolean buyItem(ShopTrade trade) {
    PlayerInventory inventory = this.player.getInventory();
    boolean success = true;
    ShopReward reward = trade.getReward();
    boolean isVirtualItem = SpecialItem.isVirtualRepresentation(reward);

    if (isVirtualItem) {
      VirtualItem virtualItem = SpecialItem.newVirtualInstance(this.player, reward);
      // Check if item was successfully added to the game
      if (!virtualItem.init()) {
        this.player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
                          ._l(this.player, "errors.alreadypurchased")));
        return false;
      }
    }

    int item1ToPay = trade.getItem1().getAmount();
    Iterator<?> stackIterator = inventory.all(trade.getItem1().getType()).entrySet().iterator();

    int firstItem1 = inventory.first(trade.getItem1());
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

    // If the item is virtual at this point it was added to the game and paid for.
    // Then prevent adding an item to the inventory.
    if (isVirtualItem) {
      return true;
    }

    ItemStack addingItem = reward.getItem().clone();
    ItemMeta meta = addingItem.getItemMeta();
    List<String> lore = meta.getLore();

    if (lore.size() > 0) {
      lore.remove(lore.size() - 1);
      if (trade.getItem2() != null) {
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
      inventory.addItem(trade.getItem1());
      if (trade.getItem2() != null) {
        inventory.addItem(trade.getItem2());
      }

      success = false;
    }

    BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player)
            .getPlayerTeam(player).getUpgrade(SwordUpgradeEnum.class).equipPlayer(player);

    player.updateInventory();
    return success;
  }

  private void changeToOldShop(Game game, Player player) {
    game.getPlayerFlags(player).setUseOldShop(true);

    this.playButtonSound();

    // open old shop
    MerchantCategory.openCategorySelection(player, game);
  }

  private void playButtonSound() {
    this.player.playSound(this.player.getLocation(),
        SoundMachine.get("CLICK", "UI_BUTTON_CLICK"),
        Float.valueOf("1.0"), Float.valueOf("1.0"));
  }

  private void playPickupSound() {
    player.playSound(player.getLocation(),
        SoundMachine.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"),
        Float.valueOf("1.0"), Float.valueOf("1.0"));
  }

  private int getBuyInventorySize(int sizeCategories, int sizeOffers) {
    return this.calcInventorySizeForItems(sizeCategories) + this.calcInventorySizeForItems(sizeOffers);
  }

  public List<MerchantCategory> getCategories() {
    return this.categories;
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

  private MerchantCategory getClickedCategory(int rawSlot) {
    int count = 0;
    for (MerchantCategory cat : this.getAccessibleCategories()) {
      if (count == rawSlot) {
        return cat;
      }
      count++;
    }
    return null;
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

  private ShopTrade getTradingItem(int slot) {
    int count = this.calcInventorySizeForItems(this.getAccessibleNumberOfCategories());
    for (ShopTrade trade : this.currentCategory.getOffers()) {
      if (count == slot) {
        return trade;
      }
      count++;
    }
    return null;
  }

  private void handleBuyClick(InventoryClickEvent event, Game game) {
    int cats = this.getAccessibleNumberOfCategories();
    List<ShopTrade> offers = this.currentCategory.getOffers();
    int items = offers.size();
    int size = this.getBuyInventorySize(cats, items);

    ItemStack item = event.getCurrentItem();
    int slot = event.getRawSlot();
    boolean cancel = false;
    int bought = 0;
    boolean oneStackPerShift = game.getPlayerFlags(player).isOneStackPerShift();

    if (this.currentCategory == null) {
      this.player.closeInventory();
      return;
    }

    if (slot < cats) {
      // is category click
      event.setCancelled(true);

      // Shouldn't happen really
      if (item == null) {
        return;
      }

      if (this.getClickedCategory(slot) == this.currentCategory) {
        // back to default category view
        this.openInventory();
      } else {
        // open the clicked buy inventory
        this.handleCategoryClick(event, game);
      }
    } else if (event.getRawSlot() < size) {
      // its a buying item
      event.setCancelled(true);

      if (item == null || item.getType() == Material.AIR) {
        return;
      }

      ShopTrade trade = this.getTradingItem(slot);

      if (trade == null) {
        return;
      }

      this.playPickupSound();

      // enough resources?
      if (!this.hasEnoughResources(trade)) {
        this.player.sendMessage(
                ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
                    ._l(this.player, "errors.notenoughress")));
        return;
      }

      if (event.isShiftClick()) {
        while (this.hasEnoughResources(trade) && !cancel) {
          cancel = !this.buyItem(trade);
          if (!cancel && oneStackPerShift) {
            bought = bought + item.getAmount();
            cancel = ((bought + item.getAmount()) > 64);
          }
        }
      } else {
        this.buyItem(trade);
      }
    } else {
      if (event.isShiftClick()) {
        event.setCancelled(true);
      } else {
        event.setCancelled(false);
      }
    }
  }

  private void handleCategoryClick(InventoryClickEvent event, Game game) {
    int cats = this.getAccessibleNumberOfCategories();
    int size = this.calcInventorySizeForItems(cats) + 9;
    int rawSlot = event.getRawSlot();

    if (rawSlot >= this.calcInventorySizeForItems(cats) && rawSlot < size) {
      event.setCancelled(true);
      if (event.getCurrentItem().getType() == Material.SLIME_BALL) {
        this.changeToOldShop(game, this.player);
        return;
      }

      if (event.getCurrentItem().getType() == Material.BUCKET) {
        game.getPlayerFlags(this.player).setOneStackPerShift(false);
        this.playButtonSound();
        this.openInventory();
        return;
      } else if (event.getCurrentItem().getType() == Material.LAVA_BUCKET) {
        game.getPlayerFlags(this.player).setOneStackPerShift(true);
        this.playButtonSound();
        this.openInventory();
        return;
      }
    }

    if (rawSlot >= cats) {
      if (event.isShiftClick()) {
        event.setCancelled(true);
        return;
      }

      event.setCancelled(false);
      return;
    }

    MerchantCategory clickedCategory = this.getClickedCategory(event.getRawSlot());
    if (clickedCategory == null) {
      if (event.isShiftClick()) {
        event.setCancelled(true);
        return;
      }
      event.setCancelled(false);
      return;
    }

    this.openBuyInventory(clickedCategory, game);
  }

  public void handleInventoryClick(InventoryClickEvent event, Game game, Player player) {
    if (this.hasOpenCategory()) {
      this.handleBuyClick(event, game);
    } else {
      this.handleCategoryClick(event, game);
    }
  }

  private boolean hasEnoughResources(ShopTrade trade) {
    ItemStack item1 = trade.getItem1();
    ItemStack item2 = trade.getItem2();
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

  public boolean hasOpenCategory() {
    return this.currentCategory != null;
  }

  public boolean hasOpenCategory(MerchantCategory category) {
    if (this.currentCategory == null) {
      return false;
    }

    return (this.currentCategory.equals(category));
  }

  private void openBuyInventory(MerchantCategory category, Game game) {
    List<ShopTrade> offers = category.getOffers();
    int cats = this.getAccessibleNumberOfCategories();
    int items = offers.size();
    int size = this.getBuyInventorySize(cats, items);
    int catsInvSize = this.calcInventorySizeForItems(cats);

    this.playButtonSound();

    this.currentCategory = category;
    Inventory buyInventory = Bukkit
        .createInventory(player, size, BedwarsRel._l(player, "ingame.shop.name"));
    this.addCategoriesToInventory(buyInventory, game);

    for (int i = 0; i < offers.size(); i++) {
      ShopTrade trade = offers.get(i);
      if (trade.getItem1().getType() == Material.AIR
          && trade.getReward().getItem().getType() == Material.AIR) {
        continue;
      }

      int slot = catsInvSize + i;
      ItemStack tradeStack = this.toItemStack(trade, game);
      buyInventory.setItem(slot, tradeStack);
    }

    this.player.openInventory(buyInventory);
  }

  public void openInventory() {
    this.currentCategory = null;

    int cats = this.getAccessibleNumberOfCategories();
    int size = this.calcInventorySizeForItems(cats);

    Inventory inventory = Bukkit.createInventory(this.player, size, BedwarsRel
        ._l(this.player, "ingame.shop.name"));

    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    this.addCategoriesToInventory(inventory, game);

    ItemStack stack;
    if (game != null) {
      String bucketText = ChatColor.AQUA + BedwarsRel._l(
          player, "default.currently") + ": " + ChatColor.WHITE;
      if (game.getPlayerFlags(this.player).isOneStackPerShift()) {
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
      inventory.setItem(size - 4, stack);
    }

    if (BedwarsRel.getInstance().getBooleanConfig("enable-old-shop", false)) {
      ItemStack slime = new ItemStack(Material.SLIME_BALL, 1);
      ItemMeta slimeMeta = slime.getItemMeta();
      slimeMeta.setDisplayName(BedwarsRel._l(this.player, "ingame.shop.oldshop"));
      slimeMeta.setLore(new ArrayList<String>());
      slime.setItemMeta(slimeMeta);
      inventory.setItem(size - 6, slime);
    }

    player.openInventory(inventory);
  }

  public void setCurrentCategory(MerchantCategory category) {
    this.currentCategory = category;
  }

  @SuppressWarnings("deprecation")
  private ItemStack toItemStack(ShopTrade trade, Game game) {
    ItemStack tradeStack = trade.getReward().getItem().clone();
    Method colorable = Utils.getColorableMethod(tradeStack.getType());
    ItemMeta meta = tradeStack.getItemMeta();
    ItemStack item1 = trade.getItem1();
    ItemStack item2 = trade.getItem2();
    if (Utils.isColorable(tradeStack)) {
      tradeStack.setDurability(game.getPlayerTeam(player).getColor().getDyeColor().getWoolData());
    } else if (colorable != null) {
      colorable.setAccessible(true);
      try {
        colorable.invoke(meta, new Object[]{game.getPlayerTeam(player).getColor().getColor()});
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
