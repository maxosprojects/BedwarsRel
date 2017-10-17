package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrel.BedwarsRel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeRegistry {
  private static Map<String, List<Upgrade>> upgrades = new HashMap<>();

  public static void loadUpgrades() {
    BedwarsRel.getInstance().getServer().getPluginManager().registerEvents(new UpgradeBaseAlarmListener(),
        BedwarsRel.getInstance());

    addUpgrade(new UpgradeBaseAlarm());

    addUpgrade(new UpgradeArmorItems(UpgradeArmorItemsEnum.LEATHER));
    addUpgrade(new UpgradeArmorItems(UpgradeArmorItemsEnum.CHAINMAIL));
    addUpgrade(new UpgradeArmorItems(UpgradeArmorItemsEnum.IRON));
    addUpgrade(new UpgradeArmorItems(UpgradeArmorItemsEnum.DIAMOND));

    addUpgrade(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION1));
    addUpgrade(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION2));
    addUpgrade(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION3));
    addUpgrade(new UpgradeArmorProtection(UpgradeArmorProtectionEnum.PROTECTION4));

    addUpgrade(new UpgradeSwordSharpness(UpgradeSwordSharpnessEnum.SHARPNESS0));
    addUpgrade(new UpgradeSwordSharpness(UpgradeSwordSharpnessEnum.SHARPNESS1));

    addUpgrade(new UpgradeForge(UpgradeForgeEnum.FORGE0));
    addUpgrade(new UpgradeForge(UpgradeForgeEnum.FORGE1));
    addUpgrade(new UpgradeForge(UpgradeForgeEnum.FORGE2));
    addUpgrade(new UpgradeForge(UpgradeForgeEnum.FORGE3));
    addUpgrade(new UpgradeForge(UpgradeForgeEnum.FORGE4));

    addUpgrade(new UpgradeItem());

    addUpgrade(new UpgradeSwordItem(1));
    addUpgrade(new UpgradeSwordItem(2));
    addUpgrade(new UpgradeSwordItem(3));
  }

  private static void addUpgrade(Upgrade upgrade) {
    List<Upgrade> list = upgrades.get(upgrade.getType());
    if (list == null) {
      list = new ArrayList<>();
      upgrades.put(upgrade.getType(), list);
    }
    list.add(upgrade);
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
