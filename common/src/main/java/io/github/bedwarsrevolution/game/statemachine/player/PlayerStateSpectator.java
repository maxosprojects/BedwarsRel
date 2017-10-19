package io.github.bedwarsrevolution.game.statemachine.player;

import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by {maxos} 2017
 */
public class PlayerStateSpectator extends PlayerState {
  public PlayerStateSpectator(PlayerContext playerCtx) {
    super(playerCtx);
  }

  @Override
  public void onDeath() {
  }

  @Override
  public void onDamage(EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onDrop(PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onFly(PlayerToggleFlightEvent event) {
    event.setCancelled(false);
  }

  @Override
  public void onBowShot(EntityShootBowEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInteractEntity(PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInventoryClick(InventoryClickEvent event) {
//    if (game.isSpectator(player)
//        || (game.getCycle() instanceof BungeeGameCycle && game.getCycle().isEndGameRunning()
//        && BedwarsRel.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))) {

    ItemStack clickedStack = event.getCurrentItem();
    if (clickedStack == null) {
      return;
    }

    event.setCancelled(true);

    if (!event.getInventory().getName().equals(
        BedwarsRevol._l(this.playerCtx.getPlayer(), "ingame.spectator"))) {
      return;
    }

    switch (clickedStack.getType()) {
      case SKULL_ITEM:
        SkullMeta meta = (SkullMeta) clickedStack.getItemMeta();
        Player teleportTo = BedwarsRevol.getInstance().getServer().getPlayer(meta.getOwner());
        if (this.playerCtx.getGameContext().getPlayerContext(teleportTo) == null) {
          return;
        }
        this.playerCtx.getPlayer().teleport(teleportTo);
        this.playerCtx.getPlayer().closeInventory();
        break;
      case SLIME_BALL:
        // TODO: implement what was in Game.playerLeave
        // this.playerCtx.leave(false);
      case COMPASS:
        // TODO: implement what was in game.openSpectatorCompass();
        // this.playerCtx.openSpectatorCompass(false);
    }
  }

  @Override
  public void leave(boolean kicked) {
    // Overrides default behavior to not let anyone know
  }

  @Override
  public boolean isSpectator() {
    return true;
  }

  @Override
  public void setGameMode() {
//    if (this.playerCtx.getState().isSpectator()) {
//        && !(this.getCycle() instanceof BungeeGameCycle && this.getCycle().isEndGameRunning()
//        && BedwarsRel.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))) {
    Player player = this.playerCtx.getPlayer();
    player.setAllowFlight(true);
    player.setFlying(true);
    player.setGameMode(GameMode.SPECTATOR);
  }

}
