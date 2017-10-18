package io.github.bedwarsrevolution.localization;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LocalizationConfigNew extends YamlConfiguration {

  private String locale;

  public LocalizationConfigNew(String locale) {
    this.locale = locale;
    this.loadLocale();
  }

  @Override
  public Object get(String path) {
    return this.getString(path);
  }

  public Object get(String path, Map<String, String> params) {
    return this.getFormatString(path, params);
  }

  public String getFormatString(String path, Map<String, String> params) {
    String str = this.getString(path);
    for (String key : params.keySet()) {
      str = str.replace("$" + key.toLowerCase() + "$", params.get(key));
    }

    return ChatColor.translateAlternateColorCodes('&', str);
  }

  @SuppressWarnings("unchecked")
  public String getPlayerLocale(Player player) {
    try {
      Method getHandleMethod = BedwarsRevol.getInstance()
          .getCraftBukkitClass("entity.CraftPlayer")
          .getMethod("getHandle", new Class[]{});
      getHandleMethod.setAccessible(true);
      Object nmsPlayer = getHandleMethod.invoke(player, new Object[]{});

      Field localeField = nmsPlayer.getClass().getDeclaredField("locale");
      localeField.setAccessible(true);
      return localeField.get(nmsPlayer).toString().split("_")[0].toLowerCase();
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
      return BedwarsRevol.getInstance().getFallbackLocale();
    }
  }

  @Override
  public String getString(String path) {
    if (super.get(path) == null) {
      BedwarsRevol.getInstance().getServer().getConsoleSender()
          .sendMessage(ChatWriterNew
              .pluginMessage(ChatColor.GOLD + "No translation found for: \"" + path + "\""));
      return "LOCALE_NOT_FOUND";
    }

    return ChatColor.translateAlternateColorCodes('&', super.getString(path));
  }

  public void loadLocale() {
    File locFile = new File(BedwarsRevol.getInstance().getDataFolder()
        .getPath() + "/locale/" + this.locale + ".yml");
    BufferedReader reader = null;
    InputStream inputStream = null;
    if (locFile.exists()) {
      try {
        inputStream = new FileInputStream(locFile);
      } catch (FileNotFoundException e) {
        // NO ERROR
      }
      BedwarsRevol.getInstance().getServer().getConsoleSender().sendMessage(ChatWriterNew
          .pluginMessage(ChatColor.GOLD + "Using your custom locale \"" + this.locale + "\"."));
    } else {
      inputStream = BedwarsRevol.getInstance().getResource("locale/" + this.locale + ".yml");
      if (inputStream == null) {
        BedwarsRevol.getInstance().getServer().getConsoleSender()
            .sendMessage(ChatWriterNew.pluginMessage(ChatColor.GOLD + "The locale \"" + this.locale
                + "\" defined in your config is not available. Using fallback locale: "
                + BedwarsRevol.getInstance().getFallbackLocale()));
        inputStream = BedwarsRevol.getInstance()
            .getResource("locale/" + BedwarsRevol.getInstance().getFallbackLocale() + ".yml");
      }
    }
    try {
      reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      this.load(reader);
    } catch (Exception e) {
      BedwarsRevol.getInstance().getServer().getConsoleSender().sendMessage(
          ChatWriterNew.pluginMessage(ChatColor.RED + "Failed to load localization language!"));
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
