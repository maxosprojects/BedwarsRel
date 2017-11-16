package io.github.bedwarsrevolution.shop;

import com.google.common.collect.ImmutableSet;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.actions.ShopAction;
import io.github.bedwarsrevolution.shop.actions.ShopActionBuy;
import io.github.bedwarsrevolution.shop.actions.ShopActionCategory;
import io.github.bedwarsrevolution.shop.actions.ShopActionStackPerShift;
import io.github.bedwarsrevolution.shop.upgrades.Upgrade;
import io.github.bedwarsrevolution.utils.SoundMachineNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop {
  private static final Set<InventoryAction> CHECKED_ACTIONS = ImmutableSet.of(
      InventoryAction.SWAP_WITH_CURSOR,
      InventoryAction.PLACE_ALL,
      InventoryAction.PLACE_ONE,
      InventoryAction.PLACE_SOME);

  private final PlayerContext playerCtx;
  private final Player player;
  private final String windowName;
  private List<MerchantCategory> categories;
  private Map<String, MerchantCategory> categoryMap = new HashMap<>();
  private String currentCategory = "";
  private List<ShopAction> actions;
  private int nextSlot = 0;

  public Shop(List<MerchantCategory> categories, PlayerContext playerCtx) {
    this.playerCtx = playerCtx;
    this.player = playerCtx.getPlayer();
    this.categories = categories;
    for (MerchantCategory cat : this.categories) {
      this.categoryMap.put(cat.getName(), cat);
    }
    this.windowName = BedwarsRevol._l(this.player, "ingame.shop.name");
  }

  /**
   * Renders the shop and highlights an open category if any.
   */
  public void render() {
    int cats = this.getAccessibleNumberOfCategories();
    int size = this.calcInventorySizeForItems(cats);

    this.actions = Arrays.asList(new ShopAction[size]);
    this.nextSlot = 0;

    Inventory inventory = Bukkit.createInventory(this.player, size, this.windowName);

    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(this.player);
    if (ctx == null) {
      return;
    }

    this.renderCategories(inventory);

    if (this.currentCategory.isEmpty()) {
      this.renderButtonStackPerShift(inventory, size - 4,
          this.playerCtx.isOneStackPerShift());
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
    boolean isShop = event.getInventory().getName().equals(this.windowName);
    int rawSlot = event.getRawSlot();
    Inventory clickedInventory = event.getClickedInventory();
    InventoryView view = event.getView();
    ItemStack clickedStack = event.getCurrentItem();
    InventoryAction inventoryAction = event.getAction();
    ItemStack cursorStack = event.getCursor();

    if (isShop && rawSlot >= 0 && rawSlot < this.actions.size()) {
      event.setCancelled(true);
      Map<String, Object> args = new HashMap<>();
      args.put("InventoryClickEvent", event);
      ShopAction action = this.actions.get(rawSlot);
      if (action != null) {
        this.actions.get(rawSlot).execute(args);
      }
      return;
    }

    if (clickedInventory == null) {
      event.setCancelled(true);
      return;
    }
    if (inventoryAction == InventoryAction.MOVE_TO_OTHER_INVENTORY
        && (view.getTopInventory().getType() != InventoryType.CRAFTING)
        && this.isPermanentUpgrade(clickedStack.getType())) {
      event.setCancelled(true);
      return;
    }
    if (clickedInventory.getType() != InventoryType.PLAYER
        && CHECKED_ACTIONS.contains(inventoryAction)
        && this.isPermanentUpgrade(cursorStack.getType())) {
      event.setCancelled(true);
    }
  }

  public void handleDrop(PlayerDropItemEvent event) {
    ItemStack item = event.getItemDrop().getItemStack();
    if (this.isPermanentUpgrade(item.getType())) {
      event.setCancelled(true);
    }
  }

  private boolean isPermanentUpgrade(Material material) {
    for (Upgrade upgrade : this.playerCtx.getAllUpgrades()) {
      if (upgrade.isPermanent() && upgrade.isMaterial(material)) {
        return true;
      }
    }
    for (Upgrade upgrade : this.playerCtx.getTeam().getUpgrades().values()) {
      if (upgrade.isPermanent() && upgrade.isMaterial(material)) {
        return true;
      }
    }
    return false;
  }

  private void renderCategories(Inventory inventory) {
    for (MerchantCategory cat : this.categories) {
      if (!this.player.hasPermission(cat.getPermission())) {
        continue;
      }
      ItemStack button = cat.getButton().clone();
      ItemMeta meta = button.getItemMeta();
      if (UtilsNew.isColorable(button)) {
        button.setDurability(this.playerCtx.getTeam().getColor().getDyeColor().getWoolData());
      }
      ShopActionCategory action = new ShopActionCategory(this.playerCtx, this);
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
    String bucketText = ChatColor.AQUA + BedwarsRevol._l(
        this.player, "default.currently") + ": " + ChatColor.WHITE;
    if (oneStackPerShift) {
      stack = new ItemStack(Material.BUCKET, 1);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(bucketText + BedwarsRevol._l(this.player, "ingame.shop.onestackpershift"));
      stack.setItemMeta(meta);
    } else {
      stack = new ItemStack(Material.LAVA_BUCKET, 1);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(bucketText + BedwarsRevol._l(this.player, "ingame.shop.fullstackpershift"));
      stack.setItemMeta(meta);
    }
    ShopAction action = new ShopActionStackPerShift(this.playerCtx, this, null);
    this.actions.set(slot, action);
    inventory.setItem(slot, stack);
  }

  // Caller must ensure this.currentCategory isn't empty
  private void renderTrades(Inventory inventory) {
    List<ShopTrade> offers = categoryMap.get(this.currentCategory).getOffers();
    TeamNew team = this.playerCtx.getTeam();
    for (ShopTrade trade : offers) {
      ShopReward reward = trade.getReward();
      if (trade.getItem1().getType() == Material.AIR
          && reward.getItem().getType() == Material.AIR) {
        this.nextSlot++;
        continue;
      }
      if (reward.isUpgrade() && !reward.getUpgrade().shouldRender(this.playerCtx)) {
        continue;
      }
      ItemStack tradeStack = this.toItemStack(trade, team);
      if (reward.isUpgrade() && reward.getUpgrade().alreadyOwn(this.playerCtx)) {
        this.setAlreadyOwn(tradeStack);
      }
      inventory.setItem(this.nextSlot, tradeStack);
      ShopActionBuy action = new ShopActionBuy(this.playerCtx, this);
      action.setTrade(trade);
      this.actions.set(this.nextSlot, action);
      this.nextSlot++;
    }
  }

  private void setAlreadyOwn(ItemStack itemStack) {
    ItemMeta meta = itemStack.getItemMeta();
    List<String> lores = meta.getLore();
    if (lores == null) {
      lores = new ArrayList<>();
    }
    lores.add("");
    lores.add(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol._l(this.player, "ingame.shop.alreadyown")));
    meta.setLore(lores);
    itemStack.setItemMeta(meta);
  }

  public void playButtonSound() {
    this.player.playSound(this.player.getLocation(),
        SoundMachineNew.get("CLICK", "UI_BUTTON_CLICK"),
        Float.valueOf("1.0"), Float.valueOf("1.0"));
  }

  public void playPickupSound() {
    player.playSound(player.getLocation(),
        SoundMachineNew.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"),
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

  private ItemStack toItemStack(ShopTrade trade, TeamNew team) {
    ItemStack tradeStack = trade.getReward().getItem().clone();
    Method colorable = UtilsNew.getColorableMethod(tradeStack.getType());
    ItemMeta meta = tradeStack.getItemMeta();
    ItemStack item1 = trade.getItem1();
    ItemStack item2 = trade.getItem2();
    if (UtilsNew.isColorable(tradeStack)) {
      tradeStack.setDurability(team.getColor().getDyeColor().getWoolData());
    } else if (colorable != null) {
      colorable.setAccessible(true);
      try {
        colorable.invoke(meta, new Object[]{team.getColor().getColor()});
      } catch (Exception e) {
//        BedwarsRevol.getInstance().getBugsnag().notify(e);
        e.printStackTrace();
      }
    }
    List<String> lores = meta.getLore();
    if (lores == null) {
      lores = new ArrayList<>();
    }
    lores.add("");
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
