package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class UpgradeRegistry {
  private static Map<String, List<Upgrade>> upgrades = new HashMap<>();

  public static void loadUpgrades() {
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new UpgradeBaseAlarmListener(),
        BedwarsRel.getInstance());

    List<Upgrade> alarms = new ArrayList<>();
    upgrades.put("ALARM", alarms);
    alarms.add(new UpgradeBaseAlarm());

    List<Upgrade> armorItem = new ArrayList<>();
    upgrades.put("ARMOR_ITEM", armorItem);
    armorItem.add(new UpgradeArmorItems(UpgradeArmorItemsEnum.LEATHER));
    armorItem.add(new UpgradeArmorItems(UpgradeArmorItemsEnum.CHAINMAIL));
    armorItem.add(new UpgradeArmorItems(UpgradeArmorItemsEnum.IRON));
    armorItem.add(new UpgradeArmorItems(UpgradeArmorItemsEnum.DIAMOND));

    List<Upgrade> armorProtection = new ArrayList<>();
    upgrades.put("ARMOR_PROTECTION", armorProtection);
    armorProtection.add(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION1));
    armorProtection.add(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION2));
    armorProtection.add(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION3));
    armorProtection.add(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION4));

    List<Upgrade> sword = new ArrayList<>();
    upgrades.put("SWORD", sword);
    sword.add(new UpgradeSword(UpgradeSwordEnum.SHARPNESS0));
    sword.add(new UpgradeSword(UpgradeSwordEnum.SHARPNESS1));

    List<Upgrade> permanent = new ArrayList<>();
    upgrades.put("PERMANENT", sword);
    permanent.add(new UpgradePermanentItem(UpgradePermanentItemEnum.WOOD_SWORD));
    permanent.add(new UpgradePermanentItem(UpgradePermanentItemEnum.SHEARS));

    List<Upgrade> forge = new ArrayList<>();
    upgrades.put("FORGE", sword);
    forge.add(new UpgradeForge(UpgradeForgeEnum.FORGE0));
    forge.add(new UpgradeForge(UpgradeForgeEnum.FORGE1));
    forge.add(new UpgradeForge(UpgradeForgeEnum.FORGE2));
    forge.add(new UpgradeForge(UpgradeForgeEnum.FORGE3));
    forge.add(new UpgradeForge(UpgradeForgeEnum.FORGE4));
  }

  public static Upgrade getUpgrade(String type, int level) {
    List<Upgrade> typeUpgrades = upgrades.get(type);
    if (typeUpgrades == null) {
      return null;
    }
    for (Upgrade upgrade : typeUpgrades) {
      if (upgrade.isLevel(level)) {
        return upgrade;
      }
    }
    return null;
  }

}
