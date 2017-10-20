package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockListenerNew extends BaseListenerNew {

  @EventHandler(ignoreCancelled = true)
  public void onBlockGrow(BlockGrowEvent event) {

    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameByLocation(event.getBlock().getLocation());
    if (ctx == null) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockEvent(BlockExplodeEvent event) {
    System.out.println(event);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    GameContext ctx;
    // TODO: confirm that player should never be null
//    if (player == null) {
//      Block block = event.getBlock();
//      if (block == null) {
//        return;
//      }
//      ctx = BedwarsRevol.getInstance().getGameManager().getGameByLocation(block.getLocation());
//      if (ctx == null) {
//        return;
//      }
//    }

    ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      Block block = event.getBlock();
      if (!(block.getState() instanceof Sign)) {
        return;
      }
      if (!player.hasPermission("bw.setup") || event.isCancelled()) {
        return;
      }
      ctx = BedwarsRevol.getInstance().getGameManager().getGameBySignLocation(block.getLocation());
      if (ctx == null) {
        return;
      }
      ctx.removeJoinSign(block.getLocation());
      return;
    }

    ctx.getState().onEventBlockBreak(event);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBurn(BlockBurnEvent event) {
    Block block = event.getBlock();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameByLocation(block.getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventBlockBurn(event);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockFade(BlockFadeEvent event) {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByLocation(event.getBlock().getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventBlockFade(event);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockForm(BlockFormEvent event) {
    if (event.getNewState().getType() != Material.SNOW) {
      return;
    }
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByLocation(event.getBlock().getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventBlockForm(event);
  }

  @EventHandler(ignoreCancelled = true)
  public void onIgnite(BlockIgniteEvent event) {
    if (event.getIgnitingBlock() == null && event.getIgnitingEntity() == null) {
      return;
    }
    GameContext ctx = null;
    if (event.getIgnitingBlock() == null) {
      if (event.getIgnitingEntity() instanceof Player) {
        ctx = BedwarsRevol.getInstance().getGameManager()
            .getGameOfPlayer((Player) event.getIgnitingEntity());
      } else {
        ctx = BedwarsRevol.getInstance().getGameManager()
            .getGameByLocation(event.getIgnitingEntity().getLocation());
      }
    } else {
      ctx = BedwarsRevol.getInstance().getGameManager()
          .getGameByLocation(event.getIgnitingBlock().getLocation());
    }
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventBlockIgnite(event);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventBlockPlace(event);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockSpread(BlockSpreadEvent event) {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
            .getGameByLocation(event.getBlock().getLocation());
    if (ctx == null) {
      return;
    }
    if (event.getNewState() == null || event.getSource() == null) {
      return;
    }
    ctx.getState().onEventBlockSpread(event);
  }

  @EventHandler(ignoreCancelled = true)
  public void onStructureGrow(StructureGrowEvent event) {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByLocation(event.getLocation());
    if (ctx == null) {
      return;
    }
    event.setCancelled(true);
  }

}
