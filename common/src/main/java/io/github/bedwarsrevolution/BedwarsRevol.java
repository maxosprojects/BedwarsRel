package io.github.bedwarsrevolution;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.commands.AddGameCommand;
import io.github.bedwarsrevolution.commands.AddHoloCommand;
import io.github.bedwarsrevolution.commands.AddTeamCommand;
import io.github.bedwarsrevolution.commands.AddTeamJoinCommand;
import io.github.bedwarsrevolution.commands.BaseCommand;
import io.github.bedwarsrevolution.commands.ClearSpawnerCommand;
import io.github.bedwarsrevolution.commands.DebugPasteCommand;
import io.github.bedwarsrevolution.commands.GameTimeCommand;
import io.github.bedwarsrevolution.commands.HelpCommand;
import io.github.bedwarsrevolution.commands.ItemsPasteCommand;
import io.github.bedwarsrevolution.commands.JoinGameCommand;
import io.github.bedwarsrevolution.commands.KickCommand;
import io.github.bedwarsrevolution.commands.LeaveGameCommand;
import io.github.bedwarsrevolution.commands.ListGamesCommand;
import io.github.bedwarsrevolution.commands.RegionNameCommand;
import io.github.bedwarsrevolution.commands.ReloadCommand;
import io.github.bedwarsrevolution.commands.RemoveGameCommand;
import io.github.bedwarsrevolution.commands.RemoveHoloCommand;
import io.github.bedwarsrevolution.commands.RemoveTeamCommand;
import io.github.bedwarsrevolution.commands.SaveGameCommand;
import io.github.bedwarsrevolution.commands.SetAutobalanceCommand;
import io.github.bedwarsrevolution.commands.SetBaseRegionCommand;
import io.github.bedwarsrevolution.commands.SetBedCommand;
import io.github.bedwarsrevolution.commands.SetBuilderCommand;
import io.github.bedwarsrevolution.commands.SetGameBlockCommand;
import io.github.bedwarsrevolution.commands.SetLobbyCommand;
import io.github.bedwarsrevolution.commands.SetMainLobbyCommand;
import io.github.bedwarsrevolution.commands.SetMinPlayersCommand;
import io.github.bedwarsrevolution.commands.SetRegionCommand;
import io.github.bedwarsrevolution.commands.SetSpawnCommand;
import io.github.bedwarsrevolution.commands.SetSpawnerCommand;
import io.github.bedwarsrevolution.commands.SetTargetCommand;
import io.github.bedwarsrevolution.commands.SetTeamChestCommand;
import io.github.bedwarsrevolution.commands.StartGameCommand;
import io.github.bedwarsrevolution.commands.StatsCommand;
import io.github.bedwarsrevolution.commands.StopGameCommand;
import io.github.bedwarsrevolution.game.GameManagerNew;
import io.github.bedwarsrevolution.game.ResourceSpawnerNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.listeners.BlockListenerNew;
import io.github.bedwarsrevolution.listeners.ChunkListenerNew;
import io.github.bedwarsrevolution.listeners.EntityListenerNew;
import io.github.bedwarsrevolution.listeners.HangingListenerNew;
import io.github.bedwarsrevolution.listeners.InvisibilityPotionListenerNew;
import io.github.bedwarsrevolution.listeners.PlayerListenerNew;
import io.github.bedwarsrevolution.listeners.PlayerSpigotListenerNew;
import io.github.bedwarsrevolution.listeners.ServerListenerNew;
import io.github.bedwarsrevolution.listeners.SignListenerNew;
import io.github.bedwarsrevolution.listeners.SoundListenerNew;
import io.github.bedwarsrevolution.listeners.WeatherListenerNew;
import io.github.bedwarsrevolution.listeners.events.EntityPickupItemEventListenerNew;
import io.github.bedwarsrevolution.listeners.events.PlayerPickUpItemEventListenerNew;
import io.github.bedwarsrevolution.listeners.events.PlayerSwapHandItemsEventListenerNew;
import io.github.bedwarsrevolution.localization.LocalizationConfigNew;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeRegistry;
import io.github.bedwarsrevolution.updater.ConfigUpdaterNew;
import io.github.bedwarsrevolution.utils.BedwarsCommandExecutorNew;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.ScoreboardManager;

public class BedwarsRevol extends JavaPlugin {

  public static int PROJECT_ID = 91744;
  private static BedwarsRevol instance = null;
  private static Boolean locationSerializable = null;
  private List<Material> breakableTypes = null;
//  @Getter
//  private Bugsnag bugsnag;
  private ArrayList<BaseCommand> commands = new ArrayList<>();
  private Package craftbukkit = null;
//  @Getter
//  @Setter
//  private DatabaseManager databaseManager = null;
  @Getter
  private GameManagerNew gameManager = null;
//  private IHologramInteraction holographicInteraction = null;
  private boolean isSpigot = false;
  @Getter
  private HashMap<String, LocalizationConfigNew> localization = new HashMap<>();
  private Package minecraft = null;
  @Getter
  private HashMap<UUID, String> playerLocales = new HashMap<>();
//  private PlayerStatisticManager playerStatisticManager = null;
  private ScoreboardManager scoreboardManager = null;
  private YamlConfiguration shopConfig = null;
  private BukkitTask timeTask = null;
  private BukkitTask updateChecker = null;
  private String version = null;
  @Getter
  private ProtocolManager protocolManager;

  public static String _l(CommandSender commandSender, String key, String singularValue,
      Map<String, String> params) {
    return BedwarsRevol
        ._l(BedwarsRevol.getInstance().getSenderLocale(commandSender), key, singularValue, params);
  }

  public static String _l(String locale, String key, String singularValue,
      Map<String, String> params) {
    if ("1".equals(params.get(singularValue))) {
      return BedwarsRevol._l(locale, key + "-one", params);
    }
    return BedwarsRevol._l(locale, key, params);
  }

  public static String _l(CommandSender commandSender, String key, Map<String, String> params) {
    return BedwarsRevol._l(BedwarsRevol.getInstance().getSenderLocale(commandSender), key, params);
  }

  public static String _l(String locale, String key, Map<String, String> params) {
    if (!BedwarsRevol.getInstance().localization.containsKey(locale)) {
      BedwarsRevol.getInstance().loadLocalization(locale);
    }
    return (String) BedwarsRevol.getInstance().getLocalization().get(locale).get(key, params);
  }

  public static String _l(CommandSender commandSender, String key) {
    return BedwarsRevol._l(BedwarsRevol.getInstance().getSenderLocale(commandSender), key);
  }

  public static String _l(String key) {
    return BedwarsRevol._l(BedwarsRevol.getInstance().getConfig().getString("locale"), key);
  }

  public static String _l(String key, Map<String, String> params) {
    String locale = BedwarsRevol.getInstance().getConfig().getString("locale");
    return BedwarsRevol._l(locale, key, params);
  }

  public static String _l(String locale, String key) {
    if (!BedwarsRevol.getInstance().localization.containsKey(locale)) {
      BedwarsRevol.getInstance().loadLocalization(locale);
    }
    return (String) BedwarsRevol.getInstance().getLocalization().get(locale).get(key);
  }

  public static BedwarsRevol getInstance() {
    return BedwarsRevol.instance;
  }

  public boolean allPlayersBackToMainLobby() {
    if (this.getConfig().contains("endgame.all-players-to-mainlobby")
        && this.getConfig().isBoolean("endgame.all-players-to-mainlobby")) {
      return this.getConfig().getBoolean("endgame.all-players-to-mainlobby");
    }

    return false;

  }

//  private void checkUpdates() {
//    try {
//      if (this.getBooleanConfig("check-updates", true)) {
//        this.updateChecker = new BukkitRunnable() {
//
//          @Override
//          public void run() {
//            final BukkitRunnable task = this;
//            UpdateCallback callback = new UpdateCallback() {
//
//              @Override
//              public void onFinish(PluginUpdater updater) {
//                if (updater.getResult() == UpdateResult.SUCCESS) {
//                  task.cancel();
//                }
//              }
//            };
//
//            new PluginUpdater(
//                BedwarsRevol.getInstance(), BedwarsRevol.PROJECT_ID, BedwarsRevol.getInstance().getFile(),
//                PluginUpdater.UpdateType.DEFAULT, callback,
//                BedwarsRevol.getInstance().getBooleanConfig("update-infos", true));
//          }
//
//        }.runTaskTimerAsynchronously(BedwarsRevol.getInstance(), 40L, 36000L);
//      }
//    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
//      this.getServer().getConsoleSender().sendMessage(
//          ChatWriterNew.pluginMessage(ChatColor.RED + "Check for updates not successful: Error!"));
//    }
//  }
//
//
//  private void disableBugsnag() {
//    this.bugsnag.addCallback(new Callback() {
//      @Override
//      public void beforeNotify(Report report) {
//        report.cancel();
//      }
//    });
//  }
//
//  public void dispatchRewardCommands(List<String> commands, Map<String, String> replacements) {
//    for (String command : commands) {
//      command = command.trim();
//      if ("".equals(command)) {
//        continue;
//      }
//
//      if ("none".equalsIgnoreCase(command)) {
//        break;
//      }
//
//      if (command.startsWith("/")) {
//        command = command.substring(1);
//      }
//
//      for (Entry<String, String> entry : replacements.entrySet()) {
//        command = command.replace(entry.getKey(), entry.getValue());
//      }
//
//      BedwarsRevol.getInstance().getServer()
//          .dispatchCommand(BedwarsRevol.getInstance().getServer().getConsoleSender(), command);
//    }
//  }
//
//  private void enableBugsnag() {
//    this.bugsnag.addCallback(new Callback() {
//      @Override
//      public void beforeNotify(Report report) {
//        Boolean shouldBeSent = false;
//        for (StackTraceElement stackTraceElement : report.getException().getStackTrace()) {
//          if (stackTraceElement.toString().contains("io.github.bedwarsrevolution.BedwarsRevol")) {
//            shouldBeSent = true;
//            break;
//          }
//        }
//        if (!shouldBeSent) {
//          report.cancel();
//        }
//
//        report.setUserId(SupportData.getIdentifier());
//        if (!SupportData.getPluginVersionBuild().equalsIgnoreCase("unknown")) {
//          report.addToTab("Server", "Version Build",
//              BedwarsRevol.getInstance().getDescription().getVersion() + " "
//                  + SupportData.getPluginVersionBuild());
//        }
//        report.addToTab("Server", "Version", SupportData.getServerVersion());
//        report.addToTab("Server", "Version Bukkit", SupportData.getBukkitVersion());
//        report.addToTab("Server", "Server Mode", SupportData.getServerMode());
//        report.addToTab("Server", "Plugins", SupportData.getPlugins());
//      }
//    });
//  }

  private ArrayList<BaseCommand> filterCommandsByPermission(ArrayList<BaseCommand> commands,
      String permission) {
    Iterator<BaseCommand> it = commands.iterator();

    while (it.hasNext()) {
      BaseCommand command = it.next();
      if (!command.getPermission().equals(permission)) {
        it.remove();
      }
    }

    return commands;
  }

  public List<String> getAllowedCommands() {
    FileConfiguration config = this.getConfig();
    if (config.contains("allowed-commands") && config.isList("allowed-commands")) {
      return config.getStringList("allowed-commands");
    }

    return new ArrayList<String>();
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getBaseCommands() {
    ArrayList<BaseCommand> commands = (ArrayList<BaseCommand>) this.commands.clone();
    commands = this.filterCommandsByPermission(commands, "base");

    return commands;
  }

  public boolean getBooleanConfig(String key, boolean defaultBool) {
    FileConfiguration config = this.getConfig();
    if (config.contains(key) && config.isBoolean(key)) {
      return config.getBoolean(key);
    }
    return defaultBool;
  }

  public String getBungeeHub() {
    if (this.getConfig().contains("bungeecord.hubserver")) {
      return this.getConfig().getString("bungeecord.hubserver");
    }

    return null;
  }

  public ArrayList<BaseCommand> getCommands() {
    return this.commands;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getCommandsByPermission(String permission) {
    ArrayList<BaseCommand> commands = (ArrayList<BaseCommand>) this.commands.clone();
    commands = this.filterCommandsByPermission(commands, permission);

    return commands;
  }

  public Package getCraftBukkit() {
    try {
      if (this.craftbukkit == null) {
        return Package.getPackage("org.bukkit.craftbukkit."
            + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
      } else {
        return this.craftbukkit;
      }
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender().sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
          + BedwarsRevol._l(this.getServer().getConsoleSender(), "errors.packagenotfound",
          ImmutableMap.of("package", "craftbukkit"))));
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  public Class getCraftBukkitClass(String classname) {
    try {
      if (this.craftbukkit == null) {
        this.craftbukkit = this.getCraftBukkit();
      }

      return Class.forName(this.craftbukkit.getName() + "." + classname);
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender()
          .sendMessage(ChatWriterNew.pluginMessage(
              ChatColor.RED + BedwarsRevol
                  ._l(this.getServer().getConsoleSender(), "errors.classnotfound",
                      ImmutableMap.of("package", "craftbukkit", "class", classname))));
      return null;
    }
  }

  public String getCurrentVersion() {
    return this.version;
  }

  public String getFallbackLocale() {
    return "en_US";
  }

//  public IHologramInteraction getHolographicInteractor() {
//    return this.holographicInteraction;
//  }

  public int getIntConfig(String key, int defaultInt) {
    FileConfiguration config = this.getConfig();
    if (config.contains(key) && config.isInt(key)) {
      return config.getInt(key);
    }
    return defaultInt;
  }

  private boolean getIsSpigot() {
    try {
      Package spigotPackage = Package.getPackage("org.spigotmc");
      return (spigotPackage != null);
    } catch (Exception e) {
//      BedwarsRevol.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }

    return false;
  }

  public Package getMinecraftPackage() {
    try {
      if (this.minecraft == null) {
        return Package.getPackage("net.minecraft.server."
            + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
      } else {
        return this.minecraft;
      }
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender().sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
          + BedwarsRevol._l(this.getServer().getConsoleSender(), "errors.packagenotfound",
          ImmutableMap.of("package", "minecraft server"))));
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  public Class getMinecraftServerClass(String classname) {
    try {
      if (this.minecraft == null) {
        this.minecraft = this.getMinecraftPackage();
      }

      return Class.forName(this.minecraft.getName() + "." + classname);
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender()
          .sendMessage(ChatWriterNew.pluginMessage(
              ChatColor.RED + BedwarsRevol
                  ._l(this.getServer().getConsoleSender(), "errors.classnotfound",
                      ImmutableMap.of("package", "minecraft server", "class", classname))));
      return null;
    }
  }

  public String getMissingHoloDependency() {
    if (!BedwarsRevol.getInstance().isHologramsEnabled()) {
      String missingHoloDependency = null;
      if (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")
          || this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
        if (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")) {
          missingHoloDependency = "PacketListenerApi";
          return missingHoloDependency;
        }
        if (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
          missingHoloDependency = "ProtocolLib";
          return missingHoloDependency;
        }
      } else {
        missingHoloDependency = "HolographicDisplays and ProtocolLib";
        return missingHoloDependency;
      }
    }
    return null;
  }

//  public PlayerStatisticManager getPlayerStatisticManager() {
//    return this.playerStatisticManager;
//  }

  public Integer getRespawnProtectionTime() {
    FileConfiguration config = this.getConfig();
    if (config.contains("respawn-protection") && config.isInt("respawn-protection")) {
      return config.getInt("respawn-protection");
    }
    return 0;
  }

  public ScoreboardManager getScoreboardManager() {
    return this.scoreboardManager;
  }

  public String getSenderLocale(CommandSender commandSender) {
    String locale = BedwarsRevol.getInstance().getConfig().getString("locale");
    if (commandSender instanceof Player) {
      Player player = (Player) commandSender;
      if (BedwarsRevol.getInstance().getPlayerLocales().containsKey(player.getUniqueId())) {
        locale = BedwarsRevol.getInstance().getPlayerLocales().get(player.getUniqueId());
      }
    }
    return locale;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<BaseCommand> getSetupCommands() {
    ArrayList<BaseCommand> commands = (ArrayList<BaseCommand>) this.commands.clone();
    commands = this.filterCommandsByPermission(commands, "setup");

    return commands;
  }

  public FileConfiguration getShopConfig() {
    return this.shopConfig;
  }

//  public StorageType getStatisticStorageType() {
//    String storage = this.getStringConfig("statistics.storage", "yaml");
//    return StorageType.getByName(storage);
//  }

  public String getStringConfig(String key, String defaultString) {
    FileConfiguration config = this.getConfig();
    if (config.contains(key) && config.isString(key)) {
      return config.getString(key);
    }
    return defaultString;
  }

  public Class<?> getVersionRelatedClass(String className) {
    try {
      Class<?> clazz = Class.forName(
          "io.github.bedwarsrevolution.com." + this.getCurrentVersion().toLowerCase() + "." + className);
      return clazz;
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      this.getServer().getConsoleSender()
          .sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
              + "Couldn't find version related class io.github.bedwarsrevolution.com."
              + this.getCurrentVersion() + "." + className));
    }

    return null;
  }

  public String getYamlDump(YamlConfiguration config) {
    try {
      String fullstring = config.saveToString();
      String endstring = fullstring;
      endstring = UtilsNew.unescape_perl_string(fullstring);

      return endstring;
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
    }

    return null;
  }

  public boolean isBreakableType(Material type) {
    return ((BedwarsRevol.getInstance().getConfig().getBoolean("breakable-blocks.use-as-blacklist")
        && !this.breakableTypes.contains(type))
        || (!BedwarsRevol.getInstance().getConfig().getBoolean("breakable-blocks.use-as-blacklist")
        && this.breakableTypes.contains(type)));
  }

  public boolean isBungee() {
    return this.getConfig().getBoolean("bungeecord.enabled");
  }

  public boolean isHologramsEnabled() {
    return (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")
        && this.getServer().getPluginManager().isPluginEnabled("PacketListenerApi"))
        || (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")
        && this.getServer().getPluginManager().isPluginEnabled("ProtocolLib"));
  }

  public boolean isLocationSerializable() {
    if (BedwarsRevol.locationSerializable == null) {
      try {
        Location.class.getMethod("serialize");
        BedwarsRevol.locationSerializable = true;
      } catch (Exception ex) {
//        BedwarsRevol.getInstance().getBugsnag().notify(ex);
        BedwarsRevol.locationSerializable = false;
      }
    }

    return BedwarsRevol.locationSerializable;
  }

  public boolean isMineshafterPresent() {
    try {
      Class.forName("mineshafter.MineServer");
      return true;
    } catch (Exception e) {
      // NO ERROR
      return false;
    }
  }

  public boolean isSpigot() {
    return this.isSpigot;
  }

  public void loadConfigInUTF() {
    File configFile = new File(this.getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      return;
    }

    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
      this.getConfig().load(reader);
    } catch (Exception e) {
//      BedwarsRevol.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }

    if (this.getConfig() == null) {
      return;
    }

    // load breakable materials
    this.breakableTypes = new ArrayList<Material>();
    for (String material : this.getConfig().getStringList("breakable-blocks.list")) {
      if (material.equalsIgnoreCase("none")) {
        continue;
      }

      Material mat = UtilsNew.parseMaterial(material);
      if (mat == null) {
        continue;
      }

      if (this.breakableTypes.contains(mat)) {
        continue;
      }

      this.breakableTypes.add(mat);
    }
  }

//  private void loadStatisticsDatabase() {
//    if (!this.getBooleanConfig("statistics.enabled", false)) {
//      return;
//    }
//
//    String host = this.getStringConfig("database.host", null);
//    int port = this.getIntConfig("database.port", 3306);
//    String user = this.getStringConfig("database.user", null);
//    String password = this.getStringConfig("database.password", null);
//    String db = this.getStringConfig("database.db", null);
//    String tablePrefix = this.getStringConfig("database.table-prefix", "bw_");
//
//    if (BedwarsRevol.getInstance().getStatisticStorageType() == StorageType.YAML) {
//      this.databaseManager = new YamlDatabaseManager();
//    } else if (BedwarsRevol.getInstance().getStatisticStorageType() == StorageType.DATABASE) {
//      this.databaseManager = new MysqlDatabaseManager(host, port, user, password, db, tablePrefix);
//    }
//
//    if (BedwarsRevol.getInstance().getStatisticStorageType() != StorageType.YAML
//        && BedwarsRevol.getInstance().getStatisticStorageType() != StorageType.DATABASE) {
//      return;
//    }
//
//    this.getServer().getConsoleSender()
//        .sendMessage(ChatWriterNew.pluginMessage(ChatColor.GREEN + "Initialize database ..."));
//
//    this.databaseManager.initialize();
//  }

  private void loadLocalization(String locale) {
    if (!this.localization.containsKey(locale)) {
      this.localization.put(locale, new LocalizationConfigNew(locale));
    }
  }

  public void loadShop() {
    File file = new File(BedwarsRevol.getInstance().getDataFolder(), "shop.yml");
    if (!file.exists()) {
      // create default file
      this.saveResource("shop.yml", false);

      // wait until it's really saved
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
//        BedwarsRevol.getInstance().getBugsnag().notify(e);
        e.printStackTrace();
      }
    }

    this.shopConfig = new YamlConfiguration();

    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
      this.shopConfig.load(reader);
    } catch (Exception e) {
//      BedwarsRevol.getInstance().getBugsnag().notify(e);
      this.getServer().getConsoleSender().sendMessage(
          ChatWriterNew.pluginMessage(ChatColor.RED + "Couldn't load shop! Error in parsing shop!"));
      e.printStackTrace();
    }
  }

//  private void loadStatistics() {
//    this.playerStatisticManager = new PlayerStatisticManager();
//  }

  private String loadVersion() {
    String packName = Bukkit.getServer().getClass().getPackage().getName();
    return packName.substring(packName.lastIndexOf('.') + 1);
  }

  public boolean metricsEnabled() {
    if (this.getConfig().contains("plugin-metrics")
        && this.getConfig().isBoolean("plugin-metrics")) {
      return this.getConfig().getBoolean("plugin-metrics");
    }

    return false;
  }

  @Override
  public void onDisable() {
    this.stopTimeListener();
    this.gameManager.stopGames();

//    if (this.isHologramsEnabled() && this.holographicInteraction != null) {
//      this.holographicInteraction.unloadHolograms();
//    }
  }

  @Override
  public void onEnable() {
    BedwarsRevol.instance = this;

    if (this.getDescription().getVersion().contains("-SNAPSHOT")
        && System.getProperty("IReallyKnowWhatIAmDoingISwear") == null) {
      this.getServer().getConsoleSender().sendMessage(ChatWriterNew
          .pluginMessage(ChatColor.RED + "*** Warning, you are using a development build ***"));
      this.getServer().getConsoleSender().sendMessage(ChatWriterNew
          .pluginMessage(ChatColor.RED + "*** You will get NO support regarding this build ***"));
      this.getServer().getConsoleSender().sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED
          + "*** Please download a stable build from https://github.com/maxosprojects/BedwarsRel/releases ***"));
    }

//    this.registerBugsnag();

    this.protocolManager = ProtocolLibrary.getProtocolManager();

    // register classes
    this.registerConfigurationClasses();

    // save default config
    this.saveDefaultConfig();
    this.loadConfigInUTF();

    this.getConfig().options().copyDefaults(true);
    this.getConfig().options().copyHeader(true);

    this.craftbukkit = this.getCraftBukkit();
    this.minecraft = this.getMinecraftPackage();
    this.version = this.loadVersion();

    ConfigUpdaterNew configUpdater = new ConfigUpdaterNew();
    configUpdater.addConfigs();
    this.saveConfiguration();
    this.loadConfigInUTF();

//    if (this.getBooleanConfig("send-error-data", true) && this.bugsnag != null) {
//      this.enableBugsnag();
//    } else {
//      this.disableBugsnag();
//    }

    this.loadShop();

    this.isSpigot = this.getIsSpigot();
//    this.loadStatisticsDatabase();

    this.registerCommands();
    this.registerListeners();

    this.gameManager = new GameManagerNew();

    // bungeecord
    if (BedwarsRevol.getInstance().isBungee()) {
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

//    this.loadStatistics();
    this.loadLocalization(this.getConfig().getString("locale"));

//    this.checkUpdates();

    // Loading
    this.scoreboardManager = Bukkit.getScoreboardManager();
    this.gameManager.loadGames();
    this.startTimeListener();
//    this.startMetricsIfEnabled();

//    // holograms
//    if (this.isHologramsEnabled()) {
//      if (this.getServer().getPluginManager().isPluginEnabled("HologramAPI")) {
//        this.holographicInteraction = new HologramAPIInteraction();
//      } else if (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
//        this.holographicInteraction = new HolographicDisplaysInteraction();
//      }
//      this.holographicInteraction.loadHolograms();
//    }
  }

//  private void registerBugsnag() {
//    try {
//      this.bugsnag = new Bugsnag("c23593c1e2f40fc0da36564af1bd00c6");
//      this.bugsnag.setAppVersion(SupportData.getPluginVersion());
//      this.bugsnag.setProjectPackages("io.github.bedwarsrevolution");
//      this.bugsnag.setReleaseStage(SupportData.getPluginVersionType());
//    } catch (Exception e) {
//      this.getServer().getConsoleSender().sendMessage(
//          ChatWriterNew.pluginMessage(ChatColor.GOLD + "Couldn't register Bugsnag."));
//    }
//  }

  private void registerCommands() {
    BedwarsCommandExecutorNew executor = new BedwarsCommandExecutorNew(this);

    this.commands.add(new HelpCommand(this));
    this.commands.add(new SetSpawnerCommand(this));
    this.commands.add(new AddGameCommand(this));
    this.commands.add(new StartGameCommand(this));
    this.commands.add(new StopGameCommand(this));
    this.commands.add(new SetRegionCommand(this));
    this.commands.add(new SetBaseRegionCommand(this));
    this.commands.add(new SetTeamChestCommand(this));
    this.commands.add(new AddTeamCommand(this));
    this.commands.add(new SaveGameCommand(this));
    this.commands.add(new JoinGameCommand(this));
    this.commands.add(new SetSpawnCommand(this));
    this.commands.add(new SetLobbyCommand(this));
    this.commands.add(new LeaveGameCommand(this));
    this.commands.add(new SetTargetCommand(this));
    this.commands.add(new SetBedCommand(this));
    this.commands.add(new ReloadCommand(this));
    this.commands.add(new SetMainLobbyCommand(this));
    this.commands.add(new ListGamesCommand(this));
    this.commands.add(new RegionNameCommand(this));
    this.commands.add(new RemoveTeamCommand(this));
    this.commands.add(new RemoveGameCommand(this));
    this.commands.add(new ClearSpawnerCommand(this));
    this.commands.add(new GameTimeCommand(this));
    this.commands.add(new StatsCommand(this));
    this.commands.add(new SetMinPlayersCommand(this));
    this.commands.add(new SetGameBlockCommand(this));
    this.commands.add(new SetBuilderCommand(this));
    this.commands.add(new SetAutobalanceCommand(this));
    this.commands.add(new KickCommand(this));
    this.commands.add(new AddTeamJoinCommand(this));
    this.commands.add(new AddHoloCommand(this));
    this.commands.add(new RemoveHoloCommand(this));
    this.commands.add(new DebugPasteCommand(this));
    this.commands.add(new ItemsPasteCommand(this));

    this.getCommand("bw").setExecutor(executor);
  }

  private void registerConfigurationClasses() {
    ConfigurationSerialization.registerClass(ResourceSpawnerNew.class, "ResourceSpawner");
    ConfigurationSerialization.registerClass(TeamNew.class, "Team");
//    ConfigurationSerialization.registerClass(PlayerStatistic.class, "PlayerStatistic");
  }

  private void registerListeners() {
    new WeatherListenerNew();
    new BlockListenerNew();
    new PlayerListenerNew();
    if (!BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_8")) {
      new PlayerSwapHandItemsEventListenerNew();
    }
    if (BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_8")
        || BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_9") | BedwarsRevol
        .getInstance().getCurrentVersion().startsWith("v1_10") || BedwarsRevol.getInstance()
        .getCurrentVersion().startsWith("v1_11")) {
      new PlayerPickUpItemEventListenerNew();
    } else {
      new EntityPickupItemEventListenerNew();
    }
    new HangingListenerNew();
    new EntityListenerNew();
    new ServerListenerNew();
    new SignListenerNew();
    new ChunkListenerNew();
    new InvisibilityPotionListenerNew().registerInterceptor();
    new SoundListenerNew().registerInterceptor();

    if (this.isSpigot()) {
      new PlayerSpigotListenerNew();
    }

//    SpecialItem.loadSpecials();
    UpgradeRegistry.loadUpgrades();
  }

  public void reloadLocalization() {
    this.localization = new HashMap<>();
    this.loadLocalization(this.getConfig().getString("locale"));
  }

  public void saveConfiguration() {
    File file = new File(BedwarsRevol.getInstance().getDataFolder(), "config.yml");
    try {
      file.mkdirs();

      String data = this.getYamlDump((YamlConfiguration) this.getConfig());

      FileOutputStream stream = new FileOutputStream(file);
      OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");

      try {
        writer.write(data);
      } finally {
        writer.close();
        stream.close();
      }
    } catch (Exception ex) {
//      BedwarsRevol.getInstance().getBugsnag().notify(ex);
      ex.printStackTrace();
    }
  }

  public boolean spectationEnabled() {
    if (this.getConfig().contains("spectation-enabled")
        && this.getConfig().isBoolean("spectation-enabled")) {
      return this.getConfig().getBoolean("spectation-enabled");
    }
    return true;
  }

//  public void startMetricsIfEnabled() {
//    if (this.metricsEnabled()) {
//      new BStatsMetrics(this);
//      try {
//        McStatsMetrics mcStatsMetrics = new McStatsMetrics(this);
//        mcStatsMetrics.start();
//      } catch (Exception ex) {
//        BedwarsRevol.getInstance().getBugsnag().notify(ex);
//        this.getServer().getConsoleSender().sendMessage(ChatWriterNew
//            .pluginMessage(ChatColor.RED + "Metrics are enabled, but couldn't send data!"));
//      }
//    }
//  }

  private void startTimeListener() {
    this.timeTask = this.getServer().getScheduler().runTaskTimer(this, new Runnable() {

      @Override
      public void run() {
        for (GameContext ctx : BedwarsRevol.getInstance().getGameManager().getGamesContexts()) {
          ctx.getState().updateTime();
        }
      }
    }, (long) 5 * 20, (long) 5 * 20);
  }

  public boolean statisticsEnabled() {
    return this.getBooleanConfig("statistics.enabled", false);
  }

  private void stopTimeListener() {
    try {
      this.timeTask.cancel();
    } catch (Exception ex) {
      // Timer isn't running. Just ignore.
    }

    try {
      this.updateChecker.cancel();
    } catch (Exception ex) {
      // Timer isn't running. Just ignore.
    }
  }

  public boolean toMainLobby() {
    if (this.getConfig().contains("endgame.mainlobby-enabled")) {
      return this.getConfig().getBoolean("endgame.mainlobby-enabled");
    }

    return false;
  }

}
