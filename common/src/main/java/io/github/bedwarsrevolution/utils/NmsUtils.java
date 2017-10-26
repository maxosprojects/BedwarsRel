package io.github.bedwarsrevolution.utils;

import io.github.bedwarsrevolution.BedwarsRevol;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

/**
 * Created by {maxos} 2017
 */
public class NmsUtils {
  private static Method methodHideArmor = null;
  private static Method methodUnhideArmor = null;
  private static Method methodSetTntSource = null;

  public static void hideArmor(int entityId, Player otherPlayer, int slot) {
    try {
      if (methodHideArmor == null) {
        Class<?> hiderClass = BedwarsRevol.getInstance().getVersionRelatedClass("HideArmor");
        methodHideArmor = hiderClass.getMethod(
            "hide", int.class, Player.class, int.class);
      }
      methodHideArmor.invoke(null, entityId, otherPlayer, slot);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void unhideArmor(int entityId, Player playerInOtherTeam, Object item, int slot) {
    try {
      if (methodUnhideArmor == null) {
        Class<?> hiderClass = BedwarsRevol.getInstance().getVersionRelatedClass("HideArmor");
        methodUnhideArmor = hiderClass.getMethod(
            "unhide", int.class, Player.class, Object.class, int.class);
      }
      methodUnhideArmor.invoke(null, entityId, playerInOtherTeam, item, slot);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void setTntSource(Player player, TNTPrimed tnt) {
    try {
      if (methodSetTntSource == null) {
        Class<?> tntSourceClass = BedwarsRevol.getInstance().getVersionRelatedClass("TntSource");
        methodSetTntSource = tntSourceClass.getMethod(
            "setSource", Player.class, TNTPrimed.class);
      }
      methodSetTntSource.invoke(null, player, tnt);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
