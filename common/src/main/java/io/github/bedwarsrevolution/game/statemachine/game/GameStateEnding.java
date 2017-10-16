package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by {maxos} 2017
 */
public class GameStateEnding implements GameState {

  @Override
  public void onEventCraft(GameContext ctx, CraftItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventDamage(GameContext ctx, EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void addPlayer(GameContext gameContext, Player player) {

  }

  @Override
  public void onEventDrop(GameContext ctx, PlayerDropItemEvent event) {
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
    event.setCancelled(true);
  }

  @Override
  public void onEventInventoryClick(GameContext ctx, InventoryClickEvent event) {
    event.setCancelled(true);
  }

}
