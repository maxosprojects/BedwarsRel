package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.events.BedwarsPlayerSetNameEvent;
import io.github.bedwarsrel.game.BungeeGameCycle;
import io.github.bedwarsrel.game.DamageHolder;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameStateOld;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.listener.BaseListener;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrel.utils.ChatWriter;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;

public class PlayerListener extends BaseListener {

  private String getChatFormat(String format, Team team, boolean isSpectator, boolean all) {
    String form = format;

    if (all) {
      form = form.replace("$all$", BedwarsRevol._l("ingame.all") + ChatColor.RESET);
    }

    form = form.replace("$player$",
        ((!isSpectator && team != null) ? team.getChatColor() : "") + "%1$s" + ChatColor.RESET);
    form = form.replace("$msg$", "%2$s");

    if (isSpectator) {
      form = form.replace("$team$", BedwarsRevol._l("ingame.spectator"));
    } else if (team != null) {
      form = form.replace("$team$", team.getDisplayName() + ChatColor.RESET);
    }

    return ChatColor.translateAlternateColorCodes('&', form);
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

    if (game.getState() == GameStateOld.STOPPED) {
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

    if (game.getState() != GameStateOld.RUNNING && game.getState() == GameStateOld.WAITING) {
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
  public void onCommand(PlayerCommandPreprocessEvent pcpe) {
    Player player = pcpe.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() == GameStateOld.STOPPED) {
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
  public void onCraft(CraftItemEvent event) {
    Player player = (Player) event.getWhoClicked();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventCraft(ctx, event);
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    Player player = null;
    if (event.getEntity() instanceof Player) {
      player = (Player) event.getEntity();
    } else if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent eventByEntity = (EntityDamageByEntityEvent) event;
      if (eventByEntity.getDamager() == null || !(eventByEntity.getDamager() instanceof Player)) {
        return;
      }
      player = (Player) eventByEntity.getDamager();
    }
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    ctx.getState().onEventDamage(ctx, event);
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventDrop(ctx, event);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFly(PlayerToggleFlightEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventFly(ctx, event);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onHunger(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBowShot(EntityShootBowEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventBowShot(ctx, event);
  }

  @EventHandler
  public void onInteractEntity(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventInteractEntity(ctx, event);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getWhoClicked();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventInventoryClick(ctx, event);
  }

  /*
   * LOBBY & GAME
   */

  // TODO: figure out what to do with all this
//  @EventHandler(priority = EventPriority.HIGHEST)
//  public void onJoin(PlayerJoinEvent event) {
//    Player player = event.getPlayer();
//    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
//    if (ctx == null) {
//      return;
//    }
//
//    if (BedwarsRevol.getInstance().statisticsEnabled()) {
//      BedwarsRevol.getInstance().getPlayerStatisticManager().loadStatistic(player.getUniqueId());
//    }
//
//    if (BedwarsRevol.getInstance().isHologramsEnabled()
//        && BedwarsRevol.getInstance().getHolographicInteractor() != null && BedwarsRevol.getInstance()
//        .getHolographicInteractor().getType().equalsIgnoreCase("HolographicDisplays")) {
//      BedwarsRevol.getInstance().getHolographicInteractor().updateHolograms(player, 60L);
//    }
//
//    List<Game> games = BedwarsRevol.getInstance().getGameManager().getGames();
//    if (games.size() == 0) {
//      return;
//    }
//
//    if (!BedwarsRevol.getInstance().isBungee()) {
//      Game game = BedwarsRevol.getInstance().getGameManager().getGameByLocation(player.getLocation());
//      if (game != null) {
//        if (game.getMainLobby() != null) {
//          player.teleport(game.getMainLobby());
//        } else {
//          game.playerJoins(player);
//        }
//        return;
//      }
//    }
//
//    if (BedwarsRevol.getInstance().isBungee()) {
//      event.setJoinMessage(null);
//      final Game firstGame = games.get(0);
//
//      if (firstGame.getState() == GameStateOld.STOPPED && player.hasPermission("bw.setup")) {
//        return;
//      }
//
//      firstGame.playerJoins(player);
//
//    }
//  }


  /*
   * LOBBY
   */

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx != null) {
      ctx.getState().onEventPlayerInteract(ctx, event);
      return;
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }
    Block clicked = event.getClickedBlock();
    if (clicked == null) {
      return;
    }
    if (!(clicked.getState() instanceof Sign)) {
      return;
    }
    ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameBySignLocation(clicked.getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().playerJoins(ctx, player);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventPlayerRespawn(ctx, event);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventPlayerQuit(ctx, event);
  }

  @EventHandler
  public void onSleep(PlayerBedEnterEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventPlayerBedEnter(ctx, event);
  }

  @EventHandler
  public void onChangeWorld(PlayerChangedWorldEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventPlayerChangeWorld(ctx, event);

    if (!BedwarsRevol.getInstance().isHologramsEnabled()
        || BedwarsRevol.getInstance().getHolographicInteractor() == null) {
      return;
    }
    BedwarsRevol.getInstance().getHolographicInteractor().updateHolograms(event.getPlayer());
  }

  @EventHandler
  public void openInventory(InventoryOpenEvent event) {
    if (!(event.getPlayer() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventInventoryOpen(ctx, event);
  }

}
