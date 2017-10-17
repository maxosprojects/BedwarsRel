package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.GameOverTaskNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
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

/**
 * Created by {maxos} 2017
 */
public class GameStateRunning implements GameState {

  @Override
  public void onEventCraft(GameContext ctx, CraftItemEvent event) {
    if (BedwarsRevol.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }
    event.setCancelled(true);
  }

  @Override
  public void onEventDamage(GameContext ctx, EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    PlayerContext playerCtx = ctx.getPlayerContext(player);
    if (playerCtx == null) {
      throw new IllegalStateException(String.format(
          "Damage event was processed by game that doesn't know the damaged player: %s",
          player.getName()));
    }

    playerCtx.getState().onDamage(playerCtx, event);

    this.checkGameOver(ctx);
  }

  @Override
  public void onEventDrop(GameContext ctx, PlayerDropItemEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onDrop(playerCtx, event);
  }

  @Override
  public void onEventFly(GameContext ctx, PlayerToggleFlightEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onFly(playerCtx, event);
  }

  @Override
  public void onEventBowShot(GameContext ctx, EntityShootBowEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext((Player) event.getEntity());
    playerCtx.getState().onBowShot(playerCtx, event);
  }

  @Override
  public void onEventInteractEntity(GameContext ctx, PlayerInteractEntityEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onInteractEntity(playerCtx, event);
  }

  @Override
  public void onEventInventoryClick(GameContext ctx, InventoryClickEvent event) {
    // Prevent armor changes
    if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
      event.setCancelled(true);
      return;
    }
    PlayerContext playerCtx = ctx.getPlayerContext((Player) event.getWhoClicked());
    // If open inventory isn't shop
    if (!event.getInventory().getName().equals(BedwarsRevol._l(
        playerCtx.getPlayer(), "ingame.shop.name"))) {
      return;
    }
    playerCtx.getState().onInventoryClick(playerCtx, event);
  }

  @Override
  public void onEventPlayerInteract(GameContext ctx, PlayerInteractEvent event) {
//    Material interactingMaterial = event.getMaterial();
    Block clickedBlock = event.getClickedBlock();
    if (event.getAction() == Action.PHYSICAL && clickedBlock != null
        && (clickedBlock.getType() == Material.WHEAT
        || clickedBlock.getType() == Material.SOIL)) {
      event.setCancelled(true);
      return;
    }
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }

//    if (clickedBlock != null && clickedBlock.getType() == Material.LEVER && !g.isSpectator(player)
//        && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
//      if (!g.getRegion().isPlacedUnbreakableBlock(clickedBlock)) {
//        g.getRegion().addPlacedUnbreakableBlock(clickedBlock, clickedBlock.getState());
//      }
//      return;
//    }

//    if (g.isSpectator(player)
//        || (g.getCycle() instanceof BungeeGameCycle && g.getCycle().isEndGameRunning()
//        && BedwarsRel.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))) {
//      if (interactingMaterial == Material.SLIME_BALL) {
//        g.playerLeave(player, false);
//        return;
//      }
//
//      if (interactingMaterial == Material.COMPASS) {
//        g.openSpectatorCompass(player);
//        pie.setCancelled(true);
//        return;
//      }
//    }

//    // Spectators want to block
//    if (clickedBlock != null) {
//      try {
//        GameMode.valueOf("SPECTATOR");
//      } catch (Exception ex) {
//        BedwarsRel.getInstance().getBugsnag().notify(ex);
//        for (Player p : g.getFreePlayers()) {
//          if (!g.getRegion().isInRegion(p.getLocation())) {
//            continue;
//          }
//
//          if (pie.getClickedBlock().getLocation().distance(p.getLocation()) < 2) {
//            Location oldLocation = p.getLocation();
//            if (oldLocation.getY() >= pie.getClickedBlock().getLocation().getY()) {
//              oldLocation.setY(oldLocation.getY() + 2);
//            } else {
//              oldLocation.setY(oldLocation.getY() - 2);
//            }
//            p.teleport(oldLocation);
//          }
//        }
//      }
//    }

    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    if (clickedBlock != null
        && (clickedBlock.getType() == Material.ENDER_CHEST
        || clickedBlock.getType() == Material.CHEST)
        && !playerCtx.getState().isSpectator()) {
      event.setCancelled(true);
      Block chest = event.getClickedBlock();
      TeamNew chestTeam = ctx.getTeamOfEnderChest(chest);
      TeamNew playerTeam = playerCtx.getTeam();
      if (chestTeam == null) {
        return;
      }
      Player player = playerCtx.getPlayer();
      if (chestTeam == playerTeam) {
        player.openInventory(chestTeam.getInventory());
      } else {
        player.sendMessage(
            ChatWriterNew
                .pluginMessage(ChatColor.RED + BedwarsRevol._l(player, "ingame.noturteamchest")));
      }
    }
  }

  @Override
  public void onEventPlayerRespawn(GameContext ctx, PlayerRespawnEvent event) {
//    Team team = this.getGame().getPlayerTeam(player);
//
//    // reset damager
//    this.getGame().setPlayerDamager(player, null);
//
//    if (this.getGame().isSpectator(player)) {
//      Collection<Team> teams = this.getGame().getTeams().values();
//      pre.setRespawnLocation(
//          ((Team) teams.toArray()[Utils.randInt(0, teams.size() - 1)]).getSpawnLocation());
//      return;
//    }
//
//    PlayerStorage storage = this.getGame().getPlayerStorage(player);
//
//    if (team.isBedDestroyed(this.getGame())) {
//
//      if (BedwarsRel.getInstance().statisticsEnabled()) {
//        PlayerStatistic statistic =
//            BedwarsRel.getInstance().getPlayerStatisticManager().getStatistic(player);
//        statistic.setCurrentLoses(statistic.getCurrentLoses() + 1);
//      }
//
//      if (BedwarsRel.getInstance().spectationEnabled()) {
//        if (storage != null && storage.getLeft() != null) {
//          pre.setRespawnLocation(team.getSpawnLocation());
//        }
//
//        this.getGame().toSpectator(player);
//      } else {
//        if (this.game.getCycle() instanceof BungeeGameCycle) {
//          this.getGame().playerLeave(player, false);
//          return;
//        }
//
//        if (!BedwarsRel.getInstance().toMainLobby()) {
//          if (storage != null) {
//            if (storage.getLeft() != null) {
//              pre.setRespawnLocation(storage.getLeft());
//            }
//          }
//        } else {
//          if (this.getGame().getMainLobby() != null) {
//            pre.setRespawnLocation(this.getGame().getMainLobby());
//          } else {
//            if (storage != null) {
//              if (storage.getLeft() != null) {
//                pre.setRespawnLocation(storage.getLeft());
//              }
//            }
//          }
//        }
//
//        this.getGame().playerLeave(player, false);
//      }
//
//    } else {
//      if (BedwarsRel.getInstance().getRespawnProtectionTime() > 0) {
//        RespawnProtectionRunnable protection = this.getGame().addProtection(player);
//        protection.runProtection();
//      }
//      pre.setRespawnLocation(team.getSpawnLocation());
//
//      storage.respawn();
//    }
//
//    new BukkitRunnable() {
//
//      @Override
//      public void run() {
//        GameCycle.this.checkGameOver();
//      }
//    }.runTaskLater(BedwarsRel.getInstance(), 20L);

  }

  @Override
  public void onEventPlayerQuit(GameContext ctx, PlayerQuitEvent event) {
//    Player player = event.getPlayer();
//    if (BedwarsRevol.getInstance().isBungee()) {
//      event.setQuitMessage(null);
//    }
//    // Remove holograms
//    if (BedwarsRevol.getInstance().isHologramsEnabled()
//        && BedwarsRevol.getInstance().getHolographicInteractor() != null && BedwarsRevol.getInstance()
//        .getHolographicInteractor().getType().equalsIgnoreCase("HolographicDisplays")) {
//      BedwarsRevol.getInstance().getHolographicInteractor().unloadAllHolograms(player);
//    }
//    if (BedwarsRevol.getInstance().statisticsEnabled()) {
//      BedwarsRevol.getInstance().getPlayerStatisticManager().unloadStatistic(player);
//    }
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    this.playerLeaves(ctx, playerCtx, false);
  }

  @Override
  public void onEventPlayerBedEnter(GameContext ctx, PlayerBedEnterEvent event) {
    event.setCancelled(true);
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
    if (event.getInventory().getType() == InventoryType.ENCHANTING
        || event.getInventory().getType() == InventoryType.BREWING
        || (event.getInventory().getType() == InventoryType.CRAFTING
        && !BedwarsRevol.getInstance().getBooleanConfig("allow-crafting", false))) {
      event.setCancelled(true);
      return;
    } else if (event.getInventory().getType() == InventoryType.CRAFTING
        && BedwarsRevol.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }

    HumanEntity entity = event.getPlayer();
    if (!(entity instanceof Player)) {
      return;
    }
    PlayerContext playerCtx = ctx.getPlayerContext((Player) entity);
    if (playerCtx.getState().isSpectator()) {
      if (event.getInventory().getName().equals(BedwarsRevol._l(playerCtx.getPlayer(), "ingame.spectator"))) {
        return;
      }
      event.setCancelled(true);
    }
    if (event.getInventory().getHolder() == null) {
      return;
    }
    if (ctx.getRegion().getInventories().contains(event.getInventory())) {
      return;
    }
    ctx.getRegion().addInventory(event.getInventory());
  }

  @Override
  public void playerJoins(GameContext ctx, Player player) {
    if (!BedwarsRevol.getInstance().spectationEnabled()) {
//    if (this.cycle instanceof BungeeGameCycle) {
//      ((BungeeGameCycle) this.cycle).sendBungeeMessage(p,
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(p, "errors.cantjoingame")));
//    } else {
      player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
          ._l(player, "errors.cantjoingame")));
//    }
    }

//    this.toSpectator(p);
//    this.displayMapInfo(p);
  }

  @Override
  public void playerLeaves(GameContext ctx, PlayerContext playerCtx, boolean kicked) {
    playerCtx.getState().leave(playerCtx, kicked);
    playerCtx.restoreLocation();
    playerCtx.restoreInventory();
    ctx.removePlayer(playerCtx);
    if (playerCtx.getState().isSpectator()) {
      this.checkGameOver(ctx);
    }
  }

  private void checkGameOver(GameContext ctx) {
    if (!BedwarsRevol.getInstance().isEnabled()) {
      return;
    }
    if (ctx.getPlayers().size() == 0) {
      ctx.setState(new GameStateWaiting());
      return;
    }
    Set<TeamNew> notLostTeams = new HashSet<>();
    for (PlayerContext playerCtx : ctx.getPlayers()) {
      TeamNew team = playerCtx.getTeam();
      if (!team.isBedDestroyed() || playerCtx.isVirtuallyAlive()) {
        notLostTeams.add(team);
      }
    }
    if (notLostTeams.size() > 0) {
      return;
    }
    if (notLostTeams.size() == 1) {
      this.runGameOver(ctx, notLostTeams.iterator().next());
    } else {
      this.runGameOver(ctx, null);
    }
  }

  private void runGameOver(GameContext ctx, TeamNew winner) {
//    BedwarsGameOverEvent overEvent = new BedwarsGameOverEvent(this.getGame(), winner);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(overEvent);
//
//    if (overEvent.isCancelled()) {
//      return;
//    }

    ctx.stopWorkers();

//    // new record?
//    boolean storeRecords = BedwarsRel.getInstance().getBooleanConfig("store-game-records", true);
//    boolean storeHolders = BedwarsRel
//        .getInstance().getBooleanConfig("store-game-records-holder", true);
//    boolean madeRecord = false;
//    if (storeRecords && winner != null) {
//      madeRecord = this.storeRecords(storeHolders, winner);
//    }

    int delay = BedwarsRevol.getInstance().getConfig().getInt("gameoverdelay"); // configurable

//    processStatsAndRewards(winner, madeRecord);

//    this.getGame().getPlayingTeams().clear();

    GameOverTaskNew gameOver = new GameOverTaskNew(ctx, delay, winner);
    gameOver.runTaskTimer(BedwarsRevol.getInstance(), 0L, 20L);
  }

}
