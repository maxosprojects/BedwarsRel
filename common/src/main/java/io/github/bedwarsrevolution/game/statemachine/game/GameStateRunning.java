package io.github.bedwarsrevolution.game.statemachine.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.Cooldown;
import io.github.bedwarsrevolution.game.Cooldown.Scope;
import io.github.bedwarsrevolution.game.RegionNew;
import io.github.bedwarsrevolution.game.ResourceSpawnerNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerState;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerStatePlaying;
import io.github.bedwarsrevolution.shop.MerchantCategory;
import io.github.bedwarsrevolution.shop.upgrades.Upgrade;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeBaseAlarm;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeItem;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeRegistry;
import io.github.bedwarsrevolution.shop.upgrades.UpgradeScope;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.TitleWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
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
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Bed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

/**
 * Created by {maxos} 2017
 */
public class GameStateRunning extends GameState {

  private static final String TRANSLATION = "running";

  private int timeLeft;
  private int length;

  public GameStateRunning(GameContext ctx) {
    super(ctx);
    this.timeLeft = BedwarsRevol.getInstance().getMaxLength();
    this.length = BedwarsRevol.getInstance().getMaxLength();
  }

  @Override
  public void onEventCraft(CraftItemEvent event) {
    if (BedwarsRevol.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }
    event.setCancelled(true);
  }

  @Override
  public void onEventEntityDamageToPlayer(EntityDamageEvent event, Player damager) {
    PlayerContext playerCtx = this.ctx.getPlayerContext((Player) event.getEntity());
    playerCtx.getState().onDamageToPlayer(event, damager);
    this.checkGameOver();
  }

  @Override
  public void onEventEntityDamageByPlayer(EntityDamageEvent event, Player damager) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(damager);
    playerCtx.getState().onDamageByPlayer(event);
    this.checkGameOver();
  }

  @Override
  public void onEventDrop(PlayerDropItemEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onDrop(event);
  }

  @Override
  public void onEventFly(PlayerToggleFlightEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onFly(event);
  }

  @Override
  public void onEventBowShot(EntityShootBowEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext((Player) event.getEntity());
    playerCtx.getState().onBowShot(event);
  }

  @Override
  public void onEventInteractEntity(PlayerInteractEntityEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onInteractEntity(event);
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
    playerCtx.getState().onInventoryClick(event);
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

    Player player = event.getPlayer();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);
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
      if (chestTeam == playerTeam) {
        player.openInventory(chestTeam.getInventory());
      } else {
        player.sendMessage(
            ChatWriterNew
                .pluginMessage(ChatColor.RED + BedwarsRevol._l(player, "ingame.noturteamchest")));
      }
    }

    if (event.getItem() != null && event.getItem().getType() == Material.FIREBALL) {
      event.setCancelled(true);
      if (!playerCtx.useItem(Material.FIREBALL.name())) {
        return;
      }
      // Take one fireball from the player
      Inventory inv = player.getInventory();
      int slot = inv.first(Material.FIREBALL);
      ItemStack stack = inv.getItem(slot);
      stack.setAmount(stack.getAmount() - 1);
      // Launch fireball
      Vector vec = player.getLocation().getDirection();
      Fireball ball = player.launchProjectile(Fireball.class);
      // Setting incendiary to false disables explosion damage oO
//      ball.setIsIncendiary(false);
//      ball.setYield(2);
      ball.setVelocity(vec);
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
    playerCtx.getState().leave(kicked);
    playerCtx.restoreLocation();
    playerCtx.restoreInventory();
    BedwarsRevol.getInstance().getGameManager().removePlayer(playerCtx.getPlayer());
    this.ctx.removePlayer(playerCtx);
    playerCtx.getTeam().removePlayer(playerCtx);
    playerCtx.getPlayer().setScoreboard(
        BedwarsRevol.getInstance().getScoreboardManager().getNewScoreboard());
    this.checkGameOver();
    this.ctx.updateSigns();
  }

  @Override
  public void onEventBlockBreak(BlockBreakEvent event) {
//    event.setCancelled(true);

    Player player = event.getPlayer();
    PlayerContext playerCtx = this.ctx.getPlayerContext(player);

    if (playerCtx.getState().isSpectator()) {
      event.setCancelled(true);
      return;
    }

    Block brokenBlock = event.getBlock();
    Material brokenBlockType = brokenBlock.getType();

    Material targetMaterial = this.ctx.getTargetMaterial();
    if (brokenBlockType == targetMaterial) {
      event.setCancelled(true);
      this.handleDestroyTargetMaterial(playerCtx, brokenBlock);
      return;
    }

    if (!this.ctx.getRegion().isPlacedBlock(brokenBlock)) {
      event.setCancelled(true);
      return;
    }

//    if (!this.ctx.getRegion().isPlacedBlock(brokenBlock)) {
//      if (brokenBlock == null) {
//        event.setCancelled(true);
//        return;
//      }
//
//      if (BedwarsRevol.getInstance().isBreakableType(brokenBlock.getType())) {
//        this.ctx.getRegion().addBrokenBlock(brokenBlock);
//        event.setCancelled(false);
//        return;
//      }
//
//      event.setCancelled(true);
//    } else {

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

//      if (brokenBlock.getType() == Material.ENDER_CHEST) {
//        for (TeamNew team : this.ctx.getTeams().values()) {
//          List<Block> teamChests = team.getChests();
//          if (teamChests.contains(brokenBlock)) {
//            team.removeChest(brokenBlock);
//            for (PlayerContext aPlayerCtx : team.getPlayers()) {
//              Player aPlayer = aPlayerCtx.getPlayer();
//              if (aPlayer.isOnline()) {
//                aPlayer.sendMessage(ChatWriterNew.pluginMessage(
//                    BedwarsRevol._l(aPlayer, "ingame.teamchestdestroy")));
//              }
//            }
//            break;
//          }
//        }
//
//        // Drop ender chest
//        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST, 1);
//        ItemMeta meta = enderChest.getItemMeta();
//        meta.setDisplayName(BedwarsRevol._l("ingame.teamchest"));
//        enderChest.setItemMeta(meta);
//
//        event.setCancelled(true);
//        brokenBlock.getDrops().clear();
//        brokenBlock.setType(Material.AIR);
//        brokenBlock.getWorld().dropItemNaturally(brokenBlock.getLocation(), enderChest);
//      }

    // Prevent drops of type that is not the broken block
    for (ItemStack drop : brokenBlock.getDrops()) {
      if (drop.getType() != brokenBlock.getType()) {
        brokenBlock.getDrops().remove(drop);
        brokenBlock.setType(Material.AIR);
        break;
      }
    }
    this.ctx.getRegion().removePlacedBlock(brokenBlock);
  }

  @Override
  public void onEventBlockIgnite(BlockIgniteEvent event) {
    IgniteCause cause = event.getCause();
    if (cause == IgniteCause.ENDER_CRYSTAL
        || cause == IgniteCause.LIGHTNING
        || cause == IgniteCause.SPREAD
        || cause == IgniteCause.EXPLOSION) {
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
      TNTPrimed tnt = (TNTPrimed) block.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
      try {
        Class<?> tntSourceClass = BedwarsRevol.getInstance().getVersionRelatedClass("TntSource");
        final Method methodSetSource = tntSourceClass.getMethod(
            "setSource", Player.class, TNTPrimed.class);
        methodSetSource.invoke(null, player, tnt);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
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

  public void onEventExplosionPrime(ExplosionPrimeEvent event) {
    if (event.getEntityType() == EntityType.PRIMED_TNT) {
      event.setRadius(2);
    }
  }

  @Override
  public void onEventEntityExplode(EntityExplodeEvent event) {
    Iterator<Block> explodingBlocksIter = event.blockList().iterator();
    boolean tntDestroyWorldEnabled = BedwarsRevol.getInstance()
        .getBooleanConfig("explodes.destroy-worldblocks", false);
    boolean tntDestroyBedsEnabled = BedwarsRevol.getInstance()
        .getBooleanConfig("explodes.destroy-beds", false);
    EntityType entityType = event.getEntityType();
    if (!BedwarsRevol.getInstance().getBooleanConfig("explodes.block-drops", false)) {
      event.setYield(0F);
    }

    // Establish the player that initiated explosion
    Player player = null;
    if (entityType == EntityType.PRIMED_TNT
        || entityType == EntityType.MINECART_TNT) {
      TNTPrimed tnt = (TNTPrimed) event.getEntity();
      if (tnt.getSource() instanceof Player) {
        player = (Player) tnt.getSource();
      }
    } else if (entityType == EntityType.FIREBALL) {
      Fireball ball = (Fireball) event.getEntity();
      if (ball.getShooter() instanceof Player) {
        player = (Player) ball.getShooter();
      }
    }

    Material targetMaterial = this.ctx.getTargetMaterial();
    while (explodingBlocksIter.hasNext()) {
      Block explodingBlock = explodingBlocksIter.next();
      Material explodingType = explodingBlock.getType();
      // Skip any block that isn't in game's region
      if (!this.ctx.getRegion().isInRegion(explodingBlock.getLocation())) {
        explodingBlocksIter.remove();
        continue;
      }
      if (player == null) {
        explodingBlocksIter.remove();
        continue;
      }

      if (explodingType == targetMaterial) {
        if (tntDestroyBedsEnabled) {
          if (!this.handleDestroyTargetMaterial(this.ctx.getPlayerContext(player), explodingBlock)) {
            explodingBlocksIter.remove();
          }
        } else {
          explodingBlocksIter.remove();
        }
        continue;
      }

      // Blocks can be destroyed only by explosions from TNT and Fireballs
      if (entityType != EntityType.PRIMED_TNT
          && entityType != EntityType.MINECART_TNT
          && entityType != EntityType.FIREBALL) {
        explodingBlocksIter.remove();
        continue;
      }

      // Fireballs can only destroy wool and wood
      if (entityType == EntityType.FIREBALL
          && explodingType != Material.WOOL
          && explodingType != Material.WOOD) {
        explodingBlocksIter.remove();
        continue;
      }

      if (this.ctx.getRegion().isPlacedBlock(explodingBlock)) {
        this.ctx.getRegion().removePlacedBlock(explodingBlock);
      } else {
        if (tntDestroyWorldEnabled) {
          if (BedwarsRevol.getInstance().isBreakableType(explodingType)) {
            this.ctx.getRegion().addBrokenBlock(explodingBlock);
          } else {
            explodingBlocksIter.remove();
//            continue;
          }
        } else {
          explodingBlocksIter.remove();
//          continue;
        }
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

  private boolean handleDestroyTargetMaterial(PlayerContext destroyingPlayerCtx, Block block) {
    Player player = destroyingPlayerCtx.getPlayer();
    TeamNew team = destroyingPlayerCtx.getTeam();
    TeamNew teamOfDestroyedBed;
    Block teamBedHead = team.getHeadTarget();

    if (block.getType() == Material.BED_BLOCK) {
      if (teamBedHead.equals(block) || teamBedHead.equals(UtilsNew.getBedNeighbor(block))) {
        player.sendMessage(ChatWriterNew.pluginMessage(
            ChatColor.RED + BedwarsRevol._l(player, "ingame.blocks.ownbeddestroy")));
        return false;
      }
      teamOfDestroyedBed = this.ctx.getTeamOfBed(block);
      if (teamOfDestroyedBed == null) {
        return false;
      }
      this.dropTargetBlock(block);
    } else {
      if (teamBedHead.equals(block)) {
        player.sendMessage(
            ChatWriterNew.pluginMessage(
                ChatColor.RED + BedwarsRevol._l(player, "ingame.blocks.ownbeddestroy")));
        return false;
      }
      teamOfDestroyedBed = this.ctx.getTeamOfBed(block);
      if (teamOfDestroyedBed == null) {
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
        String transSuffix = "others";
        if (teamOfDestroyedBed.isInTeam(aPlayer)) {
          String titleMsg = TitleWriterNew.pluginMessage(
              BedwarsRevol._l(aPlayer, "ingame.blocks.beddestroyedtitle"));
          aPlayer.sendTitle(titleMsg, null, 10, 70, 20);
          transSuffix = "team";
        }
        String chatMsg = ChatWriterNew.pluginMessage(
            BedwarsRevol._l(aPlayer, "ingame.blocks.beddestroyed." + transSuffix,
            ImmutableMap.of("team", teamOfDestroyedBed.getChatColor().toString()
                      + teamOfDestroyedBed.getName(),
                "player", UtilsNew.getPlayerWithTeamString(destroyingPlayerCtx))));
        aPlayer.sendMessage(chatMsg);
      }
    }

    this.ctx.broadcastSound(Sound.valueOf(BedwarsRevol.getInstance()
            .getStringConfig("bed-sound", "ENDERDRAGON_GROWL").toUpperCase()),
        30.0F, 10.0F);
    this.updateScoreboard();
    this.checkGameOver();
    return true;
  }

  private void updateScoreboard() {
    Scoreboard scoreboard = this.ctx.getScoreboard();
    Objective objectiveDisplay = scoreboard.getObjective("display");
    if (objectiveDisplay == null) {
      objectiveDisplay = scoreboard.registerNewObjective("display", "dummy");
    }
    objectiveDisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
    objectiveDisplay.setDisplayName(this.formatScoreboardTitle());
    for (TeamNew team : this.ctx.getTeams().values()) {
      scoreboard.resetScores(this.formatScoreboardTeam(team, false));
      scoreboard.resetScores(this.formatScoreboardTeam(team, true));
      Score score = objectiveDisplay.getScore(this.formatScoreboardTeam(team, team.isBedDestroyed()));
      score.setScore(team.getPlayers().size());
    }

    Objective objectiveHealth = scoreboard.getObjective("health");
    if (objectiveHealth == null) {
      objectiveHealth = scoreboard.registerNewObjective("health", "health");
    }
    objectiveHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
    objectiveHealth.setDisplayName(ChatColor.RED + "❤");

    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      playerCtx.getPlayer().setScoreboard(scoreboard);
    }
    // Bug SPIGOT-1725: need to force update to show actual health instead of 0
    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      Player player = playerCtx.getPlayer();
      if (player.getHealth() > 10) {
        player.setHealth(player.getHealth() - 0.01);
      } else {
        player.setHealth(player.getHealth() + 0.01);
      }
    }
  }

  private String formatScoreboardTitle() {
    String format = BedwarsRevol.getInstance()
        .getStringConfig("scoreboard.format-title", "&e$region$&f - $time$");
    format = format.replace("$region$", this.ctx.getRegion().getName());
    format = format.replace("$game$", this.ctx.getName());
    format = format.replace("$time$", UtilsNew.getFormattedTime(this.timeLeft));

    format = ChatColor.translateAlternateColorCodes('&', format);
    return UtilsNew.truncate(format, GameContext.MAX_OBJECTIVE_DISPLAY_LENGTH);
  }

  private String formatScoreboardTeam(TeamNew team, boolean bedDestroyed) {
    String format;
    if (bedDestroyed) {
      format = BedwarsRevol.getInstance().getStringConfig("scoreboard.format-bed-destroyed",
          "&c$status$ $team$");
    } else {
      format = BedwarsRevol.getInstance().getStringConfig(
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
    // Did all players leave the game?
    if (this.ctx.getPlayers().size() == 0) {
      this.runGameOver(null, true);
    }
    // Find teams with unharmed beds or players still operational
    Set<TeamNew> notLostTeams = new HashSet<>();
    for (TeamNew team : this.ctx.getTeams().values()) {
      if (team.isBedDestroyed()) {
        for (PlayerContext playerCtx : team.getPlayers()) {
          if (!playerCtx.getState().isSpectator()) {
            notLostTeams.add(team);
            break;
          }
        }
      } else if (team.getPlayers().size() > 0) {
        notLostTeams.add(team);
      }
    }
    // If only one team is left then we got the winner
    if (notLostTeams.size() == 1) {
      this.runGameOver(notLostTeams.iterator().next(), false);
    } else if (this.timeLeft <= 0) {
      this.runGameOver(null, false);
    }
  }

  private void runGameOver(TeamNew winner, boolean skipCountdown) {
//    BedwarsGameOverEvent overEvent = new BedwarsGameOverEvent(this.getGame(), winner);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(overEvent);
//
//    if (overEvent.isCancelled()) {
//      return;
//    }

    this.ctx.stopRunningTasks();

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

    this.ctx.setState(new GameStateEnding(this.ctx));
    GameOverTaskNew gameOver = new GameOverTaskNew(this.ctx, delay);
    if (skipCountdown) {
      gameOver.onGameEnds();
      return;
    }

    for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
      Player aPlayer = aPlayerCtx.getPlayer();
      if (aPlayer.isOnline()) {
        String title;
        String subtitle = "";
        String msg;
        if (winner == null) {
          msg = ChatWriterNew.pluginMessage(
              BedwarsRevol._l(aPlayer, "ingame.draw"));
          title = TitleWriterNew.pluginMessage(
              BedwarsRevol._l(aPlayer, "ingame.title.draw-title"));
        } else {
          int playTime = this.length - this.timeLeft;
          String formattedTime = UtilsNew.getFormattedTime(playTime);
          if (aPlayerCtx.getTeam() == winner) {
            title = TitleWriterNew.pluginMessage(
                BedwarsRevol._l(aPlayer, "ingame.title.won-title"));
          } else {
            title = TitleWriterNew.pluginMessage(
                BedwarsRevol._l(aPlayer, "ingame.title.lost-title"));
          }
          subtitle = TitleWriterNew.pluginMessage(
              BedwarsRevol._l(aPlayer, "ingame.title.won-subtitle",
                  ImmutableMap.of("team", winner.getChatColor() + winner.getDisplayName(),
                      "time", formattedTime)));
          msg = ChatWriterNew.pluginMessage(
              BedwarsRevol._l(aPlayer, "ingame.win",
                  ImmutableMap.of("team", winner.getChatColor() + winner.getDisplayName())));
        }
        aPlayer.sendTitle(title, subtitle, 0, 70, 20);
        aPlayer.sendMessage(msg);
      }
    }

    this.ctx.addRunningTask(gameOver.runTaskTimer(BedwarsRevol.getInstance(), 0L, 20L));
  }

  void startGame() {
//  public boolean startGame(CommandSender sender) {
//    if (this.state != GameState.WAITING) {
//      sender.sendMessage(
//          ChatWriter
//              .pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.startoutofwaiting")));
//      return false;
//    }
//
//    BedwarsGameStartEvent startEvent = new BedwarsGameStartEvent(this);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(startEvent);
//
//    if (startEvent.isCancelled()) {
//      return false;
//    }

//    this.isOver = false;
    for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
      Player player = aPlayerCtx.getPlayer();
      if (player.isOnline()) {
        player.sendMessage(
            ChatWriterNew.pluginMessage(
                ChatColor.GREEN + BedwarsRevol._l(player, "ingame.gamestarting")));
      }
    }

    // load shop categories again (if shop was changed)
    this.ctx.loadItemShopCategories();

    this.ctx.stopRunningTasks();
    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      playerCtx.clear(true);
    }
    this.preparePlayersAndTeams();
//    this.clearProtections();
//    this.moveFreePlayersToTeam();

//    this.cycle.onGameStart();
    this.startResourceSpawners();

    // Update world time before game starts
    this.updateTime();

    this.teleportPlayersToTeamSpawn();

//    this.state = GameState.RUNNING;

    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      PlayerState newPlayerState = new PlayerStatePlaying(playerCtx);
      playerCtx.setState(newPlayerState);
      newPlayerState.setGameMode();
//      this.setPlayerGameMode(playerCtx);
//      this.setPlayerVisibility(playerCtx);
    }

//    this.startActionBarRunnable();
//    this.updateScoreboard();

//    if (BedwarsRel.getInstance().getBooleanConfig("store-game-records", true)) {
//      this.displayRecord();
//    }

    this.startTimerCountdown();

//    if (BedwarsRevol.getInstance().getBooleanConfig("titles.map.enabled", false)) {
//      this.displayMapInfo();
//    }

    this.ctx.updateSigns();
    this.updateScoreboard();

//    if (BedwarsRel.getInstance().getBooleanConfig("global-messages", true)) {
//      for (Player aPlayer : BedwarsRel.getInstance().getServer().getOnlinePlayers()) {
//        aPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN
//            + BedwarsRel._l(aPlayer, "ingame.gamestarted",
//            ImmutableMap.of("game", this.getRegion().getName()))));
//      }
//      BedwarsRel.getInstance().getServer().getConsoleSender()
//          .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN
//              + BedwarsRel
//              ._l(BedwarsRel.getInstance().getServer().getConsoleSender(), "ingame.gamestarted",
//                  ImmutableMap.of("game", this.getRegion().getName()))));
//    }

//    BedwarsGameStartedEvent startedEvent = new BedwarsGameStartedEvent(this);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(startedEvent);
  }

  private void startTimerCountdown() {
    this.timeLeft = BedwarsRevol.getInstance().getMaxLength();
    this.length = BedwarsRevol.getInstance().getMaxLength();
    BukkitRunnable task = new BukkitRunnable() {

      @Override
      public void run() {
        GameStateRunning.this.updateScoreboardTimer();
        if (GameStateRunning.this.timeLeft == 0) {
//          GameStateRunning.this.isOver = true;
          GameStateRunning.this.checkGameOver();
          this.cancel();
          return;
        }
        GameStateRunning.this.timeLeft--;
      }
    };
    this.ctx.addRunningTask(task.runTaskTimer(BedwarsRevol.getInstance(), 0L, 20L));
  }

  private void updateScoreboardTimer() {
    Scoreboard scoreboard = this.ctx.getScoreboard();
    Objective obj = scoreboard.getObjective("display");
    if (obj == null) {
      obj = scoreboard.registerNewObjective("display", "dummy");
    }

    obj.setDisplayName(this.formatScoreboardTitle());

    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      playerCtx.getPlayer().setScoreboard(scoreboard);
    }
  }

  private void teleportPlayersToTeamSpawn() {
    for (TeamNew team : this.ctx.getTeams().values()) {
      for (PlayerContext playerCtx : team.getPlayers()) {
        Player player = playerCtx.getPlayer();
        player.setVelocity(new Vector(0, 0, 0));
        player.setFallDistance(0.0F);
        Location location = team.getSpawnLocation();
        playerCtx.setTeleportingIfWorldChange(location);
        player.teleport(location);
        playerCtx.setTeleporting(false);
//        if (!retainPlayerStorage && this.getPlayerStorage(player) != null) {
//          this.getPlayerStorage(player).clean(true);
//          this.getPlayerStorage(player).respawn();
//        }
      }
    }
  }

  private void startResourceSpawners() {
    for (ResourceSpawnerNew rs : this.ctx.getResourceSpawners()) {
      rs.setCtx(this.ctx);
      this.ctx.addRunningTask(BedwarsRevol.getInstance().getServer().getScheduler().runTaskTimer(
          BedwarsRevol.getInstance(), rs, Math.round((((double) rs.getInterval()) / 1000.0) * 20.0),
          Math.round((((double) rs.getInterval()) / 1000.0) * 20.0)));
    }
  }

  private void preparePlayersAndTeams() {
//    this.playingTeams.clear();
    for (TeamNew team : this.ctx.getTeams().values()) {
      team.getScoreboardTeam().setAllowFriendlyFire(
          BedwarsRevol.getInstance().getConfig().getBoolean("friendlyfire"));
//      if (team.getPlayers().size() == 0) {
//        this.dropTargetBlock(team.getHeadTarget());
//      } else {
//        this.playingTeams.add(team);
//      }
      team.reset();
      team.addChest(team.getChestLoc().getBlock());
    }
//    this.updateScoreboard();
    this.ctx.setDefaultUpgrades(
        (List<Map<String, Object>>) this.ctx.getConfig().getList("default-player-inventory"));
    List<Upgrade> playerUpgrades = new ArrayList<>();
    List<Upgrade> teamUpgrades = new ArrayList<>();
    for (Map<String, Object> item : this.ctx.getDefaultUpgrades()) {
      Map<String, Object> elem = (Map<String, Object>) item.get("upgrade");
      Upgrade upgrade = UpgradeRegistry.getUpgrade(
          (String) elem.get("type"), (int) elem.get("level"));
      if (upgrade instanceof UpgradeItem) {
        // Make a copy to run fixMeta on
        UpgradeItem temp = (UpgradeItem) upgrade.create(null, null, null);
        temp.setItem(MerchantCategory.fixMeta(
            ItemStack.deserialize((Map<String, Object>) item.get("item"))));
        upgrade = temp;
      }
      if (elem.containsKey("permanent")) {
        boolean permanent = (boolean) elem.get("permanent");
        upgrade.setPermanent(permanent);
      }
      if (elem.containsKey("multiple")) {
        boolean multiple = (boolean) elem.get("multiple");
        upgrade.setMultiple(multiple);
      }
      if (upgrade.getScope() == UpgradeScope.PLAYER) {
        playerUpgrades.add(upgrade);
      } else {
        teamUpgrades.add(upgrade);
      }
    }

    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      TeamNew team = playerCtx.getTeam();
      for (Upgrade upgrade : playerUpgrades) {
        playerCtx.addUpgrade(upgrade.create(this.ctx, team, playerCtx));
      }
      playerCtx.respawn();
    }
    for (TeamNew team : this.ctx.getTeams().values()) {
      for (Upgrade upgrade : teamUpgrades) {
        team.setUpgrade(upgrade.create(this.ctx, team, null));
      }
    }
  }

}
