package io.github.bedwarsrevolution.utils;

import io.github.bedwarsrevolution.BedwarsRevol;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

/**
 * Created by {maxos} 2017
 */
public class NmsUtils {
  private static Method methodHideArmor = null;
  private static Method methodUnhideArmor = null;
  private static Method methodSetTntSource = null;
  private static Method methodSpawnCustomGolem = null;

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

  public static IronGolem spawnCustomIronGolem(Location location, Set<Player> friendlyPlayers) {
    try {
      if (methodSpawnCustomGolem == null) {
        Class<?> tntSourceClass = BedwarsRevol.getInstance()
            .getVersionRelatedClass("CustomIronGolem");
        methodSpawnCustomGolem = tntSourceClass.getMethod(
            "spawn", Location.class, Set.class);
      }
      return (IronGolem) methodSpawnCustomGolem.invoke(null, location, friendlyPlayers);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static Object getPrivateField(String fieldName, Class clazz, Object object) {
    Field field;
    Object fieldValue = null;
    try {
      field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      fieldValue = field.get(object);
    } catch(Exception e) {
      e.printStackTrace();
    }
    return fieldValue;
  }
}
