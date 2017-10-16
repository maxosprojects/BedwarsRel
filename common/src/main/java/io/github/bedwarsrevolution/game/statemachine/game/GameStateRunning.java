package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
  }

  @Override
  public void addPlayer(GameContext gameContext, Player player) {
    // TODO: implement something
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
    Material interactingMaterial = event.getMaterial();
    Block clickedBlock = event.getClickedBlock();
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
  }

  @Override
  public void onEventPlayerRespawn(GameContext ctx, PlayerRespawnEvent event) {
    Team team = this.getGame().getPlayerTeam(player);

    // reset damager
    this.getGame().setPlayerDamager(player, null);

    if (this.getGame().isSpectator(player)) {
      Collection<Team> teams = this.getGame().getTeams().values();
      pre.setRespawnLocation(
          ((Team) teams.toArray()[Utils.randInt(0, teams.size() - 1)]).getSpawnLocation());
      return;
    }

    PlayerStorage storage = this.getGame().getPlayerStorage(player);

    if (team.isBedDestroyed(this.getGame())) {

      if (BedwarsRel.getInstance().statisticsEnabled()) {
        PlayerStatistic statistic =
            BedwarsRel.getInstance().getPlayerStatisticManager().getStatistic(player);
        statistic.setCurrentLoses(statistic.getCurrentLoses() + 1);
      }

      if (BedwarsRel.getInstance().spectationEnabled()) {
        if (storage != null && storage.getLeft() != null) {
          pre.setRespawnLocation(team.getSpawnLocation());
        }

        this.getGame().toSpectator(player);
      } else {
        if (this.game.getCycle() instanceof BungeeGameCycle) {
          this.getGame().playerLeave(player, false);
          return;
        }

        if (!BedwarsRel.getInstance().toMainLobby()) {
          if (storage != null) {
            if (storage.getLeft() != null) {
              pre.setRespawnLocation(storage.getLeft());
            }
          }
        } else {
          if (this.getGame().getMainLobby() != null) {
            pre.setRespawnLocation(this.getGame().getMainLobby());
          } else {
            if (storage != null) {
              if (storage.getLeft() != null) {
                pre.setRespawnLocation(storage.getLeft());
              }
            }
          }
        }

        this.getGame().playerLeave(player, false);
      }

    } else {
      if (BedwarsRel.getInstance().getRespawnProtectionTime() > 0) {
        RespawnProtectionRunnable protection = this.getGame().addProtection(player);
        protection.runProtection();
      }
      pre.setRespawnLocation(team.getSpawnLocation());

      storage.respawn();
    }

    new BukkitRunnable() {

      @Override
      public void run() {
        GameCycle.this.checkGameOver();
      }
    }.runTaskLater(BedwarsRel.getInstance(), 20L);

  }

  @Override
  public void onEventPlayerQuit(GameContext ctx, PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (BedwarsRevol.getInstance().isBungee()) {
      event.setQuitMessage(null);
    }
    // Remove holograms
    if (BedwarsRevol.getInstance().isHologramsEnabled()
        && BedwarsRevol.getInstance().getHolographicInteractor() != null && BedwarsRevol.getInstance()
        .getHolographicInteractor().getType().equalsIgnoreCase("HolographicDisplays")) {
      BedwarsRevol.getInstance().getHolographicInteractor().unloadAllHolograms(player);
    }
    if (BedwarsRevol.getInstance().statisticsEnabled()) {
      BedwarsRevol.getInstance().getPlayerStatisticManager().unloadStatistic(player);
    }
    ctx.playerLeave(player, false);
  }

  @Override
  public void onEventPlayerBedEnter(GameContext ctx, PlayerBedEnterEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerChangeWorld(GameContext ctx, PlayerChangedWorldEvent event) {
    if (!game.getCycle().isEndGameRunning()) {
      if (!game.getPlayerFlags(change.getPlayer()).isTeleporting()) {
        game.playerLeave(change.getPlayer(), false);
      }
    }
  }

  @Override
  public void onEventInventoryOpen(GameContext ctx, InventoryOpenEvent event) {
    if (event.getInventory().getType() == InventoryType.ENCHANTING
        || event.getInventory().getType() == InventoryType.BREWING
        || (event.getInventory().getType() == InventoryType.CRAFTING
        && !BedwarsRel.getInstance().getBooleanConfig("allow-crafting", false))) {
      event.setCancelled(true);
      return;
    } else if (event.getInventory().getType() == InventoryType.CRAFTING
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

  @Override
  public void playerJoins(GameContext ctx, Player player) {
    if (!BedwarsRevol.getInstance().spectationEnabled()) {
//    if (this.cycle instanceof BungeeGameCycle) {
//      ((BungeeGameCycle) this.cycle).sendBungeeMessage(p,
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(p, "errors.cantjoingame")));
//    } else {
      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRevol
          ._l(player, "errors.cantjoingame")));
      return;
//    }
    }

    this.toSpectator(p);
    this.displayMapInfo(p);
  }

  @Override
  public void playerLeaves(GameContext ctx, PlayerContext playerCtx, boolean kicked) {
    if (playerCtx.getState().isSpectator()) {
      this.checkGameOver();
    }
  }

}
