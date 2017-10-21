package io.github.bedwarsrevolution.listeners;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.MetadataValue;

public class EntityListenerNew extends BaseListenerNew {

//  @EventHandler(priority = EventPriority.HIGHEST)
//  public void onEntityDamage(EntityDamageEvent event) {
//    List<EntityType> canDamageTypes = new ArrayList<>();
//    canDamageTypes.add(EntityType.PLAYER);
//    if (BedwarsRevol.getInstance().getServer().getPluginManager().isPluginEnabled("AntiAura")
//        || BedwarsRevol.getInstance().getServer().getPluginManager().isPluginEnabled("AAC")) {
//      canDamageTypes.add(EntityType.SQUID);
//    }
//    if (canDamageTypes.contains(event.getEntityType())) {
//      return;
//    }
//    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
//        .getGameByLocation(event.getEntity().getLocation());
//    if (ctx == null) {
//      return;
//    }
//
//    if (game.getState() == GameState.STOPPED) {
//      return;
//    }
//
//    event.setCancelled(true);
//  }

//  @EventHandler(priority = EventPriority.HIGHEST)
//  public void onEntityDamageByEntity(EntityDamageByEntityEvent ede) {
//    List<EntityType> canDamageTypes = new ArrayList<EntityType>();
//    canDamageTypes.add(EntityType.PLAYER);
//
//    if (BedwarsRel.getInstance().getServer().getPluginManager().isPluginEnabled("AntiAura")
//        || BedwarsRel.getInstance().getServer().getPluginManager().isPluginEnabled("AAC")) {
//      canDamageTypes.add(EntityType.SQUID);
//    }
//
//    if (canDamageTypes.contains(ede.getEntityType())) {
//      return;
//    }
//
//    Game game =
//        BedwarsRel.getInstance().getGameManager().getGameByLocation(ede.getEntity().getLocation());
//    if (game == null) {
//      return;
//    }
//
//    if (game.getState() == GameState.STOPPED) {
//      return;
//    }
//
//    ede.setCancelled(true);
//  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (BedwarsRevol.getInstance().getGameManager() == null) {
      return;
    }
    if (event.getLocation() == null) {
      return;
    }
    if (event.getLocation().getWorld() == null) {
      return;
    }
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByLocation(event.getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventCreatureSpawn(event);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityExplosionPrime(ExplosionPrimeEvent event) {
    if (event.getEntity() == null) {
      return;
    }
    if (event.getEntity().getWorld() == null) {
      return;
    }
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByLocation(event.getEntity().getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventExplosionPrime(event);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityExplode(EntityExplodeEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (event.getEntity() == null) {
      return;
    }
    if (event.getEntity().getWorld() == null) {
      return;
    }
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByLocation(event.getEntity().getLocation());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventEntityExplode(event);
  }

//  @EventHandler(priority = EventPriority.HIGH)
//  public void onInteractEntity(PlayerInteractAtEntityEvent event) {
//    if (event.getRightClicked() == null) {
//      return;
//    }
//
//    Entity entity = event.getRightClicked();
//    Player player = event.getPlayer();
//    if (!player.hasMetadata("bw-addteamjoin")) {
//      if (!(entity instanceof LivingEntity)) {
//        return;
//      }
//
//      LivingEntity livEntity = (LivingEntity) entity;
//      Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
//      if (game == null) {
//        return;
//      }
//
//      if (game.getState() != GameState.WAITING) {
//        return;
//      }
//
//      Team team = game.getTeam(ChatColor.stripColor(livEntity.getCustomName()));
//      if (team == null) {
//        return;
//      }
//
//      game.playerJoinTeam(player, team);
//      event.setCancelled(true);
//      return;
//    }
//
//    List<MetadataValue> values = player.getMetadata("bw-addteamjoin");
//    if (values == null || values.size() == 0) {
//      return;
//    }
//
//    event.setCancelled(true);
//    TeamJoinMetaDataValue value = (TeamJoinMetaDataValue) values.get(0);
//    if (!((boolean) value.value())) {
//      return;
//    }
//
//    if (!(entity instanceof LivingEntity)) {
//      player.sendMessage(
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
//              ._l(player, "errors.entitynotcompatible")));
//      return;
//    }
//
//    LivingEntity living = (LivingEntity) entity;
//    living.setRemoveWhenFarAway(false);
//    living.setCanPickupItems(false);
//    living.setCustomName(value.getTeam().getChatColor() + value.getTeam().getDisplayName());
//    living.setCustomNameVisible(
//        BedwarsRel.getInstance().getBooleanConfig("jointeam-entity.show-name", true));
//
//    if (living.getType().equals(EntityType.valueOf("ARMOR_STAND"))) {
//      Utils.equipArmorStand(living, value.getTeam());
//    }
//
//    player.removeMetadata("bw-addteamjoin", BedwarsRel.getInstance());
//    player.sendMessage(ChatWriter
//        .pluginMessage(
//            ChatColor.GREEN + BedwarsRel._l(player, "success.teamjoinadded", ImmutableMap.of("team",
//                value.getTeam().getChatColor() + value.getTeam().getDisplayName()
//                    + ChatColor.GREEN))));
//  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onRegainHealth(EntityRegainHealthEvent event) {
    if (event.getEntityType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventRegainHealth(event);
  }
}
