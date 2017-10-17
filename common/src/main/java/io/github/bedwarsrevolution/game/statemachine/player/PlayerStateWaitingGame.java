package io.github.bedwarsrevolution.game.statemachine.player;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by {maxos} 2017
 */
public class PlayerStateWaitingGame extends PlayerState {

  @Override
  public void onDeath(PlayerContext playerCtx) {
    // TODO: "how did they do that?"... "scratching head"...
  }

  @Override
  public void onDamage(PlayerContext playerCtx, EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onDrop(PlayerContext playerCtx, PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onFly(PlayerContext playerCtx, PlayerToggleFlightEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onBowShot(PlayerContext playerCtx, EntityShootBowEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInteractEntity(PlayerContext playerCtx, PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInventoryClick(PlayerContext playerCtx, InventoryClickEvent event) {
    event.setCancelled(true);

    // TODO: maybe implement this (though it's rather pointless)
//    Inventory inv = event.getInventory();
//    ItemStack clickedStack = event.getCurrentItem();
//
//    event.setCancelled(true);
//    if (!inv.getTitle().equals(BedwarsRevol._l(playerCtx.getPlayer(), "lobby.chooseteam"))
//        || clickedStack == null
//        || clickedStack.getType() != Material.WOOL) {
//      return;
//    }
//
//    Wool wool = (Wool) clickedStack.getData();
//    GameContext gameCtx = playerCtx.getGameContext();
//    Team team = gameCtx.getTeamByDyeColor(wool.getColor());
//    if (team == null) {
//      return;
//    }
//    Player player = playerCtx.getPlayer();
//    gameCtx.playerJoinsTeam(playerCtx, team);
//    player.closeInventory();
  }

//  private void playerJoinsTeam(PlayerContext playerCtx, Team team) {
//    Player player = playerCtx.getPlayer();
//    if (team.getPlayers().size() >= team.getMaxPlayers()) {
//      player.sendMessage(ChatWriter.pluginMessage(
//          ChatColor.RED + BedwarsRevol._l(player, "errors.teamfull")));
//      return;
//    }
//
//    if (team.addPlayer(player)) {
//      // Team color chestplate
//      ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
//      LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
//      meta.setColor(team.getColor().getColor());
//      meta.setDisplayName(team.getChatColor() + team.getDisplayName());
//      chestplate.setItemMeta(meta);
//
//      player.getInventory().setChestplate(chestplate);
//      player.updateInventory();
//    } else {
//      player.sendMessage(ChatWriter.pluginMessage(
//          ChatColor.RED + BedwarsRevol._l(player, "errors.teamfull")));
//      return;
//    }
//
//    this.updateScoreboard();
//
//    if (this.isStartable() && this.getLobbyCountdown() == null) {
//      GameLobbyCountdown lobbyCountdown = new GameLobbyCountdown(this);
//      lobbyCountdown.runTaskTimer(BedwarsRel.getInstance(), 20L, 20L);
//      this.setLobbyCountdown(lobbyCountdown);
//    }
//
//    player
//        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel
//            ._l(player, "lobby.teamjoined",
//                ImmutableMap.of("team", team.getDisplayName() + ChatColor.GREEN))));
//  }

}
