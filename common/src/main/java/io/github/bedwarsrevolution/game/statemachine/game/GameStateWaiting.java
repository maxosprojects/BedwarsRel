package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriter;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

/**
 * Created by {maxos} 2017
 */
public class GameStateWaiting implements GameState {

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
  public void addPlayer(GameContext gameContext, Player player) {
    // TODO: implement something
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
    if (!game.getPlayerFlags(change.getPlayer()).isTeleporting()) {
      game.playerLeave(change.getPlayer(), false);
    }
  }

  @Override
  public void onEventInventoryOpen(GameContext ctx, InventoryOpenEvent event) {

  }

  @Override
  public void playerJoins(GameContext ctx, Player player) {
    ctx.addPlayer(player);

    if (ctx.isFull()) {
      if (!player.hasPermission("bw.vip.joinfull")) {
        player.sendMessage(
            ChatWriter.pluginMessage(ChatColor.RED + BedwarsRevol._l(player, "lobby.gamefull")));
        return;
      } else {
        List<Player> players = ctx.getNonVipPlayers();
        if (players.size() == 0) {
          player.sendMessage(ChatWriter.pluginMessage(
              ChatColor.RED + BedwarsRevol._l(player, "lobby.gamefullpremium")));
          return;
        }
        Player kickPlayer = null;
        if (players.size() == 1) {
          kickPlayer = players.get(0);
        } else {
          kickPlayer = players.get(Utils.randInt(0, players.size() - 1));
        }
        kickPlayer.sendMessage(ChatWriter.pluginMessage(
            ChatColor.RED + BedwarsRevol._l(kickPlayer, "lobby.kickedbyvip")));
        ctx.playerLeave(kickPlayer, false);
      }
    }

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

    BedwarsRevol.getInstance().getGameManager().addGamePlayer(player, this);
    if (BedwarsRevol.getInstance().statisticsEnabled()) {
      BedwarsRevol.getInstance().getPlayerStatisticManager().getStatistic(player);
    }

    new BukkitRunnable() {

      @Override
      public void run() {
        for (Player playerInGame : Game.this.getPlayers()) {
          playerInGame.hidePlayer(p);
          p.hidePlayer(playerInGame);
        }
      }

    }.runTaskLater(BedwarsRel.getInstance(), 5L);

    if (this.state == GameStateOld.RUNNING) {
      this.toSpectator(p);
      this.displayMapInfo(p);
    } else {
      storage.store();
      storage.clean(true);

      if (!BedwarsRel.getInstance().isBungee()) {
        final Location location = this.getPlayerTeleportLocation(p);
        if (!p.getLocation().equals(location)) {
          this.setTeleportingIfWorldChange(p, location);
          // TODO: Figure out what was this insane logic here for (not bungee outer if and
          // bungee inner if)
          if (BedwarsRel.getInstance().isBungee()) {
            new BukkitRunnable() {

              @Override
              public void run() {
                p.teleport(location);
              }

            }.runTaskLater(BedwarsRel.getInstance(), 10L);
          } else {
            p.teleport(location);
          }
        }
      }

      storage.loadLobbyInventory(this);

      new BukkitRunnable() {

        @Override
        public void run() {
          Game.this.setPlayerGameMode(p);
          Game.this.setPlayerVisibility(p);
        }

      }.runTaskLater(BedwarsRel.getInstance(), 15L);

      for (Player aPlayer : this.getPlayers()) {
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(
              ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(aPlayer, "lobby.playerjoin",
                  ImmutableMap.of("player", p.getDisplayName() + ChatColor.GREEN))));
        }
      }

      if (!this.isAutobalanceEnabled()) {
        this.freePlayers.add(p);
      } else {
        Team team = this.getLowestTeam();
        team.addPlayer(p);
      }

      if (BedwarsRel.getInstance().getBooleanConfig("store-game-records", true)) {
        this.displayRecord(p);
      }

      if (this.isStartable()) {
        if (this.gameLobbyCountdown == null) {
          this.gameLobbyCountdown = new GameLobbyCountdown(this);
          this.gameLobbyCountdown.runTaskTimer(BedwarsRel.getInstance(), 20L, 20L);
        }
      } else {
        if (!this.hasEnoughPlayers()) {
          int playersNeeded = this.getMinPlayers() - this.getPlayerAmount();
          for (Player aPlayer : this.getPlayers()) {
            if (aPlayer.isOnline()) {
              aPlayer.sendMessage(ChatWriter
                  .pluginMessage(
                      ChatColor.GREEN + BedwarsRel._l(aPlayer, "lobby.moreplayersneeded", "count",
                          ImmutableMap.of("count", String.valueOf(playersNeeded)))));
            }
          }
        } else if (!this.hasEnoughTeams()) {
          for (Player aPlayer : this.getPlayers()) {
            if (aPlayer.isOnline()) {
              aPlayer.sendMessage(ChatWriter
                  .pluginMessage(ChatColor.RED + BedwarsRel._l(aPlayer, "lobby.moreteamsneeded")));
            }
          }
        }
      }
    }

    BedwarsPlayerJoinedEvent joinEvent = new BedwarsPlayerJoinedEvent(this, null, p);
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(joinEvent);

    this.updateScoreboard();
    this.updateSigns();

    player.sendMessage(ChatWriter.pluginMessage(
        ChatColor.GREEN + BedwarsRevol._l(player, "success.joined")));
  }

}
