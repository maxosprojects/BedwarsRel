package io.github.bedwarsrevolution.utils;

import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.ChatColor;

public class ChatWriterNew {

  public static String pluginMessage(String str) {
    return ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("chat-prefix",
            ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]")
        + " " + ChatColor.WHITE + str);
  }

}
