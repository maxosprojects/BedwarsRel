package io.github.bedwarsrel.shop;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.Specials.SpecialItem;
import io.github.bedwarsrel.shop.Specials.Upgrade;
import io.github.bedwarsrel.utils.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MerchantCategory {

  private ItemStack button = null;
  private String name = null;
  private ArrayList<ShopTrade> offers = null;
  private String permission = null;

  public MerchantCategory(String name, ItemStack button) {
    this(name, button, new ArrayList<ShopTrade>(), "bw.base");
  }

  public MerchantCategory(String name, ItemStack button, ArrayList<ShopTrade> offers, String permission) {
    this.name = name;
    this.button = button;
    this.offers = offers;
    this.permission = permission;
  }

  private static String color(String in) {
    return ChatColor.translateAlternateColorCodes('&', in);
  }

  private static List<String> color(List<String> in) {
    List<String> out = new ArrayList<>();
    for (String line : in) {
      out.add(color(line));
    }
    return out;
  }

  private static ItemStack color(ItemStack item) {
    if (item.hasItemMeta()) {
      ItemMeta meta = item.getItemMeta();
      if (meta.hasDisplayName()) {
        meta.setDisplayName(color(meta.getDisplayName()));
      }
      if (meta.hasLore()) {
        meta.setLore(color(meta.getLore()));
      }
    }
    return item;
  }

  private static void logParseError(String catName) {
    logError("Couldn't parse shop category" + catName);
  }

  private static void logError(String text) {
    BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + text);
  }

  @SuppressWarnings({"unchecked", "deprecation"})
  public static List<MerchantCategory> loadCategories(FileConfiguration cfg) {
    if (cfg.getConfigurationSection("shop") == null) {
      return new ArrayList<>();
    }

    List<MerchantCategory> res = new ArrayList<>();

    for (Map<?, ?> elem : cfg.getMapList("shop")) {
      if (!elem.containsKey("name")
          || !elem.containsKey("button")
          || !elem.containsKey("offers")) {
        logError("Shop category misses required field");
        continue;
      }
      Map<String, Object> cat = (Map<String, Object>) elem;

      String catName = color((String) cat.get("name"));
      ItemStack catButton = color(ItemStack.deserialize((Map<String, Object>) cat.get("button")));
      ItemMeta catMeta = catButton.getItemMeta();
      catMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
          ItemFlag.HIDE_POTION_EFFECTS,
          ItemFlag.HIDE_ENCHANTS);
      String permission = "bw.base";

      if (cat.containsKey("permission")) {
        permission = (String) cat.get("permission");
      }

      ArrayList<ShopTrade> offers = new ArrayList<ShopTrade>();

      for (Object offer : (List<Object>)cat.get("offers")) {
        if (offer instanceof String) {
          if (offer.toString().equalsIgnoreCase("empty")
              || offer.toString().equalsIgnoreCase("null")
              || offer.toString().equalsIgnoreCase("e")) {
            ShopTrade trade = new ShopTrade(new ItemStack(Material.AIR, 1),
                    new ShopReward(new ItemStack(Material.AIR, 1), null));
            offers.add(trade);
          }
          continue;
        }

        Map<String, Object> offerSection = (Map<String, Object>) offer;
        if (!offerSection.containsKey("price") || !offerSection.containsKey("reward")) {
          continue;
        }

        ItemStack item1 = null;
        ItemStack item2 = null;

        List<Map<String, Object>> price = (List<Map<String, Object>>) offerSection.get("price");
        try {
          item1 = color(setResourceName(ItemStack.deserialize(price.get(0))));
        } catch (Exception e) {
          logParseError(catName);
        }
        if (price.size() == 2) {
          try {
            item2 = color(setResourceName(ItemStack.deserialize(price.get(1))));
          } catch (Exception e) {
            logParseError(catName);
          }
        }

        ItemStack rewardButton = null;
        Upgrade upgrade = null;
        try {
          Map<String, Object> rewardElem = (Map<String, Object>) offerSection.get("reward");
          if (rewardElem.containsKey("button")) {
            rewardButton = color(ItemStack.deserialize(
                (Map<String, Object>) rewardElem.get("button")));
            Map<String, Object> upgradeElem = (Map<String, Object>) rewardElem.get("upgrade");
            upgrade = SpecialItem.getUpgrade(
                (String)upgradeElem.get("type"), (int)upgradeElem.get("level"));
          } else {
            rewardButton = color(setResourceName(ItemStack.deserialize(
                (Map<String, Object>) rewardElem.get("reward"))));
          }
        } catch (Exception e) {
          logParseError(catName);
        }
        ShopReward reward = new ShopReward(rewardButton, upgrade);

        if (item1 == null || rewardButton == null) {
          logParseError(catName);
          continue;
        }

        offers.add(new ShopTrade(item1, item2, reward));
      }

      res.add(new MerchantCategory(catName, catButton, offers, permission));
    }

    return res;
  }

  @SuppressWarnings("deprecation")
  public static void openCategorySelection(Player player, Game game) {
    if (player == null) {
      return;
    }
    List<MerchantCategory> cats = game.getShopCatList();

    int part = cats.size() % 9;
    // Complete lines
    int size = cats.size() / 9 * 9;
    if (part > 0) {
      size += 9;
    }

    Inventory inv = Bukkit.createInventory(player, size, BedwarsRel._l(player, "ingame.shop.name"));
    for (MerchantCategory cat : cats) {
      if (!player.hasPermission(cat.getPermission())) {
        continue;
      }
      ItemStack is = cat.getButton().clone();
      ItemMeta im = is.getItemMeta();
      if (Utils.isColorable(is)) {
        is.setDurability(game.getPlayerTeam(player).getColor().getDyeColor().getWoolData());
      }
      is.setItemMeta(im);
      inv.addItem(is);
    }

    player.openInventory(inv);
  }

  @SuppressWarnings("deprecation")
  private static ItemStack setResourceName(ItemStack item) {

    ItemMeta im = item.getItemMeta();
    String name = im.getDisplayName();

    // check if is resource
    ConfigurationSection resourceSection =
        BedwarsRel.getInstance().getConfig().getConfigurationSection("resource");
    for (String key : resourceSection.getKeys(false)) {
      List<Object> resourceList =
          (List<Object>) BedwarsRel.getInstance().getConfig().getList("resource." + key + ".item");

      for (Object resource : resourceList) {
        ItemStack itemStack = ItemStack.deserialize((Map<String, Object>) resource);
        if (itemStack != null && itemStack.getType().equals(item.getType())
            && itemStack.getItemMeta() != null
            && itemStack.getItemMeta().getDisplayName() != null) {
          name = itemStack.getItemMeta().getDisplayName();
        }
      }
    }

    im.setDisplayName(name);
    item.setItemMeta(im);

    return item;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<ShopTrade> getFilteredOffers() {
    ArrayList<ShopTrade> trades = (ArrayList<ShopTrade>) this.offers.clone();
    Iterator<ShopTrade> iterator = trades.iterator();

    while (iterator.hasNext()) {
      ShopTrade trade = iterator.next();
      if (trade.getItem1().getType() == Material.AIR
          && trade.getReward().getItem().getType() == Material.AIR) {
        iterator.remove();
      }
    }

    return trades;
  }

  public String getName() {
    return this.name;
  }

  public ArrayList<ShopTrade> getOffers() {
    return this.offers;
  }

  public String getPermission() {
    return this.permission;
  }

  public ItemStack getButton() {
    return button;
  }
}
