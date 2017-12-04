package io.github.bedwarsrevolution.shop.upgrades;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import java.util.Iterator;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UpgradeRegistry {
  private static Multimap<String, Upgrade> upgrades = ArrayListMultimap.create();

  public static void loadUpgrades() {
    BedwarsRevol.getInstance().getServer().getPluginManager().
        registerEvents(new UpgradeBaseAlarmListener(), BedwarsRevol.getInstance());

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

    addUpgrade(new UpgradeItemFireball());
    addUpgrade(new UpgradeItemMonsterEgg());
    addUpgrade(new UpgradeItemLandmineGoggles());
    addUpgrade(new UpgradeItemCopter());
  }

  private static void addUpgrade(Upgrade upgrade) {
    upgrades.put(upgrade.getType(), upgrade);
  }

  public static Upgrade getUpgrade(String type, int level) {
    for (Upgrade upgrade : upgrades.get(type)) {
      if (upgrade.isLevel(level)) {
        return upgrade;
      }
    }
    return null;
  }

  public static boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    for (Upgrade upgrade : upgrades.values()) {
      if (upgrade.use(playerCtx, item, event)) {
        return true;
      }
    }
    return false;
  }
}
