package io.github.bedwarsrevolution.utils;

import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.Sound;

public class SoundMachineNew {

  public static Sound get(String v18, String v19) {
    Sound finalSound = null;

    try {
      if (BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_8")) {
        finalSound = Sound.valueOf(v18);
      } else {
        finalSound = Sound.valueOf(v19);
      }
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
      // just compatibility
    }

    return finalSound;
  }

}
