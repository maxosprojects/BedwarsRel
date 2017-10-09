package io.github.bedwarsrel.shop.Specials;

import io.github.bedwarsrel.BedwarsRel;
import java.util.ArrayList;
import java.util.List;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class SpecialItem {

  private static List<Class<? extends SpecialItem>> availableSpecials =
      new ArrayList<Class<? extends SpecialItem>>();
  private static List<VirtualItem> virtualItems = new ArrayList<>();

  public static List<Class<? extends SpecialItem>> getSpecials() {
    return SpecialItem.availableSpecials;
  }

  public static void loadSpecials() {
    SpecialItem.availableSpecials.add(RescuePlatform.class);
    SpecialItem.availableSpecials.add(Trap.class);
    SpecialItem.availableSpecials.add(TrapBase.class);
    SpecialItem.availableSpecials.add(MagnetShoe.class);
    SpecialItem.availableSpecials.add(ProtectionWall.class);
    SpecialItem.availableSpecials.add(WarpPowder.class);
    SpecialItem.availableSpecials.add(TNTSheep.class);
    SpecialItem.availableSpecials.add(Tracker.class);
    SpecialItem.availableSpecials.add(ArrowBlocker.class);
    BedwarsRel.getInstance().getServer().getPluginManager()
        .registerEvents(new RescuePlatformListener(),
            BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new TrapListener(),
        BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new TrapBaseListener(),
            BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new TntListener(),
            BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new MagnetShoeListener(),
        BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager()
        .registerEvents(new ProtectionWallListener(),
            BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new WarpPowderListener(),
        BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new TNTSheepListener(),
        BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new TrackerListener(),
        BedwarsRel.getInstance());
    BedwarsRel.getInstance().getServer().getPluginManager()
        .registerEvents(new ArrowBlockerListener(),
            BedwarsRel.getInstance());

    SpecialItem.virtualItems.add(new TrapBase());
    SpecialItem.virtualItems.add(new ArmorPurchase(ArmorPurchaseEnum.LEATHER));
    SpecialItem.virtualItems.add(new ArmorPurchase(ArmorPurchaseEnum.CHAINMAIL));
    SpecialItem.virtualItems.add(new ArmorPurchase(ArmorPurchaseEnum.IRON));
    SpecialItem.virtualItems.add(new ArmorPurchase(ArmorPurchaseEnum.DIAMOND));
    SpecialItem.virtualItems.add(new ArmorUpgrade(ArmorUpgradeEnum.PROTECTION1));
    SpecialItem.virtualItems.add(new ArmorUpgrade(ArmorUpgradeEnum.PROTECTION2));
    SpecialItem.virtualItems.add(new ArmorUpgrade(ArmorUpgradeEnum.PROTECTION3));
    SpecialItem.virtualItems.add(new ArmorUpgrade(ArmorUpgradeEnum.PROTECTION4));
    SpecialItem.virtualItems.add(new SwordUpgrade(SwordUpgradeEnum.SHARPNESS0));
    SpecialItem.virtualItems.add(new SwordUpgrade(SwordUpgradeEnum.SHARPNESS1));
  }

  public abstract Material getActivatedMaterial();

  public abstract Material getItemMaterial();

  private static VirtualItem getVirtualItemBuilder(ItemStack item) {
    for (VirtualItem virtualItem : virtualItems) {
      if (virtualItem.isRepresentation(item)) {
        return virtualItem;
      }
    }
    return null;
  }

  public static boolean isVirtualRepresentation(ItemStack item) {
    VirtualItem builder = getVirtualItemBuilder(item);
    if (builder == null) {
      return false;
    }
    return true;
  }

  public static VirtualItem newVirtualInstance(Player player, ItemStack item) {
    VirtualItem builder = getVirtualItemBuilder(item);
    if (builder == null) {
      return null;
    }
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    Team team = game.getPlayerTeam(player);
    return builder.create(game, team, player);
  }

}
