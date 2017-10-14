package io.github.bedwarsrel.listener;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.events.BedwarsPlayerSetNameEvent;
import io.github.bedwarsrel.game.BungeeGameCycle;
import io.github.bedwarsrel.game.DamageHolder;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrel.utils.ChatWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerListener extends BaseListener {

  private Map<Player, BukkitTask> invisibilityTasks = new HashMap<>();

  private String getChatFormat(String format, Team team, boolean isSpectator, boolean all) {
    String form = format;

    if (all) {
      form = form.replace("$all$", BedwarsRel._l("ingame.all") + ChatColor.RESET);
    }

    form = form.replace("$player$",
        ((!isSpectator && team != null) ? team.getChatColor() : "") + "%1$s" + ChatColor.RESET);
    form = form.replace("$msg$", "%2$s");

    if (isSpectator) {
      form = form.replace("$team$", BedwarsRel._l("ingame.spectator"));
    } else if (team != null) {
      form = form.replace("$team$", team.getDisplayName() + ChatColor.RESET);
    }

    return ChatColor.translateAlternateColorCodes('&', form);
  }

  @SuppressWarnings("deprecation")
  private void inGameInteractEntity(PlayerInteractEntityEvent event, Game game, Player player) {
    List<Material> preventClickEggs = Arrays.asList(
        Material.MONSTER_EGG,
        Material.MONSTER_EGGS,
        Material.DRAGON_EGG);
    if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
      if (preventClickEggs.contains(player.getItemInHand().getType())) {
        event.setCancelled(true);
        return;
      }
    } else {
      PlayerInventory inv = player.getInventory();
      if (preventClickEggs.contains(inv.getItemInMainHand().getType())
          || preventClickEggs.contains(inv.getItemInOffHand().getType())) {
        event.setCancelled(true);
        return;
      }
    }

    if (event.getRightClicked() != null
        && !event.getRightClicked().getType().equals(EntityType.VILLAGER)) {
      List<EntityType> preventClickTypes =
          Arrays.asList(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND);

      if (preventClickTypes.contains(event.getRightClicked().getType())) {
        event.setCancelled(true);
      }
      return;
    }

    if (game.isSpectator(player)) {
      return;
    }

    if (!BedwarsRel.getInstance().getBooleanConfig("use-builtin-shop", true)) {
      return;
    }

    event.setCancelled(true);

    BedwarsOpenShopEvent openShopEvent =
        new BedwarsOpenShopEvent(game, player, game.getShopCategories(), event.getRightClicked());
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(openShopEvent);

    if (openShopEvent.isCancelled()) {
      return;
    }

    Shop shop = game.getShop(player);
    shop.resetCurrentCategory();
    shop.render();
  }

  /*
   * GAME
   */

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent ce) {
    if (ce.isCancelled()) {
      return;
    }

    Player player = ce.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      boolean seperateGameChat = BedwarsRel
          .getInstance().getBooleanConfig("seperate-game-chat", true);
      if (!seperateGameChat) {
        return;
      }

      Iterator<Player> recipients = ce.getRecipients().iterator();
      while (recipients.hasNext()) {
        Player recipient = recipients.next();
        Game recipientGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(recipient);
        if (recipientGame != null) {
          recipients.remove();
        }
      }
      return;
    }

    if (game.getState() == GameState.STOPPED) {
      return;
    }

    Team team = game.getPlayerTeam(player);
    String message = ce.getMessage();
    boolean isSpectator = game.isSpectator(player);

    String displayName = player.getDisplayName();
    String playerListName = player.getPlayerListName();

    if (BedwarsRel.getInstance().getBooleanConfig("overwrite-names", false)) {
      if (team == null) {
        displayName = ChatColor.stripColor(player.getName());

        playerListName = ChatColor.stripColor(player.getName());
      } else {
        displayName = team.getChatColor() + ChatColor.stripColor(player.getName());
        playerListName = team.getChatColor() + ChatColor.stripColor(player.getName());
      }

    }

    if (BedwarsRel.getInstance().getBooleanConfig("teamname-on-tab", false)) {
      if (team == null || isSpectator) {
        playerListName = ChatColor.stripColor(player.getDisplayName());
      } else {
        playerListName = team.getChatColor() + team.getName() + ChatColor.WHITE + " | "
            + team.getChatColor() + ChatColor.stripColor(player.getDisplayName());
      }
    }

    BedwarsPlayerSetNameEvent playerSetNameEvent =
        new BedwarsPlayerSetNameEvent(team, displayName, playerListName, player);
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(playerSetNameEvent);

    if (!playerSetNameEvent.isCancelled()) {
      player.setDisplayName(playerSetNameEvent.getDisplayName());
      player.setPlayerListName(playerSetNameEvent.getPlayerListName());
    }

    if (game.getState() != GameState.RUNNING && game.getState() == GameState.WAITING) {
      String format = null;
      if (team == null) {
        format = this.getChatFormat(
            BedwarsRel.getInstance().getStringConfig("lobby-chatformat", "$player$: $msg$"), null,
            false,
            true);
      } else {
        format = this.getChatFormat(
            BedwarsRel.getInstance()
                .getStringConfig("ingame-chatformat", "<$team$>$player$: $msg$"),
            team, false, true);
      }

      ce.setFormat(format);

      if (!BedwarsRel.getInstance().getBooleanConfig("seperate-game-chat", true)) {
        return;
      }

      Iterator<Player> recipiens = ce.getRecipients().iterator();
      while (recipiens.hasNext()) {
        Player recipient = recipiens.next();
        if (!game.isInGame(recipient)) {
          recipiens.remove();
        }
      }

      return;
    }

    @SuppressWarnings("unchecked")
    List<String> toAllPrefixList = (List<String>) BedwarsRel.getInstance().getConfig()
        .getList("chat-to-all-prefix", Arrays.asList("@"));

    String toAllPrefix = null;

    for (String oneToAllPrefix : toAllPrefixList) {
      if (message.trim().startsWith(oneToAllPrefix)) {
        toAllPrefix = oneToAllPrefix;
      }
    }

    if (toAllPrefix != null || isSpectator || (game.getCycle().isEndGameRunning()
        && BedwarsRel.getInstance().getBooleanConfig("global-chat-after-end", true))) {
      boolean seperateSpectatorChat =
          BedwarsRel.getInstance().getBooleanConfig("seperate-spectator-chat", false);

      message = message.trim();
      String format = null;
      if (!isSpectator && !(game.getCycle().isEndGameRunning()
          && BedwarsRel.getInstance().getBooleanConfig("global-chat-after-end", true))) {
        ce.setMessage(message.substring(toAllPrefix.length(), message.length()).trim());
        format = this
            .getChatFormat(BedwarsRel.getInstance().getStringConfig("ingame-chatformat-all",
                "[$all$] <$team$>$player$: $msg$"), team, false, true);
      } else {
        ce.setMessage(message);
        format = this.getChatFormat(
            BedwarsRel.getInstance()
                .getStringConfig("ingame-chatformat", "<$team$>$player$: $msg$"),
            team, isSpectator, true);
      }

      ce.setFormat(format);

      if (!BedwarsRel.getInstance().isBungee() || seperateSpectatorChat) {
        Iterator<Player> recipiens = ce.getRecipients().iterator();
        while (recipiens.hasNext()) {
          Player recipient = recipiens.next();
          if (!game.isInGame(recipient)) {
            recipiens.remove();
            continue;
          }

          if (!seperateSpectatorChat || (game.getCycle().isEndGameRunning()
              && BedwarsRel.getInstance().getBooleanConfig("global-chat-after-end", true))) {
            continue;
          }

          if (isSpectator && !game.isSpectator(recipient)) {
            recipiens.remove();
          } else if (!isSpectator && game.isSpectator(recipient)) {
            recipiens.remove();
          }
        }
      }
    } else {
      message = message.trim();
      ce.setMessage(message);
      ce.setFormat(this.getChatFormat(
          BedwarsRel.getInstance().getStringConfig("ingame-chatformat", "<$team$>$player$: $msg$"),
          team,
          false, false));

      Iterator<Player> recipiens = ce.getRecipients().iterator();
      while (recipiens.hasNext()) {
        Player recipient = recipiens.next();
        if (!game.isInGame(recipient) || !team.isInTeam(recipient)) {
          recipiens.remove();
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onConsumeEvent(PlayerItemConsumeEvent event) {
    final Player player = event.getPlayer();
    ItemStack item = event.getItem();
    if (item.getType() == Material.POTION) {
      PotionMeta meta = (PotionMeta) item.getItemMeta();
      if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
        // Default 30 sec
        int duration = 600;
        for (PotionEffect effect : meta.getCustomEffects()) {
          if (PotionEffectType.INVISIBILITY.equals(effect.getType())) {
            duration = effect.getDuration();
          }
        }
        this.makePlayerInvisible(player, true);
        if (invisibilityTasks.containsKey(player)) {
          invisibilityTasks.get(player).cancel();
        }
        invisibilityTasks.put(player, new BukkitRunnable() {
          @Override
          public void run() {
            invisibilityTasks.remove(player);
            PlayerListener.this.makePlayerInvisible(player, false);
          }
        }.runTaskLater(BedwarsRel.getInstance(), duration));
      }
      new BukkitRunnable() {
        public void run() {
          player.getInventory().remove(Material.GLASS_BOTTLE);
        }
      }.runTaskLater(BedwarsRel.getInstance(), 1L);
    }
  }

  /**
   * Makes given player entirely invisible (including gear, bubbles etc.) to
   * enemy teams.
   * @param player player to hide
   * @param hide   true to hide, false to show again
   */
  private void makePlayerInvisible(Player player, boolean hide) {
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
    if (game != null && !game.isSpectator(player)) {
      Team playerTeam = game.getPlayerTeam(player);
      for (Team team : game.getTeams().values()) {
        if (team != playerTeam) {
          for (Player playerInTeam : team.getPlayers()) {
            if (hide) {
              playerInTeam.hidePlayer(player);
            } else {
              playerInTeam.showPlayer(player);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommand(PlayerCommandPreprocessEvent pcpe) {
    Player player = pcpe.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() == GameState.STOPPED) {
      return;
    }

    String message = pcpe.getMessage();
    if (!message.startsWith("/bw")) {

      for (String allowed : BedwarsRel.getInstance().getAllowedCommands()) {
        if (!allowed.startsWith("/")) {
          allowed = "/" + allowed;
        }

        if (message.startsWith(allowed.trim())) {
          return;
        }
      }

      if (player.hasPermission("bw.cmd")) {
        return;
      }

      pcpe.setCancelled(true);
      return;
    }
  }

  @EventHandler
  public void onCraft(CraftItemEvent cie) {
    Player player = (Player) cie.getWhoClicked();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() == GameState.STOPPED) {
      return;
    }

    if (BedwarsRel.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }

    cie.setCancelled(true);
  }

  @EventHandler
  public void onDamage(EntityDamageEvent ede) {
    if (!(ede.getEntity() instanceof Player)) {
      if (!(ede instanceof EntityDamageByEntityEvent)) {
        return;
      }

      EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) ede;
      if (edbee.getDamager() == null || !(edbee.getDamager() instanceof Player)) {
        return;
      }

      Player player = (Player) edbee.getDamager();
      Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

      if (game == null) {
        return;
      }

      if (game.getState() == GameState.WAITING) {
        ede.setCancelled(true);
      }

      return;
    }

    Player p = (Player) ede.getEntity();
    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
    if (g == null) {
      return;
    }

    if (g.getState() == GameState.STOPPED) {
      return;
    }

    if (g.getState() == GameState.RUNNING) {
      if (g.isSpectator(p)) {
        ede.setCancelled(true);
        return;
      }

      if (g.isProtected(p) && ede.getCause() != DamageCause.VOID) {
        ede.setCancelled(true);
        return;
      }

      if (BedwarsRel.getInstance().getBooleanConfig("die-on-void", false)
          && ede.getCause() == DamageCause.VOID) {
        ede.setCancelled(true);
//        p.setHealth(0);
        this.handleDeath(p, g);
        return;
      }

      Player damager = null;
      if (ede instanceof EntityDamageByEntityEvent) {
        EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) ede;

        if (edbee.getDamager() instanceof Player) {
          damager = (Player) edbee.getDamager();
        } else if (edbee.getDamager().getType().equals(EntityType.ARROW)) {
          Arrow arrow = (Arrow) edbee.getDamager();
          if (arrow.getShooter() instanceof Player) {
            damager = (Player) arrow.getShooter();
          }
        } else if (edbee.getDamager().getType().equals(EntityType.PRIMED_TNT)) {
          TNTPrimed tnt = (TNTPrimed) edbee.getDamager();
          damager = (Player) tnt.getSource();
        }
      }

      if (damager != null) {
        if (g.isSpectator(damager)) {
          ede.setCancelled(true);
          return;
        } else {
          g.setPlayerDamager(p, damager);
        }
      }

      if (g.isWaitingRespawn(p) || g.getCycle().isEndGameRunning()) {
        ede.setCancelled(true);
        return;
      }

      if (ede.getDamage() >= p.getHealth()) {
        ede.setCancelled(true);
        this.handleDeath(p, g);
        return;
      }

    } else if (g.getState() == GameState.WAITING
        && ede.getCause() == EntityDamageEvent.DamageCause.VOID) {
      p.teleport(g.getLobby());
    }
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent die) {
    Player p = die.getPlayer();
    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
    if (g == null) {
      return;
    }

    if (g.getState() != GameState.WAITING) {
      if (g.isSpectator(p)) {
        die.setCancelled(true);
      }

      return;
    }

    die.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFly(PlayerToggleFlightEvent tfe) {

    System.out.println("PlayerToggleFlightEvent");

    Player p = tfe.getPlayer();

    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
    if (g == null) {
      return;
    }

    if (g.getState() == GameState.STOPPED) {
      return;
    }

    if (g.getState() == GameState.RUNNING && g.isSpectator(p)) {
      tfe.setCancelled(false);
      return;
    }

    tfe.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHunger(FoodLevelChangeEvent flce) {
    if (!(flce.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) flce.getEntity();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() == GameState.RUNNING) {
      if (game.isSpectator(player) || game.getCycle().isEndGameRunning() || !game.isHungerEnabled()) {
        flce.setCancelled(true);
        return;
      }

      flce.setCancelled(false);
      return;
    }

    flce.setCancelled(true);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void onIngameInventoryClick(InventoryClickEvent event, Player player, Game game) {
    // Prevent armor changes
    if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
      event.setCancelled(true);
      return;
    }
    // If open inventory isn't shop
    if (!event.getInventory().getName().equals(BedwarsRel._l(player, "ingame.shop.name"))) {
      if (game.isSpectator(player)
          || (game.getCycle() instanceof BungeeGameCycle && game.getCycle().isEndGameRunning()
          && BedwarsRel.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))) {

        ItemStack clickedStack = event.getCurrentItem();
        if (clickedStack == null) {
          return;
        }

        if (event.getInventory().getName().equals(BedwarsRel._l(player, "ingame.spectator"))) {
          event.setCancelled(true);
          if (!clickedStack.getType().equals(Material.SKULL_ITEM)) {
            return;
          }

          SkullMeta meta = (SkullMeta) clickedStack.getItemMeta();
          Player pl = BedwarsRel.getInstance().getServer().getPlayer(meta.getOwner());
          if (pl == null) {
            return;
          }

          if (!game.isInGame(pl)) {
            return;
          }

          player.teleport(pl);
          player.closeInventory();
          return;
        }

        Material clickedMat = event.getCurrentItem().getType();
        if (clickedMat.equals(Material.SLIME_BALL)) {
          game.playerLeave(player, false);
        }

        if (clickedMat.equals(Material.COMPASS)) {
          game.openSpectatorCompass(player);
        }
      }
      return;
    }

    event.setCancelled(true);
    ItemStack clickedStack = event.getCurrentItem();

    if (clickedStack == null) {
      return;
    }

    game.getShop(player).handleClick(event);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBowShot(EntityShootBowEvent evt) {
    if (!(evt.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) evt.getEntity();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null || game.getState() != GameState.RUNNING) {
      return;
    }

    Team team = game.getPlayerTeam(player);
    if (team == null) {
      return;
    }

    ItemStack bow = evt.getBow();
    // Take away one arrow from player if shot from a bow with "infinity" enchantment
    if (bow.hasItemMeta() && bow.getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)) {
      Inventory inv = player.getInventory();
      int slot = inv.first(Material.ARROW);
      ItemStack stack = inv.getItem(slot);
      stack.setAmount(stack.getAmount() - 1);
    }
  }

  @EventHandler
  public void onInteractEntity(PlayerInteractEntityEvent iee) {

    System.out.println("PlayerInteractEntityEvent");

    Player p = iee.getPlayer();
    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
    if (g == null) {
      return;
    }

    if (g.getState() == GameState.WAITING) {
      iee.setCancelled(true);
      return;
    }

    if (g.getState() == GameState.RUNNING) {
      this.inGameInteractEntity(iee, g, p);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {

    System.out.println("InventoryClickEvent");

    Player player = (Player) event.getWhoClicked();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() == GameState.WAITING) {
      this.onLobbyInventoryClick(event, player, game);
    }

    if (game.getState() == GameState.RUNNING) {
      this.onIngameInventoryClick(event, player, game);
    }
  }

  /*
   * LOBBY & GAME
   */

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent je) {

    System.out.println("PlayerJoinEvent");


    final Player player = je.getPlayer();

    if (BedwarsRel.getInstance().statisticsEnabled()) {
      BedwarsRel.getInstance().getPlayerStatisticManager().loadStatistic(player.getUniqueId());
    }

    if (BedwarsRel.getInstance().isHologramsEnabled()
        && BedwarsRel.getInstance().getHolographicInteractor() != null && BedwarsRel.getInstance()
        .getHolographicInteractor().getType().equalsIgnoreCase("HolographicDisplays")) {
      BedwarsRel.getInstance().getHolographicInteractor().updateHolograms(player, 60L);
    }

    ArrayList<Game> games = BedwarsRel.getInstance().getGameManager().getGames();
    if (games.size() == 0) {
      return;
    }

    if (!BedwarsRel.getInstance().isBungee()) {
      Game game = BedwarsRel.getInstance().getGameManager().getGameByLocation(player.getLocation());


      System.out.println("Checkpoint 31");


      if (game != null) {
        if (game.getMainLobby() != null) {
          player.teleport(game.getMainLobby());
        } else {
          game.playerJoins(player);
        }
        return;
      }
    }

    if (BedwarsRel.getInstance().isBungee()) {
      je.setJoinMessage(null);
      final Game firstGame = games.get(0);

      if (firstGame.getState() == GameState.STOPPED && player.hasPermission("bw.setup")) {
        return;
      }

      firstGame.playerJoins(player);

    }
  }

  private void onLobbyInventoryClick(InventoryClickEvent ice, Player player, Game game) {
    Inventory inv = ice.getInventory();
    ItemStack clickedStack = ice.getCurrentItem();

    if (!inv.getTitle().equals(BedwarsRel._l(player, "lobby.chooseteam"))) {
      ice.setCancelled(true);
      return;
    }

    if (clickedStack == null) {
      ice.setCancelled(true);
      return;
    }

    if (clickedStack.getType() != Material.WOOL) {
      ice.setCancelled(true);
      return;
    }

    ice.setCancelled(true);
    Wool wool = (Wool) clickedStack.getData();
    Team team = game.getTeamByDyeColor(wool.getColor());
    if (team == null) {
      return;
    }

    game.playerJoinTeam(player, team);
    player.closeInventory();
  }

  private void handleDeath(Player player, Game game) {

//    if (!BedwarsRel.getInstance().getBooleanConfig("player-drops", false)) {
//      pde.getDrops().clear();
//    }

//    pde.setKeepInventory(
//            BedwarsRel.getInstance().getBooleanConfig("keep-inventory-on-death", false));

    DamageHolder damager = game.getPlayerDamager(player);
    game.getCycle().onPlayerDies(player, (damager != null && damager.wasCausedRecently()) ? damager.getDamager() : null);

    game.onPlayerVirtuallyDies(player);
  }

  /*
   * LOBBY
   */

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent pie) {

    System.out.println("PlayerInteractEvent");

    Player player = pie.getPlayer();
    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (g == null) {
      if (pie.getAction() != Action.RIGHT_CLICK_BLOCK
          && pie.getAction() != Action.RIGHT_CLICK_AIR) {
        return;
      }

      Block clicked = pie.getClickedBlock();

      if (clicked == null) {
        return;
      }

      if (!(clicked.getState() instanceof Sign)) {
        return;
      }

      Game game = BedwarsRel.getInstance().getGameManager()
          .getGameBySignLocation(clicked.getLocation());
      if (game == null) {
        return;
      }

      if (game.playerJoins(player)) {
        player.sendMessage(
            ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(player, "success.joined")));
      }
      System.out.println("Checkpoint 12");
      return;
    }

    if (g.getState() == GameState.STOPPED) {
      return;
    }

    Material interactingMaterial = pie.getMaterial();
    Block clickedBlock = pie.getClickedBlock();

    if (g.getState() == GameState.RUNNING) {
      if (pie.getAction() == Action.PHYSICAL && clickedBlock != null
          && (clickedBlock.getType() == Material.WHEAT
          || clickedBlock.getType() == Material.SOIL)) {
        pie.setCancelled(true);
        return;
      }

      if (pie.getAction() != Action.RIGHT_CLICK_BLOCK
          && pie.getAction() != Action.RIGHT_CLICK_AIR) {
        return;
      }

      if (clickedBlock != null && clickedBlock.getType() == Material.LEVER && !g.isSpectator(player)
          && pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
        if (!g.getRegion().isPlacedUnbreakableBlock(clickedBlock)) {
          g.getRegion().addPlacedUnbreakableBlock(clickedBlock, clickedBlock.getState());
        }
        return;
      }

      if (g.isSpectator(player)
          || (g.getCycle() instanceof BungeeGameCycle && g.getCycle().isEndGameRunning()
          && BedwarsRel.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))) {
        if (interactingMaterial == Material.SLIME_BALL) {
          g.playerLeave(player, false);
          return;
        }

        if (interactingMaterial == Material.COMPASS) {
          g.openSpectatorCompass(player);
          pie.setCancelled(true);
          return;
        }
      }

      // Spectators want to block
      if (clickedBlock != null) {
        try {
          GameMode.valueOf("SPECTATOR");
        } catch (Exception ex) {
          BedwarsRel.getInstance().getBugsnag().notify(ex);
          for (Player p : g.getFreePlayers()) {
            if (!g.getRegion().isInRegion(p.getLocation())) {
              continue;
            }

            if (pie.getClickedBlock().getLocation().distance(p.getLocation()) < 2) {
              Location oldLocation = p.getLocation();
              if (oldLocation.getY() >= pie.getClickedBlock().getLocation().getY()) {
                oldLocation.setY(oldLocation.getY() + 2);
              } else {
                oldLocation.setY(oldLocation.getY() - 2);
              }

              System.out.println("Checkpoint 30");

              p.teleport(oldLocation);
            }
          }
        }
      }

      if (clickedBlock != null
              && (clickedBlock.getType() == Material.ENDER_CHEST
                  || clickedBlock.getType() == Material.CHEST)
              && !g.isSpectator(player)) {
        pie.setCancelled(true);

        Block chest = pie.getClickedBlock();
        Team chestTeam = g.getTeamOfEnderChest(chest);
        Team playerTeam = g.getPlayerTeam(player);

        if (chestTeam == null) {
          return;
        }

        if (chestTeam.equals(playerTeam)) {
          player.openInventory(chestTeam.getInventory());
        } else {
          player.sendMessage(
              ChatWriter
                  .pluginMessage(ChatColor.RED + BedwarsRel._l(player, "ingame.noturteamchest")));
        }

        return;
      }

      return;
    } else if (g.getState() == GameState.WAITING) {
      if (interactingMaterial == null) {
        pie.setCancelled(true);
        return;
      }

      if (pie.getAction() == Action.PHYSICAL) {
        if (clickedBlock != null && (clickedBlock.getType() == Material.WHEAT
            || clickedBlock.getType() == Material.SOIL)) {
          pie.setCancelled(true);
          return;
        }
      }

      if (pie.getAction() != Action.RIGHT_CLICK_BLOCK
          && pie.getAction() != Action.RIGHT_CLICK_AIR) {
        return;
      }

      switch (interactingMaterial) {
        case BED:
          pie.setCancelled(true);
          if (!g.isAutobalanceEnabled()) {
            g.getPlayerStorage(player).openTeamSelection(g);
          }

          break;
        case DIAMOND:
          pie.setCancelled(true);
          if (player.isOp() || player.hasPermission("bw.setup")) {
            g.start(player);
          } else if (player.hasPermission("bw.vip.forcestart")) {
            if (g.isStartable()) {
              g.start(player);
            } else {
              if (!g.hasEnoughPlayers()) {
                player.sendMessage(ChatWriter.pluginMessage(
                    ChatColor.RED + BedwarsRel._l(player, "lobby.cancelstart.not_enough_players")));
              } else if (!g.hasEnoughTeams()) {
                player.sendMessage(ChatWriter
                    .pluginMessage(
                        ChatColor.RED + BedwarsRel
                            ._l(player, "lobby.cancelstart.not_enough_teams")));
              }
            }
          }
          break;
        case EMERALD:
          pie.setCancelled(true);
          if ((player.isOp() || player.hasPermission("bw.setup")
              || player.hasPermission("bw.vip.reducecountdown"))
              && g.getGameLobbyCountdown().getCounter() > g.getGameLobbyCountdown()
              .getLobbytimeWhenFull()) {
            g.getGameLobbyCountdown().setCounter(g.getGameLobbyCountdown().getLobbytimeWhenFull());
          }
          break;
        case SLIME_BALL:
          pie.setCancelled(true);
          g.playerLeave(player, false);
          break;
        case LEATHER_CHESTPLATE:
          pie.setCancelled(true);
          player.updateInventory();
          break;
        default:
          break;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerRespawn(PlayerRespawnEvent pre) {

    System.out.println("PlayerRespawnEvent");

    Player p = pre.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);

    if (game == null) {
      return;
    }

    if (game.getState() == GameState.RUNNING) {
      game.getCycle().onPlayerRespawn(pre, p);
      return;
    }

    if (game.getState() == GameState.WAITING) {
      pre.setRespawnLocation(game.getLobby());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent pqe) {

    System.out.println("PlayerQuitEvent");

    Player player = pqe.getPlayer();

    if (BedwarsRel.getInstance().isBungee()) {
      pqe.setQuitMessage(null);
    }

    // Remove holographs
    if (BedwarsRel.getInstance().isHologramsEnabled()
        && BedwarsRel.getInstance().getHolographicInteractor() != null && BedwarsRel.getInstance()
        .getHolographicInteractor().getType().equalsIgnoreCase("HolographicDisplays")) {
      BedwarsRel.getInstance().getHolographicInteractor().unloadAllHolograms(player);
    }

    if (BedwarsRel.getInstance().statisticsEnabled()) {
      BedwarsRel.getInstance().getPlayerStatisticManager().unloadStatistic(player);
    }

    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (g == null) {
      return;
    }

    g.playerLeave(player, false);
  }

  @EventHandler
  public void onSleep(PlayerBedEnterEvent bee) {

    Player p = bee.getPlayer();

    Game g = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
    if (g == null) {
      return;
    }

    if (g.getState() == GameState.STOPPED) {
      return;
    }

    bee.setCancelled(true);
  }

  @EventHandler
  public void onSwitchWorld(PlayerChangedWorldEvent change) {

    System.out.println("PlayerChangedWorldEvent");

    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(change.getPlayer());
    if (game != null) {

      System.out.println("Checkpoint 20");

      if (game.getState() == GameState.RUNNING) {
        if (!game.getCycle().isEndGameRunning()) {
          if (!game.getPlayerFlags(change.getPlayer()).isTeleporting()) {
            game.playerLeave(change.getPlayer(), false);
          }
        }
      } else if (game.getState() == GameState.WAITING) {
        if (!game.getPlayerFlags(change.getPlayer()).isTeleporting()) {

          System.out.println("Checkpoint 40");

          game.playerLeave(change.getPlayer(), false);
        }
      }
    }


    System.out.println("Checkpoint 21");

    if (!BedwarsRel.getInstance().isHologramsEnabled()
        || BedwarsRel.getInstance().getHolographicInteractor() == null) {
      return;
    }


    System.out.println("Checkpoint 22");

    BedwarsRel.getInstance().getHolographicInteractor().updateHolograms(change.getPlayer());

    System.out.println("Checkpoint 23");

  }

  @EventHandler
  public void openInventory(InventoryOpenEvent ioe) {
    if (!(ioe.getPlayer() instanceof Player)) {
      return;
    }

    Player player = (Player) ioe.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    if (ioe.getInventory().getType() == InventoryType.ENCHANTING
        || ioe.getInventory().getType() == InventoryType.BREWING
        || (ioe.getInventory().getType() == InventoryType.CRAFTING
        && !BedwarsRel.getInstance().getBooleanConfig("allow-crafting", false))) {
      ioe.setCancelled(true);
      return;
    } else if (ioe.getInventory().getType() == InventoryType.CRAFTING
        && BedwarsRel.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }

    if (game.isSpectator(player)) {
      if (ioe.getInventory().getName().equals(BedwarsRel._l(player, "ingame.spectator"))) {
        return;
      }

      ioe.setCancelled(true);
    }

    if (ioe.getInventory().getHolder() == null) {
      return;
    }

    if (game.getRegion().getInventories().contains(ioe.getInventory())) {
      return;
    }

    game.getRegion().addInventory(ioe.getInventory());
  }

}
