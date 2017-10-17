package io.github.bedwarsrevolution.game.statemachine.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.GameLobbyCountdownNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by {maxos} 2017
 */
public class GameStateWaiting implements GameState {

  private GameLobbyCountdownNew lobbyCountdown;

  @Override
  public void onEventCraft(GameContext ctx, CraftItemEvent event) {
    if (BedwarsRevol.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }
    event.setCancelled(true);
  }

  @Override
  public void onEventDamage(GameContext ctx, EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventDrop(GameContext ctx, PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventFly(GameContext ctx, PlayerToggleFlightEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventBowShot(GameContext ctx, EntityShootBowEvent event) {
  }

  @Override
  public void onEventInteractEntity(GameContext ctx, PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventInventoryClick(GameContext ctx, InventoryClickEvent event) {
    // Prevent armor changes
    if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
      event.setCancelled(true);
      return;
    }
    PlayerContext playerCtx = ctx.getPlayerContext((Player) event.getWhoClicked());
    playerCtx.getState().onInventoryClick(playerCtx, event);
  }

  @Override
  public void onEventPlayerInteract(GameContext ctx, PlayerInteractEvent event) {
    Material interactingMaterial = event.getMaterial();
    Block clickedBlock = event.getClickedBlock();
    if (interactingMaterial == null) {
      event.setCancelled(true);
      return;
    }

    if (event.getAction() == Action.PHYSICAL) {
      if (clickedBlock != null && (clickedBlock.getType() == Material.WHEAT
          || clickedBlock.getType() == Material.SOIL)) {
        event.setCancelled(true);
        return;
      }
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }

    Player player = event.getPlayer();
    switch (interactingMaterial) {
//      case BED:
//        pie.setCancelled(true);
//        if (!g.isAutobalanceEnabled()) {
//          g.getPlayerStorage(player).openTeamSelection(g);
//        }
//        break;
      case DIAMOND:
        event.setCancelled(true);
        this.forceStart(ctx, player);
        break;
//      case EMERALD:
//        pie.setCancelled(true);
//        if ((player.isOp() || player.hasPermission("bw.setup")
//            || player.hasPermission("bw.vip.reducecountdown"))
//            && g.getGameLobbyCountdown().getCounter() > g.getGameLobbyCountdown()
//            .getLobbytimeWhenFull()) {
//          g.getGameLobbyCountdown().setCounter(g.getGameLobbyCountdown().getLobbytimeWhenFull());
//        }
//        break;
      case SLIME_BALL:
        event.setCancelled(true);
        this.playerLeaves(ctx, ctx.getPlayerContext(player), false);
        break;
//      case LEATHER_CHESTPLATE:
//        event.setCancelled(true);
//        player.updateInventory();
//        break;
      default:
        break;
    }
  }

  private void forceStart(GameContext ctx, Player player) {
    boolean enoughPlayers = this.isEnoughPlayers(ctx);
    if (player.isOp() || player.hasPermission("bw.setup")) {
      ctx.setState(new GameStateRunning());
    } else if (player.hasPermission("bw.vip.forcestart")) {
      if (enoughPlayers && this.isEnoughTeams(ctx)) {
        ctx.setState(new GameStateRunning());
      } else if (!enoughPlayers) {
          player.sendMessage(ChatWriterNew.pluginMessage(
              ChatColor.RED + BedwarsRevol._l(player, "lobby.cancelstart.not_enough_players")));
      } else {
          player.sendMessage(ChatWriterNew
              .pluginMessage(
                  ChatColor.RED + BedwarsRevol
                      ._l(player, "lobby.cancelstart.not_enough_teams")));
        }
      }
  }

  @Override
  public void onEventPlayerRespawn(GameContext ctx, PlayerRespawnEvent event) {
    event.setRespawnLocation(ctx.getLobby());
  }

  @Override
  public void onEventPlayerQuit(GameContext ctx, PlayerQuitEvent event) {

  }

  @Override
  public void onEventPlayerBedEnter(GameContext ctx, PlayerBedEnterEvent event) {

  }

  @Override
  public void onEventPlayerChangeWorld(GameContext ctx, PlayerChangedWorldEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    if (!playerCtx.isTeleporting()) {
      this.playerLeaves(ctx, playerCtx, false);
    }
  }

  @Override
  public void onEventInventoryOpen(GameContext ctx, InventoryOpenEvent event) {

  }

  @Override
  public void playerJoins(GameContext ctx, Player player) {
    if (ctx.isFull()) {
      if (player.hasPermission("bw.vip.joinfull")) {
        List<PlayerContext> nonVip = ctx.getNonVipPlayers();
        if (nonVip.size() == 0) {
          player.sendMessage(ChatWriterNew.pluginMessage(
              ChatColor.RED + BedwarsRevol._l(player, "lobby.gamefullpremium")));
          return;
        }
        PlayerContext kickNonVip = null;
        if (nonVip.size() == 1) {
          kickNonVip = nonVip.get(0);
        } else {
          kickNonVip = nonVip.get(Utils.randInt(0, nonVip.size() - 1));
        }
        Player kickedPlayer = kickNonVip.getPlayer();
        kickedPlayer.sendMessage(ChatWriterNew.pluginMessage(
            ChatColor.RED + BedwarsRevol._l(kickedPlayer, "lobby.kickedbyvip")));
        this.playerLeaves(ctx, kickNonVip, false);
      } else {
        player.sendMessage(ChatWriterNew.pluginMessage(
            ChatColor.RED + BedwarsRevol._l(player, "lobby.gamefull")));
        return;
      }
    }

    BedwarsRevol.getInstance().getGameManager().playerJoined(player, ctx);
    PlayerContext playerCtx = ctx.addPlayer(player);

//    BedwarsPlayerJoinEvent joiningEvent = new BedwarsPlayerJoinEvent(this, p);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(joiningEvent);
//    if (joiningEvent.isCancelled()) {
//      if (joiningEvent.getKickOnCancel()) {
//        new BukkitRunnable() {
//          @Override
//          public void run() {
//            if (Game.this.getCycle() instanceof BungeeGameCycle) {
//              ((BungeeGameCycle) Game.this.getCycle())
//                  .bungeeSendToServer(BedwarsRel.getInstance().getBungeeHub(), p, true);
//            }
//          }
//        }.runTaskLater(BedwarsRel.getInstance(), 5L);
//      }
//      return false;
//    }

//    if (BedwarsRevol.getInstance().statisticsEnabled()) {
//      BedwarsRevol.getInstance().getPlayerStatisticManager().getStatistic(player);
//    }

    // TODO: why is that?
//    new BukkitRunnable() {
//
//      @Override
//      public void run() {
//        for (Player playerInGame : Game.this.getPlayers()) {
//          playerInGame.hidePlayer(p);
//          p.hidePlayer(playerInGame);
//        }
//      }
//
//    }.runTaskLater(BedwarsRel.getInstance(), 5L);

    playerCtx.storeInventory();
    playerCtx.clear(false);

//      if (!BedwarsRel.getInstance().isBungee()) {
//        final Location location = this.getPlayerTeleportLocation(p);
//        if (!p.getLocation().equals(location)) {
//          this.setTeleportingIfWorldChange(p, location);
//          // TODO: Figure out what was this insane logic here for (not bungee outer if and
//          // bungee inner if)
//          if (BedwarsRel.getInstance().isBungee()) {
//            new BukkitRunnable() {
//
//              @Override
//              public void run() {
//                p.teleport(location);
//              }
//
//            }.runTaskLater(BedwarsRel.getInstance(), 10L);
//          } else {
//            p.teleport(location);
//          }
//        }
//      }

    this.setLobbyInventory(ctx, playerCtx);

//      new BukkitRunnable() {
//
//        @Override
//        public void run() {
//          Game.this.setPlayerGameMode(p);
//          Game.this.setPlayerVisibility(p);
//        }
//
//      }.runTaskLater(BedwarsRel.getInstance(), 15L);

    for (PlayerContext aPlayerCtx : ctx.getPlayers()) {
      Player aPlayer = aPlayerCtx.getPlayer();
      if (aPlayer.isOnline()) {
        aPlayer.sendMessage(
            ChatWriterNew
                .pluginMessage(ChatColor.GREEN + BedwarsRevol._l(aPlayer, "lobby.playerjoin",
                ImmutableMap.of("player", player.getDisplayName() + ChatColor.GREEN))));
      }
    }

    TeamNew team = this.getLowestTeam(ctx);
    if (team == null) {
      throw new IllegalStateException("No teams defined or no players joined");
    }
    team.addPlayer(player);
//      if (!this.isAutobalanceEnabled()) {
//        this.freePlayers.add(p);
//      } else {
//        Team team = this.getLowestTeam();
//        team.addPlayer(p);
//      }

//      if (BedwarsRel.getInstance().getBooleanConfig("store-game-records", true)) {
//        this.displayRecord(p);
//      }

//    BedwarsPlayerJoinedEvent joinEvent = new BedwarsPlayerJoinedEvent(this, null, p);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(joinEvent);

    this.updateScoreboard(ctx);
    ctx.updateSigns();

    player.sendMessage(ChatWriterNew.pluginMessage(
        ChatColor.GREEN + BedwarsRevol._l(player, "success.joined")));

    boolean enoughPlayers = this.isEnoughPlayers(ctx);
    if (enoughPlayers && this.isEnoughTeams(ctx)) {
      if (this.lobbyCountdown == null) {
        this.lobbyCountdown = new GameLobbyCountdownNew(ctx, this);
        this.lobbyCountdown.runTaskTimer(BedwarsRevol.getInstance(), 20L, 20L);
      }
    } else if (!enoughPlayers) {
      Collection<PlayerContext> players = ctx.getPlayers();
      int playersNeeded = ctx.getMinPlayers() - players.size();
      for (PlayerContext aPlayerCtx : players) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(ChatWriterNew.pluginMessage(
              ChatColor.GREEN + BedwarsRevol._l(aPlayer, "lobby.moreplayersneeded", "count",
                  ImmutableMap.of("count", String.valueOf(playersNeeded)))));
        }
      }
    } else {
      for (PlayerContext aPlayerCtx : ctx.getPlayers()) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(ChatWriterNew.pluginMessage(
              ChatColor.RED + BedwarsRevol._l(aPlayer, "lobby.moreteamsneeded")));
        }
      }
    }
  }

  private boolean isEnoughTeams(GameContext ctx) {
    for (Collection<PlayerContext> players : ctx.getTeamsToPlayers().values()) {
      if (players.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private boolean isEnoughPlayers(GameContext ctx) {
    return ctx.getPlayers().size() >= ctx.getMinPlayers();
  }

  private TeamNew getLowestTeam(GameContext ctx) {
    int lowestNum = Integer.MAX_VALUE;
    TeamNew lowestTeam = null;
    for (Entry<TeamNew, Collection<PlayerContext>> entry : ctx.getTeamsToPlayers().entrySet()) {
      int size = entry.getValue().size();
      if (size < lowestNum) {
        lowestNum = size;
        lowestTeam = entry.getKey();
      }
    }
    return lowestTeam;
  }

  private void setLobbyInventory(GameContext ctx, PlayerContext playerCtx) {
    Player player = playerCtx.getPlayer();
    ItemMeta im = null;
//    // choose team only when autobalance is disabled
//    if (!game.isAutobalanceEnabled()) {
//      // Choose team (Wool)
//      ItemStack teamSelection = new ItemStack(Material.BED, 1);
//      im = teamSelection.getItemMeta();
//      im.setDisplayName(BedwarsRel._l(this.player, "lobby.chooseteam"));
//      teamSelection.setItemMeta(im);
//      this.player.getInventory().addItem(teamSelection);
//    }

    // Leave game (Slimeball)
    ItemStack leaveGame = new ItemStack(Material.SLIME_BALL, 1);
    im = leaveGame.getItemMeta();
    im.setDisplayName(BedwarsRevol._l(player, "lobby.leavegame"));
    leaveGame.setItemMeta(im);
    player.getInventory().setItem(8, leaveGame);

    if ((player.hasPermission("bw.setup")
        || player.isOp()
        || player.hasPermission("bw.vip.forcestart"))) {
      this.addGameStartItem(ctx, playerCtx);
    }

//    if (game.getGameLobbyCountdown() != null
//        && game.getGameLobbyCountdown().getLobbytime() > game.getGameLobbyCountdown()
//        .getLobbytimeWhenFull()
//        && (this.player.hasPermission("bw.setup") || this.player.isOp()
//        || this.player.hasPermission("bw.vip.reducecountdown"))) {
//      this.addReduceCountdownItem();
//    }

    player.updateInventory();
  }

  private void addGameStartItem(GameContext ctx, PlayerContext playerCtx) {
    Player player = playerCtx.getPlayer();
    ItemStack startGame = new ItemStack(Material.DIAMOND, 1);
    ItemMeta im = startGame.getItemMeta();
    im.setDisplayName(BedwarsRevol._l(player, "lobby.startgame"));
    startGame.setItemMeta(im);
    player.getInventory().addItem(startGame);
  }

  @Override
  public void playerLeaves(GameContext ctx, PlayerContext playerCtx, boolean kicked) {
//    Team team = this.getPlayerTeam(p);

//    BedwarsPlayerLeaveEvent leaveEvent = new BedwarsPlayerLeaveEvent(this, p, team);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(leaveEvent);

//    PlayerStatistic statistic = null;
//    if (BedwarsRel.getInstance().statisticsEnabled()) {
//      statistic = BedwarsRel.getInstance().getPlayerStatisticManager().getStatistic(p);
//    }

//    if (this.isSpectator(p)) {
//      if (!this.getCycle().isEndGameRunning()) {
//        for (Player player : this.getPlayers()) {
//          if (player.equals(p)) {
//            continue;
//          }
//
//          player.showPlayer(p);
//          p.showPlayer(player);
//        }
//      }
//    } else if (this.state == GameStateOld.RUNNING
//        && !this.getCycle().isEndGameRunning()
//        && !team.isBedDestroyed(this)
//        && this.isPlayerVirtuallyAlive(p)
//        && BedwarsRel.getInstance().statisticsEnabled()
//        && BedwarsRel.getInstance().getBooleanConfig("statistics.player-leave-kills",
//        false)) {
//      statistic.setCurrentDeaths(statistic.getCurrentDeaths() + 1);
//      statistic.setCurrentScore(statistic.getCurrentScore() + BedwarsRel.getInstance()
//          .getIntConfig("statistics.scores.die", 0));
//      if (this.getPlayerDamager(p) != null && this.getPlayerDamager(p).wasCausedRecently()) {
//        PlayerStatistic killerPlayer = BedwarsRel.getInstance().getPlayerStatisticManager()
//            .getStatistic(this.getPlayerDamager(p).getDamager());
//        killerPlayer.setCurrentKills(killerPlayer.getCurrentKills() + 1);
//        killerPlayer.setCurrentScore(killerPlayer.getCurrentScore() + BedwarsRel.getInstance()
//            .getIntConfig("statistics.scores.kill", 10));
//      }
//      statistic.setCurrentLoses(statistic.getCurrentLoses() + 1);
//      statistic.setCurrentScore(statistic.getCurrentScore() + BedwarsRel.getInstance()
//          .getIntConfig("statistics.scores.lose", 0));
//    }
//
//    if (this.isProtected(p)) {
//      this.removeProtection(p);
//    }
//
//    this.playerDamages.remove(p);

    BedwarsRevol.getInstance().getGameManager().removePlayer(playerCtx);

//    if (this.freePlayers.contains(p)) {
//      this.freePlayers.remove(p);
//    }

//    if (BedwarsRel.getInstance().isBungee()) {
//      this.cycle.onPlayerLeave(p);
//    }

//    if (BedwarsRel.getInstance().statisticsEnabled()) {
//
//      if (BedwarsRel.getInstance().isHologramsEnabled()
//          && BedwarsRel.getInstance().getHolographicInteractor() != null && BedwarsRel.getInstance()
//          .getHolographicInteractor().getType().equalsIgnoreCase("HolographicDisplays")) {
//        BedwarsRel.getInstance().getHolographicInteractor().updateHolograms(p);
//      }
//
//      if (BedwarsRel.getInstance().getBooleanConfig("statistics.show-on-game-end", true)) {
//        BedwarsRel.getInstance().getServer().dispatchCommand(p, "bw stats");
//      }
//      BedwarsRel.getInstance().getPlayerStatisticManager().storeStatistic(statistic);
//
//      BedwarsRel.getInstance().getPlayerStatisticManager().unloadStatistic(p);
//    }

    playerCtx.getState().leave(playerCtx, kicked);
    playerCtx.restoreLocation();
    playerCtx.restoreInventory();

    this.updateScoreboard(ctx);

//    try {
//      p.setScoreboard(BedwarsRel.getInstance().getScoreboardManager().getMainScoreboard());
//    } catch (Exception e) {
//      BedwarsRel.getInstance().getBugsnag().notify(e);
//    }

//    if (!BedwarsRel.getInstance().isBungee() && p.isOnline()) {
    Player player = playerCtx.getPlayer();
    if (playerCtx.getPlayer().isOnline()) {
      if (kicked) {
        player.sendMessage(
            ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol._l(
                player, "ingame.player.waskicked")));
      } else {
        player.sendMessage(ChatWriterNew.pluginMessage(
            ChatColor.GREEN + BedwarsRevol._l(player, "success.left")));
      }
    }

//    if (!BedwarsRel.getInstance().isBungee()) {
//    PlayerStorage storage = this.getGame().getPlayerStorage(player);
//
//    if (BedwarsRel.getInstance().toMainLobby()) {
//      if (BedwarsRel.getInstance().isHologramsEnabled()
//          && BedwarsRel.getInstance().getHolographicInteractor() != null
//          && this.getGame().getMainLobby().getWorld() == player.getWorld()) {
//        BedwarsRel.getInstance().getHolographicInteractor().updateHolograms(player);
//      }
//
//      player.teleport(this.getGame().getMainLobby());
//    } else {
//      if (BedwarsRel.getInstance().isHologramsEnabled()
//          && BedwarsRel.getInstance().getHolographicInteractor() != null
//          && storage.getLeft() == player.getWorld()) {
//        BedwarsRel.getInstance().getHolographicInteractor().updateHolograms(player);
//      }
//
//      player.teleport(storage.getLeft());
//    }

    ctx.updateSigns();
  }

  private void updateScoreboard(GameContext ctx) {
    Scoreboard scoreboard = ctx.getScoreboard();
    scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    Objective obj = scoreboard.getObjective("lobby");
    if (obj != null) {
      obj.unregister();
    }
    obj = scoreboard.registerNewObjective("lobby", "dummy");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName(this.formatScoreboard(ctx,
        BedwarsRevol.getInstance().getStringConfig("lobby-scoreboard.title", "&eBEDWARS")));

    List<String> rows = BedwarsRevol.getInstance().getConfig()
        .getStringList("lobby-scoreboard.content");
    int rowMax = rows.size();
    if (rows.isEmpty()) {
      return;
    }

    for (String row : rows) {
      if (row.trim().equals("")) {
        for (int i = 0; i <= rowMax; i++) {
          row = row + " ";
        }
      }

      Score score = obj.getScore(this.formatScoreboard(ctx, row));
      score.setScore(rowMax);
      rowMax--;
    }

    for (PlayerContext playerCtx : ctx.getPlayers()) {
      playerCtx.getPlayer().setScoreboard(scoreboard);
    }
  }

  private String formatScoreboard(GameContext ctx, String str) {
    String finalStr = str;
    finalStr = finalStr.replace("$regionname$", ctx.getRegion().getName());
    finalStr = finalStr.replace("$gamename$", ctx.getName());
    finalStr = finalStr.replace("$players$", String.valueOf(ctx.getPlayers().size()));
    finalStr = finalStr.replace("$maxplayers$", String.valueOf(ctx.getMaxPlayers()));
    finalStr = ChatColor.translateAlternateColorCodes('&', finalStr);
    return Utils.truncate(finalStr, GameContext.MAX_OBJECTIVE_DISPLAY_LENGTH);
  }

}
