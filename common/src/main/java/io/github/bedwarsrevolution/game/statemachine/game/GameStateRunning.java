package io.github.bedwarsrevolution.game.statemachine.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.GameOverTaskNew;
import io.github.bedwarsrevolution.game.RegionNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeBaseAlarm;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.TitleWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Bed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by {maxos} 2017
 */
public class GameStateRunning extends GameState {
  private static final String TRANSLATION = "running";

  private int timeLeft;

  public GameStateRunning(GameContext ctx) {
    super(ctx);
    this.timeLeft = BedwarsRevol.getInstance().getMaxLength();
  }

  @Override
  public void onEventCraft(CraftItemEvent event) {
    if (BedwarsRevol.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }
    event.setCancelled(true);
  }

  @Override
  public void onEventEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);
    if (playerCtx == null) {
      throw new IllegalStateException(String.format(
          "Damage event was processed by game that doesn't know the damaged player: %s",
          player.getName()));
    }

    playerCtx.getState().onDamage(playerCtx, event);

    this.checkGameOver();
  }

  @Override
  public void onEventDrop(PlayerDropItemEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onDrop(playerCtx, event);
  }

  @Override
  public void onEventFly(PlayerToggleFlightEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onFly(playerCtx, event);
  }

  @Override
  public void onEventBowShot(EntityShootBowEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext((Player) event.getEntity());
    playerCtx.getState().onBowShot(playerCtx, event);
  }

  @Override
  public void onEventInteractEntity(PlayerInteractEntityEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onInteractEntity(playerCtx, event);
  }

  @Override
  public void onEventInventoryClick(InventoryClickEvent event) {
    // Prevent armor changes
    if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
      event.setCancelled(true);
      return;
    }
    PlayerContext playerCtx = this.ctx.getPlayerContext((Player) event.getWhoClicked());
    // If open inventory isn't shop
    if (!event.getInventory().getName().equals(BedwarsRevol._l(
        playerCtx.getPlayer(), "ingame.shop.name"))) {
      return;
    }
    playerCtx.getState().onInventoryClick(playerCtx, event);
  }

  @Override
  public void onEventPlayerInteract(PlayerInteractEvent event) {
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

    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    if (clickedBlock != null
        && (clickedBlock.getType() == Material.ENDER_CHEST
        || clickedBlock.getType() == Material.CHEST)
        && !playerCtx.getState().isSpectator()) {
      event.setCancelled(true);
      Block chest = event.getClickedBlock();
      TeamNew chestTeam = this.ctx.getTeamOfEnderChest(chest);
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
  public void onEventPlayerRespawn(PlayerRespawnEvent event) {
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
  public void onEventPlayerQuit(PlayerQuitEvent event) {
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
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    this.playerLeaves(playerCtx, false);
  }

  @Override
  public void onEventPlayerBedEnter(PlayerBedEnterEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerChangeWorld(PlayerChangedWorldEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    if (!playerCtx.isTeleporting()) {
      this.playerLeaves(playerCtx, false);
    }
  }

  @Override
  public void onEventInventoryOpen(InventoryOpenEvent event) {
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
    PlayerContext playerCtx = this.ctx.getPlayerContext((Player) entity);
    if (playerCtx.getState().isSpectator()) {
      if (event.getInventory().getName()
          .equals(BedwarsRevol._l(playerCtx.getPlayer(), "ingame.spectator"))) {
        return;
      }
      event.setCancelled(true);
    }
    if (event.getInventory().getHolder() == null) {
      return;
    }
    if (this.ctx.getRegion().getInventories().contains(event.getInventory())) {
      return;
    }
    this.ctx.getRegion().addInventory(event.getInventory());
  }

  @Override
  public void playerJoins(Player player) {
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
  public void playerLeaves(PlayerContext playerCtx, boolean kicked) {
    playerCtx.getState().leave(playerCtx, kicked);
    playerCtx.restoreLocation();
    playerCtx.restoreInventory();
    this.ctx.removePlayer(playerCtx);
    if (playerCtx.getState().isSpectator()) {
      this.checkGameOver();
    }
  }

  @Override
  public void onEventBlockBreak(BlockBreakEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);

    if (playerCtx.getState().isSpectator()) {
      event.setCancelled(true);
      return;
    }

    Material targetMaterial = this.ctx.getTargetMaterial();
    if (event.getBlock().getType() == targetMaterial) {
      event.setCancelled(true);
      this.handleDestroyTargetMaterial(playerCtx, event.getBlock());
      return;
    }

    Block brokenBlock = event.getBlock();

    if (!this.ctx.getRegion().isPlacedBlock(brokenBlock)) {
      if (brokenBlock == null) {
        event.setCancelled(true);
        return;
      }

      if (BedwarsRevol.getInstance().isBreakableType(brokenBlock.getType())) {
        this.ctx.getRegion().addBrokenBlock(brokenBlock);
        event.setCancelled(false);
        return;
      }

      event.setCancelled(true);
    } else {
      if (!BedwarsRevol.getInstance().getBooleanConfig("friendlybreak", true)) {
        TeamNew playerTeam = this.ctx.getPlayerContext(player).getTeam();
        for (PlayerContext aPlayerCtx : playerTeam.getPlayers()) {
          Player aPlayer = aPlayerCtx.getPlayer();
          if (player == aPlayer) {
            continue;
          }

          if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).equals(brokenBlock)) {
            player.sendMessage(ChatWriterNew.pluginMessage(
                ChatColor.RED + BedwarsRevol._l(player, "ingame.no-friendlybreak")));
            event.setCancelled(true);
            return;
          }
        }
      }

      if (brokenBlock.getType() == Material.ENDER_CHEST) {
        for (TeamNew team : this.ctx.getTeams().values()) {
          List<Block> teamChests = team.getChests();
          if (teamChests.contains(brokenBlock)) {
            team.removeChest(brokenBlock);
            for (PlayerContext aPlayerCtx : team.getPlayers()) {
              Player aPlayer = aPlayerCtx.getPlayer();
              if (aPlayer.isOnline()) {
                aPlayer.sendMessage(ChatWriterNew.pluginMessage(
                    BedwarsRevol._l(aPlayer, "ingame.teamchestdestroy")));
              }
            }
            break;
          }
        }

        // Drop ender chest
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta meta = enderChest.getItemMeta();
        meta.setDisplayName(BedwarsRevol._l("ingame.teamchest"));
        enderChest.setItemMeta(meta);

        event.setCancelled(true);
        brokenBlock.getDrops().clear();
        brokenBlock.setType(Material.AIR);
        brokenBlock.getWorld().dropItemNaturally(brokenBlock.getLocation(), enderChest);
      }

      for (ItemStack drop : brokenBlock.getDrops()) {
        if (!drop.getType().equals(brokenBlock.getType())) {
          brokenBlock.getDrops().remove(drop);
          brokenBlock.setType(Material.AIR);
          break;
        }
      }

      this.ctx.getRegion().removePlacedBlock(brokenBlock);
    }
  }

  @Override
  public void onEventBlockIgnite(BlockIgniteEvent event) {
    if (event.getCause() == IgniteCause.ENDER_CRYSTAL || event.getCause() == IgniteCause.LIGHTNING
        || event.getCause() == IgniteCause.SPREAD) {
      event.setCancelled(true);
      return;
    }

    if (event.getIgnitingEntity() == null) {
      event.setCancelled(true);
      return;
    }

    if (!this.ctx.getRegion().isPlacedBlock(event.getIgnitingBlock())
        && event.getIgnitingBlock() != null) {
      this.ctx.getRegion().addPlacedBlock(event.getIgnitingBlock(),
          event.getIgnitingBlock().getState());
    }
  }

  @Override
  public void onEventBlockPlace(BlockPlaceEvent event) {
    final Player player = event.getPlayer();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);
    if (playerCtx.getState().isSpectator()) {
      event.setCancelled(true);
      event.setBuild(false);
      return;
    }

    Block placedBlock = event.getBlockPlaced();
    BlockState replacedBlock = event.getBlockReplacedState();

    if (placedBlock.getType() == this.ctx.getTargetMaterial()) {
      event.setCancelled(true);
      event.setBuild(false);
      return;
    }

    if (!this.ctx.getRegion().isInRegion(placedBlock.getLocation())) {
      event.setCancelled(true);
      event.setBuild(false);
      return;
    }

    if (replacedBlock != null && !BedwarsRevol
        .getInstance().getBooleanConfig("place-in-liquid", true)
        && (replacedBlock.getType().equals(Material.WATER)
        || replacedBlock.getType().equals(Material.STATIONARY_WATER)
        || replacedBlock.getType().equals(Material.LAVA)
        || replacedBlock.getType().equals(Material.STATIONARY_LAVA))) {
      event.setCancelled(true);
      event.setBuild(false);
      return;
    }

    if (replacedBlock != null && placedBlock.getType().equals(Material.WEB)
        && (replacedBlock.getType().equals(Material.WATER)
        || replacedBlock.getType().equals(Material.STATIONARY_WATER)
        || replacedBlock.getType().equals(Material.LAVA)
        || replacedBlock.getType().equals(Material.STATIONARY_LAVA))) {
      event.setCancelled(true);
      event.setBuild(false);
      return;
    }

//    if (placedBlock.getType() == Material.ENDER_CHEST) {
//      TeamNew playerTeam = playerCtx.getTeam();
//      if (playerTeam.getInventory() == null) {
//        playerTeam.createTeamInventory();
//      }
//      playerTeam.addChest(placedBlock);
//    }

    if (replacedBlock != null && !event.isCancelled()) {
      this.ctx.getRegion().addPlacedBlock(placedBlock,
          (replacedBlock.getType().equals(Material.AIR) ? null : replacedBlock));
    }

    Block block = event.getBlock();
    if (block.getType() == Material.TNT) {
      event.setCancelled(true);
      Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
      block.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
      new BukkitRunnable() {
        public void run() {
//          player.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.TNT, 1)});
//          player.updateInventory();
          PlayerInventory inv = player.getInventory();
          int slot = inv.getHeldItemSlot();
          ItemStack stack = inv.getItem(slot);
          stack.setAmount(stack.getAmount() - 1);
        }
      }.runTaskLater(BedwarsRevol.getInstance(), 1L);
    }
  }

  @Override
  public void onEventBlockSpread(BlockSpreadEvent event) {
    if (event.getNewState().getType().equals(Material.FIRE)) {
      event.setCancelled(true);
      return;
    }
    RegionNew region = this.ctx.getRegion();
    if (this.ctx.getRegion().isPlacedBlock(event.getSource())) {
      region.addPlacedBlock(event.getBlock(), event.getBlock().getState());
    } else {
      region.addPlacedUnbreakableBlock(event.getBlock(), event.getBlock().getState());
    }
  }

  @Override
  public void onEventPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
  }

  @Override
  public void onEventChunkUnload(ChunkUnloadEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventEntityExplode(EntityExplodeEvent event) {
    Iterator<Block> explodeBlocks = event.blockList().iterator();
    boolean tntDestroyEnabled = BedwarsRevol.getInstance()
        .getBooleanConfig("explodes.destroy-worldblocks", false);
    boolean tntDestroyBeds = BedwarsRevol.getInstance()
        .getBooleanConfig("explodes.destroy-beds", false);

    if (!BedwarsRevol.getInstance().getBooleanConfig("explodes.drop-blocks", false)) {
      event.setYield(0F);
    }

    Material targetMaterial = this.ctx.getTargetMaterial();
    while (explodeBlocks.hasNext()) {
      Block exploding = explodeBlocks.next();
      if (!this.ctx.getRegion().isInRegion(exploding.getLocation())) {
        explodeBlocks.remove();
        continue;
      }
      if ((!tntDestroyEnabled
              && !tntDestroyBeds)
          || (!tntDestroyEnabled
              && exploding.getType() != Material.BED_BLOCK
              && exploding.getType() != Material.BED)) {
        if (!this.ctx.getRegion().isPlacedBlock(exploding)) {
          if (BedwarsRevol.getInstance().isBreakableType(exploding.getType())) {
            this.ctx.getRegion().addBrokenBlock(exploding);
            continue;
          }
          explodeBlocks.remove();
        } else {
          this.ctx.getRegion().removePlacedBlock(exploding);
        }
        continue;
      }
      if (this.ctx.getRegion().isPlacedBlock(exploding)) {
        this.ctx.getRegion().removePlacedBlock(exploding);
        continue;
      }
      if (exploding.getType().equals(targetMaterial)) {
        if (!tntDestroyBeds) {
          explodeBlocks.remove();
          continue;
        }
        // only destroyable by tnt
        if (!event.getEntityType().equals(EntityType.PRIMED_TNT)
            && !event.getEntityType().equals(EntityType.MINECART_TNT)) {
          explodeBlocks.remove();
          continue;
        }
        // when it wasn't player who ignited the tnt
        TNTPrimed primedTnt = (TNTPrimed) event.getEntity();
        if (!(primedTnt.getSource() instanceof Player)) {
          explodeBlocks.remove();
          continue;
        }
        PlayerContext player = this.ctx.getPlayerContext((Player) primedTnt.getSource());
        if (!this.handleDestroyTargetMaterial(player, exploding)) {
          explodeBlocks.remove();
        }
      } else {
        this.ctx.getRegion().addBrokenBlock(exploding);
      }
    }
  }

  @Override
  public void onEventRegainHealth(EntityRegainHealthEvent event) {
    Player player = (Player) event.getEntity();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);
    if (player.getHealth() >= player.getMaxHealth()) {
      playerCtx.setDamager(null);
    }
  }

  @Override
  public void onEventServerListPing(ServerListPingEvent event) {
    event.setMotd(motdReplacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("bungeecord.motds.running"))));
  }

  @Override
  protected String getStatusKey() {
    return TRANSLATION;
  }

  @Override
  public void updateTime() {
    this.ctx.getRegion().getWorld().setTime(this.ctx.getTime());
  }

  @Override
  public void onEventPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);
    if (playerCtx.getState().isSpectator()) {
      return;
    }
    TeamNew team = playerCtx.getTeam();
    for (TeamNew otherTeam : this.ctx.getTeams().values()) {
      if (otherTeam == team) {
        continue;
      }
      UpgradeBaseAlarm alarm = otherTeam.getUpgrade(UpgradeBaseAlarm.class);
      if (alarm != null) {
        if (alarm.isLocationIn(event.getTo())) {
          alarm.trigger(player);
        }
      }
    }
  }

  private boolean handleDestroyTargetMaterial(PlayerContext playerCtx, Block block) {
    Player player = playerCtx.getPlayer();
    TeamNew team = playerCtx.getTeam();
    TeamNew bedDestroyTeam = null;
    Block bedBlock = team.getHeadTarget();

    if (block.getType() == Material.BED_BLOCK) {
      Block breakBlock = block;
      Block neighbor = null;
      Bed breakBed = (Bed) breakBlock.getState().getData();

      if (!breakBed.isHeadOfBed()) {
        neighbor = breakBlock;
        breakBlock = UtilsNew.getBedNeighbor(neighbor);
      } else {
        neighbor = UtilsNew.getBedNeighbor(breakBlock);
      }

      if (bedBlock.equals(breakBlock)) {
        player.sendMessage(ChatWriterNew.pluginMessage(
            ChatColor.RED + BedwarsRevol._l(player, "ingame.blocks.ownbeddestroy")));
        return false;
      }

      bedDestroyTeam = this.ctx.getTeamOfBed(breakBlock);
      if (bedDestroyTeam == null) {
        return false;
      }
      this.dropTargetBlock(block);
    } else {
      if (bedBlock.equals(block)) {
        player.sendMessage(
            ChatWriterNew.pluginMessage(
                ChatColor.RED + BedwarsRevol._l(player, "ingame.blocks.ownbeddestroy")));
        return false;
      }

      bedDestroyTeam = this.ctx.getTeamOfBed(block);
      if (bedDestroyTeam == null) {
        return false;
      }

      this.dropTargetBlock(block);
    }

    // set statistics
//    if (BedwarsRel.getInstance().statisticsEnabled()) {
//      PlayerStatistic statistic = BedwarsRel.getInstance().getPlayerStatisticManager()
//          .getStatistic(p);
//      statistic.setCurrentDestroyedBeds(statistic.getCurrentDestroyedBeds() + 1);
//      statistic.setCurrentScore(statistic.getCurrentScore() + BedwarsRel.getInstance()
//          .getIntConfig("statistics.scores.bed-destroy", 25));
//    }
//
//    // reward when destroy bed
//    if (BedwarsRel.getInstance().getBooleanConfig("rewards.enabled", false)) {
//      List<String> commands =
//          BedwarsRel.getInstance().getConfig().getStringList("rewards.player-destroy-bed");
//      BedwarsRel.getInstance()
//          .dispatchRewardCommands(commands, ImmutableMap.of("{player}", p.getName(),
//              "{score}",
//              String.valueOf(
//                  BedwarsRel.getInstance().getIntConfig("statistics.scores.bed-destroy", 25))));
//    }

//    BedwarsTargetBlockDestroyedEvent targetBlockDestroyedEvent =
//        new BedwarsTargetBlockDestroyedEvent(this, p, bedDestroyTeam);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(targetBlockDestroyedEvent);

    for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
      Player aPlayer = aPlayerCtx.getPlayer();
      if (aPlayer.isOnline()) {
        ChatColor chatColor = ChatColor.GOLD;
        if (bedDestroyTeam.isInTeam(aPlayer)) {
          String titleMsg = TitleWriterNew.pluginMessage(ChatColor.RED
              + BedwarsRevol._l(aPlayer, "ingame.blocks.beddestroyedtitle"));
          aPlayer.sendTitle(titleMsg, null, 10, 70, 20);
          chatColor = ChatColor.RED;
        }
        String chatMsg = ChatWriterNew.pluginMessage(chatColor.toString() + ChatColor.BOLD
            + BedwarsRevol._l(aPlayer, "ingame.blocks.beddestroyed",
            ImmutableMap.of("team", bedDestroyTeam.getChatColor() + ChatColor.BOLD.toString()
                    + bedDestroyTeam.getName() + chatColor.toString(),
                "player",
                ChatColor.BOLD.toString() + UtilsNew.getPlayerWithTeamString(
                    player, team, bedDestroyTeam.getChatColor()))));
        aPlayer.sendMessage(chatMsg);
      }
    }

    this.ctx.broadcastSound(Sound.valueOf(BedwarsRevol.getInstance()
            .getStringConfig("bed-sound", "ENDERDRAGON_GROWL").toUpperCase()),
        30.0F, 10.0F);
    this.updateScoreboard();
    return true;
  }

  private void updateScoreboard() {
    Scoreboard scoreboard = this.ctx.getScoreboard();
    Objective obj = scoreboard.getObjective("display");
    if (obj == null) {
      obj = scoreboard.registerNewObjective("display", "dummy");
    }

    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName(this.formatScoreboardTitle());

    for (TeamNew team : this.ctx.getTeams().values()) {
      scoreboard.resetScores(this.formatScoreboardTeam(team, false));
      scoreboard.resetScores(this.formatScoreboardTeam(team, true));

      Score score = obj.getScore(this.formatScoreboardTeam(team, team.isBedDestroyed()));
      score.setScore(team.getPlayers().size());
    }

    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      playerCtx.getPlayer().setScoreboard(scoreboard);
    }
  }

  private String getFormattedTimeLeft() {
    int min = this.timeLeft / 60;
    int sec = this.timeLeft % 60;
    return String.format("%2d:%02d", min, sec);
  }

  private String formatScoreboardTitle() {
    String format = BedwarsRevol.getInstance()
            .getStringConfig("scoreboard.format-title", "&e$region$&f - $time$");
    format = format.replace("$region$", this.ctx.getRegion().getName());
    format = format.replace("$game$", this.ctx.getName());
    format = format.replace("$time$", this.getFormattedTimeLeft());

    format = ChatColor.translateAlternateColorCodes('&', format);
    return UtilsNew.truncate(format, GameContext.MAX_OBJECTIVE_DISPLAY_LENGTH);
  }

  private String formatScoreboardTeam(TeamNew team, boolean bedDestroyed) {
    String format;
    if (bedDestroyed) {
      format = BedwarsRevol.getInstance().getStringConfig("scoreboard.format-bed-destroyed",
          "&c$status$ $team$");
    } else {
      format =BedwarsRevol.getInstance().getStringConfig(
          "scoreboard.format-bed-alive", "&a$status$ $team$");
    }
    format = format.replace("$status$", (bedDestroyed) ?
        GameContext.BED_DESTROYED : GameContext.BED_EXISTS);
    format = format.replace("$team$", team.getChatColor() + team.getName());

    format = ChatColor.translateAlternateColorCodes('&', format);
    return UtilsNew.truncate(format, GameContext.MAX_SCORE_LENGTH);
  }

  private void dropTargetBlock(Block targetBlock) {
    if (targetBlock.getType().equals(Material.BED_BLOCK)) {
      Block bedHead;
      Block bedFeet;
      Bed bedBlock = (Bed) targetBlock.getState().getData();

      if (!bedBlock.isHeadOfBed()) {
        bedFeet = targetBlock;
        bedHead = UtilsNew.getBedNeighbor(bedFeet);
      } else {
        bedHead = targetBlock;
        bedFeet = UtilsNew.getBedNeighbor(bedHead);
      }

      if (!BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_12")) {
        bedFeet.setType(Material.AIR);
      } else {
        bedHead.setType(Material.AIR);
      }
    } else {
      targetBlock.setType(Material.AIR);
    }
  }

  private void checkGameOver() {
    if (!BedwarsRevol.getInstance().isEnabled()) {
      return;
    }
    if (this.ctx.getPlayers().size() == 0) {
      this.ctx.setState(new GameStateWaiting(this.ctx));
      return;
    }
    Set<TeamNew> notLostTeams = new HashSet<>();
    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      TeamNew team = playerCtx.getTeam();
      if (!team.isBedDestroyed() || playerCtx.isVirtuallyAlive()) {
        notLostTeams.add(team);
      }
    }
    if (notLostTeams.size() > 0) {
      return;
    }
    if (notLostTeams.size() == 1) {
      this.runGameOver(notLostTeams.iterator().next());
    } else {
      this.runGameOver(null);
    }
  }

  private void runGameOver(TeamNew winner) {
//    BedwarsGameOverEvent overEvent = new BedwarsGameOverEvent(this.getGame(), winner);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(overEvent);
//
//    if (overEvent.isCancelled()) {
//      return;
//    }

    this.ctx.stopWorkers();

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

    GameOverTaskNew gameOver = new GameOverTaskNew(this.ctx, delay, winner);
    gameOver.runTaskTimer(BedwarsRevol.getInstance(), 0L, 20L);
  }

}
