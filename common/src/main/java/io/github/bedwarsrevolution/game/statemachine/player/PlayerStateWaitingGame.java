package io.github.bedwarsrevolution.game.statemachine.player;

import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.GameMode;
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

  public PlayerStateWaitingGame(PlayerContext playerCtx) {
    super(playerCtx);
  }

  @Override
  public void onDeath() {
    // TODO: "how did they do that?"... "scratching head"...
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
    event.setCancelled(true);
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
    event.setCancelled(true);

    // TODO: maybe implement this (though it's rather pointless)
//    Inventory inv = event.getInventory();
//    ItemStack clickedStack = event.getCurrentItem();
//
//    event.setCancelled(true);
//    if (!inv.getTitle().equals(BedwarsRevol._l(this.playerCtx.getPlayer(), "lobby.chooseteam"))
//        || clickedStack == null
//        || clickedStack.getType() != Material.WOOL) {
//      return;
//    }
//
//    Wool wool = (Wool) clickedStack.getData();
//    GameContext gameCtx = this.playerCtx.getGameContext();
//    Team team = gameCtx.getTeamByDyeColor(wool.getColor());
//    if (team == null) {
//      return;
//    }
//    Player player = this.playerCtx.getPlayer();
//    gameCtx.playerJoinsTeam(this.playerCtx, team);
//    player.closeInventory();
  }

  @Override
  public void setGameMode() {
    Integer gameMode = BedwarsRevol.getInstance().getIntConfig("lobby-gamemode", 0);
    this.playerCtx.getPlayer().setGameMode(GameMode.values()[gameMode]);
  }

//  private void playerJoinsTeam(PlayerContext playerCtx, Team team) {
//    Player player = this.playerCtx.getPlayer();
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
