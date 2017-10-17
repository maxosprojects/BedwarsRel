package io.github.bedwarsrevolution.updater;

import io.github.bedwarsrel.shop.ItemStackParser;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConfigUpdaterNew {

  @SuppressWarnings("unchecked")
  public void addConfigs() {
    // <1.1.3>
    BedwarsRevol.getInstance().getConfig().addDefault("check-updates", true);
    // </1.1.3>

    // <1.1.4>
    BedwarsRevol.getInstance().getConfig().addDefault("sign.first-line", "$title$");
    BedwarsRevol.getInstance().getConfig().addDefault("sign.second-line", "$regionname$");
    BedwarsRevol.getInstance().getConfig().addDefault("sign.third-line",
        "Players &7[&b$currentplayers$&7/&b$maxplayers$&7]");
    BedwarsRevol.getInstance().getConfig().addDefault("sign.fourth-line", "$status$");
    BedwarsRevol.getInstance().getConfig().addDefault("specials.rescue-platform.break-time", 10);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.rescue-platform.using-wait-time", 20);
    BedwarsRevol.getInstance().getConfig().addDefault("explodes.destroy-worldblocks", false);
    BedwarsRevol.getInstance().getConfig().addDefault("explodes.destroy-beds", false);
    BedwarsRevol.getInstance().getConfig().addDefault("explodes.drop-blocking", false);
    BedwarsRevol.getInstance().getConfig().addDefault("rewards.enabled", false);

    List<String> defaultRewards = new ArrayList<String>();
    defaultRewards.add("/example {player} {score}");
    BedwarsRevol.getInstance().getConfig().addDefault("rewards.player-win", defaultRewards);
    BedwarsRevol.getInstance().getConfig().addDefault("rewards.player-end-game", defaultRewards);
    // </1.1.4>

    // <1.1.6>
    BedwarsRevol.getInstance().getConfig().addDefault("global-messages", true);
    BedwarsRevol.getInstance().getConfig().addDefault("player-settings.one-stack-on-shift", false);
    // </1.1.6>

    // <1.1.8>
    BedwarsRevol.getInstance().getConfig().addDefault("seperate-game-chat", true);
    BedwarsRevol.getInstance().getConfig().addDefault("seperate-spectator-chat", false);
    // </1.1.8>

    // <1.1.9>
    BedwarsRevol.getInstance().getConfig().addDefault("specials.trap.play-sound", true);
    // </1.1.9>

    // <1.1.11>
    BedwarsRevol.getInstance().getConfig().addDefault("specials.magnetshoe.probability", 75);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.magnetshoe.boots", "IRON_BOOTS");
    // </1.1.11>

    // <1.1.13>
    BedwarsRevol.getInstance().getConfig().addDefault("specials.rescue-platform.block", "GLASS");
    BedwarsRevol.getInstance().getConfig().addDefault("specials.rescue-platform.block", "BLAZE_ROD");
    BedwarsRevol.getInstance().getConfig().addDefault("ingame-chatformat-all",
        "[$all$] <$team$>$player$: $msg$");
    BedwarsRevol.getInstance().getConfig().addDefault("ingame-chatformat", "<$team$>$player$: $msg$");
    // </1.1.13>

    // <1.1.14>
    BedwarsRevol.getInstance().getConfig().addDefault("overwrite-names", false);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.break-time", 0);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.wait-time", 20);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.can-break", true);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.item", "BRICK");
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.block", "SANDSTONE");
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.width", 4);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.height", 4);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.protection-wall.distance", 2);

    if (BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_8")) {
      BedwarsRevol.getInstance().getConfig().addDefault("bed-sound", "ENDERDRAGON_GROWL");
    } else {
      BedwarsRevol.getInstance().getConfig().addDefault("bed-sound", "ENTITY_ENDERDRAGON_GROWL");
    }

    try {
      Sound.valueOf(
          BedwarsRevol.getInstance().getStringConfig("bed-sound", "ENDERDRAGON_GROWL").toUpperCase());
    } catch (Exception e) {
      if (BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_8")) {
        BedwarsRevol.getInstance().getConfig().set("bed-sound", "ENDERDRAGON_GROWL");
      } else {
        BedwarsRevol.getInstance().getConfig().set("bed-sound", "ENTITY_ENDERDRAGON_GROWL");
      }
    }
    // </1.1.14>

    // <1.1.15>
    BedwarsRevol.getInstance().getConfig().addDefault("store-game-records", true);
    BedwarsRevol.getInstance().getConfig().addDefault("store-game-records-holder", true);
    BedwarsRevol.getInstance().getConfig().addDefault("statistics.scores.record", 100);
    BedwarsRevol.getInstance().getConfig().addDefault("game-block", "BED_BLOCK");
    // </1.1.15>

    // <1.2.0>
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.enabled", true);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.title-fade-in", 1.5);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.title-stay", 5.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.title-fade-out", 2.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.subtitle-fade-in", 1.5);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.subtitle-stay", 5.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.win.subtitle-fade-out", 2.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.enabled", false);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.title-fade-in", 1.5);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.title-stay", 5.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.title-fade-out", 2.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.subtitle-fade-in", 1.5);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.subtitle-stay", 5.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.map.subtitle-fade-out", 2.0);
    BedwarsRevol.getInstance().getConfig().addDefault("player-drops", false);
    BedwarsRevol.getInstance().getConfig().addDefault("bungeecord.spigot-restart", true);
    BedwarsRevol.getInstance().getConfig().addDefault("place-in-liquid", true);
    BedwarsRevol.getInstance().getConfig().addDefault("friendlybreak", true);
    BedwarsRevol.getInstance().getConfig().addDefault("breakable-blocks", Arrays.asList("none"));
    BedwarsRevol.getInstance().getConfig().addDefault("update-infos", true);
    BedwarsRevol.getInstance().getConfig().addDefault("lobby-chatformat", "$player$: $msg$");
    // <1.2.0>

    // <1.2.1>
    BedwarsRevol.getInstance().getConfig().addDefault("statistics.bed-destroyed-kills", false);
    BedwarsRevol.getInstance().getConfig().addDefault("rewards.player-destroy-bed",
        Arrays.asList("/example {player} {score}"));
    BedwarsRevol.getInstance().getConfig().addDefault("rewards.player-kill",
        Arrays.asList("/example {player} 10"));
    BedwarsRevol.getInstance().getConfig().addDefault("specials.tntsheep.fuse-time", 8.0);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.countdown.enabled", true);
    BedwarsRevol.getInstance().getConfig().addDefault("titles.countdown.format", "&3{countdown}");
    BedwarsRevol.getInstance().getConfig().addDefault("specials.tntsheep.speed", 0.4D);
    // </1.2.1>

    // <1.2.2>
    BedwarsRevol.getInstance().getConfig().addDefault("global-autobalance", false);
    BedwarsRevol.getInstance().getConfig().addDefault("scoreboard.format-bed-destroyed",
        "&c$status$ $team$");
    BedwarsRevol
        .getInstance().getConfig().addDefault("scoreboard.format-bed-alive", "&a$status$ $team$");
    BedwarsRevol
        .getInstance().getConfig().addDefault("scoreboard.format-title", "&e$region$&f - $time$");
    BedwarsRevol.getInstance().getConfig().addDefault("teamname-on-tab", false);
    // </1.2.2>

    // <1.2.3>
    BedwarsRevol.getInstance().getConfig().addDefault("bungeecord.motds.full", "&c[Full]");
    BedwarsRevol.getInstance().getConfig().addDefault("teamname-in-chat", false);
    BedwarsRevol.getInstance().getConfig().addDefault("hearts-on-death", true);
    BedwarsRevol.getInstance().getConfig().addDefault("lobby-scoreboard.title", "&eBEDWARS");
    BedwarsRevol.getInstance().getConfig().addDefault("lobby-scoreboard.enabled", true);
    BedwarsRevol.getInstance().getConfig().addDefault("lobby-scoreboard.content",
        Arrays.asList("", "&fMap: &2$regionname$", "&fPlayers: &2$players$&f/&2$maxplayers$", "",
            "&fWaiting ...", ""));
    BedwarsRevol.getInstance().getConfig().addDefault("jointeam-entity.show-name", true);
    // </1.2.3>

    // <1.2.6>
    BedwarsRevol.getInstance().getConfig().addDefault("die-on-void", false);
    BedwarsRevol.getInstance().getConfig().addDefault("global-chat-after-end", true);
    // </1.2.6>

    // <1.2.7>
    BedwarsRevol.getInstance().getConfig().addDefault("holographic-stats.show-prefix", false);
    BedwarsRevol.getInstance().getConfig().addDefault("holographic-stats.name-color", "&7");
    BedwarsRevol.getInstance().getConfig().addDefault("holographic-stats.value-color", "&e");
    BedwarsRevol.getInstance().getConfig().addDefault("holographic-stats.head-line",
        "Your &eBEDWARS&f stats");
    BedwarsRevol.getInstance().getConfig().addDefault("lobby-gamemode", 0);
    BedwarsRevol.getInstance().getConfig().addDefault("statistics.show-on-game-end", true);
    BedwarsRevol.getInstance().getConfig().addDefault("allow-crafting", false);
    // </1.2.7>

    // <1.2.8>
    BedwarsRevol.getInstance().getConfig().addDefault("specials.tntsheep.explosion-factor", 1.0);
    BedwarsRevol.getInstance().getConfig().addDefault("bungeecord.full-restart", true);
    BedwarsRevol.getInstance().getConfig().addDefault("lobbytime-full", 15);
    BedwarsRevol.getInstance().getConfig().addDefault("bungeecord.endgame-in-lobby", true);
    // </1.2.8>

    // <1.3.0>
    BedwarsRevol.getInstance().getConfig().addDefault("hearts-in-halfs", true);
    // </1.3.0>

    // <1.3.1>
    if (BedwarsRevol.getInstance().getConfig().isString("chat-to-all-prefix")) {
      String chatToAllPrefixString = BedwarsRevol.getInstance().getConfig()
          .getString("chat-to-all-prefix");
      BedwarsRevol.getInstance().getConfig().set("chat-to-all-prefix",
          Arrays.asList(chatToAllPrefixString));
    }
    if (BedwarsRevol.getInstance().getConfig().isList("breakable-blocks")) {
      List<String> breakableBlocks =
          (List<String>) BedwarsRevol.getInstance().getConfig().getList("breakable-blocks");
      BedwarsRevol.getInstance().getConfig().set("breakable-blocks.list", breakableBlocks);
    }
    BedwarsRevol.getInstance().getConfig().addDefault("breakable-blocks.use-as-blacklist", false);
    // </1.3.1>

    // <1.3.2>
    BedwarsRevol.getInstance().getConfig().addDefault("statistics.player-leave-kills", false);

    List<PotionEffect> oldPotions = new ArrayList<PotionEffect>();

    if (BedwarsRevol.getInstance().getConfig().getBoolean("specials.trap.blindness.enabled")) {
      oldPotions.add(new PotionEffect(PotionEffectType.BLINDNESS,
          BedwarsRevol.getInstance().getConfig().getInt("specials.trap.duration"),
          BedwarsRevol.getInstance().getConfig().getInt("specials.trap.blindness.amplifier"), true,
          BedwarsRevol.getInstance().getConfig().getBoolean("specials.trap.show-particles")));
    }
    if (BedwarsRevol.getInstance().getConfig().getBoolean("specials.trap.slowness.enabled")) {
      oldPotions.add(new PotionEffect(PotionEffectType.SLOW,
          BedwarsRevol.getInstance().getConfig().getInt("specials.trap.duration"),
          BedwarsRevol.getInstance().getConfig().getInt("specials.trap.slowness.amplifier"), true,
          BedwarsRevol.getInstance().getConfig().getBoolean("specials.trap.show-particles")));
    }
    if (BedwarsRevol.getInstance().getConfig().getBoolean("specials.trap.weakness.enabled")) {
      oldPotions.add(new PotionEffect(PotionEffectType.WEAKNESS,
          BedwarsRevol.getInstance().getConfig().getInt("specials.trap.duration"),
          BedwarsRevol.getInstance().getConfig().getInt("specials.trap.weakness.amplifier"), true,
          BedwarsRevol.getInstance().getConfig().getBoolean("specials.trap.show-particles")));
    }
    BedwarsRevol.getInstance().getConfig().addDefault("specials.trap.effects", oldPotions);
    BedwarsRevol.getInstance().getConfig().set("specials.trap.duration", null);
    BedwarsRevol.getInstance().getConfig().set("specials.trap.blindness", null);
    BedwarsRevol.getInstance().getConfig().set("specials.trap.slowness", null);
    BedwarsRevol.getInstance().getConfig().set("specials.trap.weakness", null);
    BedwarsRevol.getInstance().getConfig().set("specials.trap.show-particles", null);

    List<PotionEffect> potionEffectList = new ArrayList<>();
    potionEffectList.add(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 2, true, true));
    potionEffectList.add(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 2, true, true));
    potionEffectList.add(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 2, true, true));
    BedwarsRevol.getInstance().getConfig().addDefault("specials.trap.effects", potionEffectList);
    // </1.3.2>

    // <1.3.3>
    BedwarsRevol.getInstance().getConfig().addDefault("show-team-in-actionbar", false);
    BedwarsRevol.getInstance().getConfig().addDefault("send-error-data", true);
    BedwarsRevol.getInstance().getConfig().addDefault("player-settings.old-shop-as-default", false);
    // </1.3.3>

    // <1.3.4>
    BedwarsRevol.getInstance().getConfig().addDefault("keep-inventory-on-death", false);
    BedwarsRevol.getInstance().getConfig().addDefault("use-internal-shop", true);
    BedwarsRevol.getInstance().getConfig().addDefault("save-inventory", true);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.arrow-blocker.protection-time", 10);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.arrow-blocker.using-wait-time", 30);
    BedwarsRevol.getInstance().getConfig().addDefault("specials.arrow-blocker.item", "ender_eye");
    // </1.3.4>

    // <1.3.5>
    BedwarsRevol.getInstance().getConfig().addDefault("spawn-resources-in-chest", true);
    BedwarsRevol.getInstance().getConfig().addDefault("database.table-prefix", "bw_");
    Object ressourceObject = BedwarsRevol.getInstance().getConfig().get("ressource");
    if (ressourceObject != null) {
      BedwarsRevol.getInstance().getConfig().set("resource", ressourceObject);
      BedwarsRevol.getInstance().getConfig().set("ressource", null);
    }

    ConfigurationSection resourceSection = BedwarsRevol.getInstance().getConfig()
        .getConfigurationSection("resource");
    for (Entry<String, Object> entry : resourceSection.getValues(false).entrySet()) {
      if (!BedwarsRevol.getInstance().getConfig().isList("resource." + entry.getKey() + ".item")) {
        ItemStackParser parser = new ItemStackParser(entry.getValue());
        ItemStack item = parser.parse();
        if (item != null) {
          List<Map<String, Object>> itemList = new ArrayList<>();
          itemList.add(item.serialize());
          resourceSection.set(entry.getKey() + ".item", itemList);
          resourceSection.set(entry.getKey() + ".amount", null);
          resourceSection.set(entry.getKey() + ".name", null);
        }
      }
    }
    // </1.3.5>

    // <1.3.7>
    BedwarsRevol.getInstance().getConfig().addDefault("specials.rescue-platform.distance", 1);
    // </1.3.7>
  }
}
