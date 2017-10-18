package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by {maxos} 2017
 */
public class GameStateStopped extends GameStateNew {
  private static final String TRANSLATION = "stopped";

  public GameStateStopped(GameContext ctx) {
    super(ctx);
  }

  @Override
  public void onEventCraft(CraftItemEvent event) {

  }

  @Override
  public void onEventEntityDamage(EntityDamageEvent event) {
  }

  @Override
  public void onEventDrop(PlayerDropItemEvent event) {

  }

  @Override
  public void onEventFly(PlayerToggleFlightEvent event) {

  }

  @Override
  public void onEventBowShot(EntityShootBowEvent event) {

  }

  @Override
  public void onEventInteractEntity(PlayerInteractEntityEvent event) {

  }

  @Override
  public void onEventInventoryClick(InventoryClickEvent event) {

  }

  @Override
  public void onEventPlayerInteract(PlayerInteractEvent event) {
  }

  @Override
  public void onEventPlayerRespawn(PlayerRespawnEvent event) {

  }

  @Override
  public void onEventPlayerQuit(PlayerQuitEvent event) {

  }

  @Override
  public void onEventPlayerBedEnter(PlayerBedEnterEvent event) {

  }

  @Override
  public void onEventPlayerChangeWorld(PlayerChangedWorldEvent event) {

  }

  @Override
  public void onEventInventoryOpen(InventoryOpenEvent event) {

  }

  @Override
  public void playerJoins(Player player) {
//    if (this.cycle instanceof BungeeGameCycle) {
//      ((BungeeGameCycle) this.cycle).sendBungeeMessage(p,
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(p, "errors.cantjoingame")));
//    } else {
      player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
          ._l(player, "errors.cantjoingame")));
//    }
  }

  @Override
  public void playerLeaves(PlayerContext playerCtx, boolean kicked) {
  }

  @Override
  public void onEventBlockBurn(BlockBurnEvent event) {
  }

  @Override
  public void onEventBlockFade(BlockFadeEvent event) {
  }

  @Override
  public void onEventBlockForm(BlockFormEvent event) {
  }

  @Override
  public void onEventBlockIgnite(BlockIgniteEvent event) {
  }

  @Override
  public void onEventBlockPlace(BlockPlaceEvent event) {
  }

  @Override
  public void onEventPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
  }

  @Override
  public void onEventCreatureSpawn(CreatureSpawnEvent event) {
  }

  @Override
  public void onEventEntityExplode(EntityExplodeEvent event) {
  }

  @Override
  public void onEventServerListPing(ServerListPingEvent event) {
    event.setMotd(motdReplacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("bungeecord.motds.stopped"))));
  }

  @Override
  public void onEventWeatherChange(WeatherChangeEvent event) {
  }

  @Override
  protected String getStatusKey() {
    return TRANSLATION;
  }
}
