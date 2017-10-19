package io.github.bedwarsrevolution.game.statemachine.player;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.DamageHolder;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.shop.Shop;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by {maxos} 2017
 */
public class PlayerStatePlaying extends PlayerState {

  public PlayerStatePlaying(PlayerContext playerCtx) {
    super(playerCtx);
  }

  @Override
  public void onDeath() {
//    if (!BedwarsRel.getInstance().getBooleanConfig("player-drops", false)) {
//      pde.getDrops().clear();
//    }

//    pde.setKeepInventory(
//            BedwarsRel.getInstance().getBooleanConfig("keep-inventory-on-death", false));

//    if (this.isEndGameRunning()) {
//      return;
//    }

//    BedwarsPlayerKilledEvent killedEvent =
//        new BedwarsPlayerKilledEvent(this.getGame(), player, killer);
//    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(killedEvent);

//    Iterator<SpecialItem> itemIterator = this.game.getSpecialItems().iterator();
//    while (itemIterator.hasNext()) {
//      SpecialItem item = itemIterator.next();
//      if (!(item instanceof RescuePlatform)) {
//        continue;
//      }
//
//      RescuePlatform rescue = (RescuePlatform) item;
//      if (rescue.getOwner().equals(player)) {
//        itemIterator.remove();
//      }
//    }

    TeamNew team = this.playerCtx.getTeam();
    DamageHolder damage = this.playerCtx.getLastDamagedBy();
    boolean damageCausedRecently = (damage != null && damage.wasCausedRecently());

//    PlayerStatistic diedPlayerStats = null;
//    PlayerStatistic killerPlayerStats = null;
//    if (BedwarsRevol.getInstance().statisticsEnabled()) {
//      diedPlayerStats = BedwarsRevol.getInstance().getPlayerStatisticManager().getStatistic(this.playerCtx.getPlayer());
//      boolean onlyOnBedDestroyed = BedwarsRevol.getInstance()
//          .getBooleanConfig("statistics.bed-destroyed-kills", false);
//      boolean teamBedDestroyed = team.isBedDestroyed();
//
//      if (!onlyOnBedDestroyed || teamBedDestroyed) {
//        diedPlayerStats.setCurrentDeaths(diedPlayerStats.getCurrentDeaths() + 1);
//        diedPlayerStats.setCurrentScore(diedPlayerStats.getCurrentScore() +
//            BedwarsRevol.getInstance().getIntConfig("statistics.scores.die", 0));
//      }
//
//      if (damageCausedRecently) {
//        if (!onlyOnBedDestroyed || teamBedDestroyed) {
//          killerPlayerStats = BedwarsRevol.getInstance().getPlayerStatisticManager()
//              .getStatistic(damage.getDamager());
//          if (killerPlayerStats != null) {
//            killerPlayerStats.setCurrentKills(killerPlayerStats.getCurrentKills() + 1);
//            killerPlayerStats.setCurrentScore(killerPlayerStats.getCurrentScore() +
//                BedwarsRevol.getInstance().getIntConfig("statistics.scores.kill", 10));
//          }
//        }
//      }
//
//      // dispatch reward commands directly
//      if (BedwarsRevol.getInstance().getBooleanConfig("rewards.enabled", false)
//          && damageCausedRecently
//          && (!onlyOnBedDestroyed || teamBedDestroyed)) {
//        List<String> commands = BedwarsRevol.getInstance().getConfig()
//            .getStringList("rewards.player-kill");
//        BedwarsRevol.getInstance().dispatchRewardCommands(commands,
//            ImmutableMap.of("{player}", damage.getDamager().getName(), "{score}",
//                String.valueOf(BedwarsRevol.getInstance()
//                    .getIntConfig("statistics.scores.kill", 10))));
//      }
//    }

    if (damageCausedRecently) {
      for (PlayerContext aPlayerCtx : playerCtx.getGameContext().getPlayers()) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(
              ChatWriterNew.pluginMessage(
                  ChatColor.GOLD + BedwarsRevol._l(aPlayer, "ingame.player.died", ImmutableMap
                      .of("player",
                          UtilsNew.getPlayerWithTeamString(aPlayer, team, ChatColor.GOLD)))));
        }
      }
      this.sendTeamDeadMessage(team);
      return;
    }

//    Team killerTeam = this.getGame().getPlayerTeam(killer);
//    if (killerTeam == null) {
//      for (Player aPlayer : this.getGame().getPlayers()) {
//        if (aPlayer.isOnline()) {
//          aPlayer.sendMessage(
//              ChatWriter.pluginMessage(
//                  ChatColor.GOLD + BedwarsRel._l(aPlayer, "ingame.player.died", ImmutableMap
//                      .of("player",
//                          Game.getPlayerWithTeamString(player, team, ChatColor.GOLD)))));
//        }
//      }
//      this.sendTeamDeadMessage(team);
//      return;
//    }

//    String hearts = "";
//    DecimalFormat format = new DecimalFormat("#");
//    double health = ((double) killer.getHealth()) / ((double) killer.getMaxHealth())
//        * ((double) killer.getHealthScale());
//    if (!BedwarsRel.getInstance().getBooleanConfig("hearts-in-halfs", true)) {
//      format = new DecimalFormat("#.#");
//      health = health / 2;
//    }
//
//    if (BedwarsRel.getInstance().getBooleanConfig("hearts-on-death", true)) {
//      hearts = "[" + ChatColor.RED + "\u2764" + format.format(health) + ChatColor.GOLD + "]";
//    }
//
//    for (Player aPlayer : this.getGame().getPlayers()) {
//      if (aPlayer.isOnline()) {
//        aPlayer.sendMessage(
//            ChatWriter.pluginMessage(ChatColor.GOLD + BedwarsRel._l(aPlayer, "ingame.player.killed",
//                ImmutableMap.of("killer",
//                    Game.getPlayerWithTeamString(killer, killerTeam, ChatColor.GOLD, hearts),
//                    "player",
//                    Game.getPlayerWithTeamString(player, team, ChatColor.GOLD)))));
//      }
//    }

//    if (team.isBedDestroyed()) {
//      killer.playSound(killer.getLocation(), SoundMachine.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"),
//          Float.valueOf("1.0"), Float.valueOf("1.0"));
//    }
//    this.sendTeamDeadMessage(team);

    if (this.playerCtx.getTeam().isBedDestroyed()) {
      PlayerStateSpectator newState = new PlayerStateSpectator(this.playerCtx);
      this.playerCtx.setState(newState);
    } else {
      PlayerStateWaitingRespawn newState = new PlayerStateWaitingRespawn(this.playerCtx);
      this.playerCtx.setState(newState);
      newState.runWaitingRespawn(true);
    }
  }

  @Override
  public void onDamage(EntityDamageEvent event) {
    if (this.playerCtx.isProtectd() && event.getCause() != DamageCause.VOID) {
      event.setCancelled(true);
      return;
    }

    if (event.getCause() == DamageCause.VOID) {
      event.setCancelled(true);
      this.onDeath();
      return;
    }

    Player damager = null;
    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent eventByentity = (EntityDamageByEntityEvent) event;
      EntityType damagerType = eventByentity.getDamager().getType();
      if (eventByentity.getDamager() instanceof Player) {
        damager = (Player) eventByentity.getDamager();
      } else if (damagerType == EntityType.ARROW) {
        Arrow arrow = (Arrow) eventByentity.getDamager();
        if (arrow.getShooter() instanceof Player) {
          damager = (Player) arrow.getShooter();
        }
      } else if (damagerType == EntityType.PRIMED_TNT) {
        TNTPrimed tnt = (TNTPrimed) eventByentity.getDamager();
        damager = (Player) tnt.getSource();
      }
    }

    if (damager != null) {
      this.playerCtx.setDamager(damager);
    }

    if (event.getDamage() >= this.playerCtx.getPlayer().getHealth()) {
      event.setCancelled(true);
      this.onDeath();
    }
  }

  @Override
  public void onDrop(PlayerDropItemEvent event) {
  }

  @Override
  public void onFly(PlayerToggleFlightEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onBowShot(EntityShootBowEvent event) {
    ItemStack bow = event.getBow();
    // Take away one arrow from player if shot from a bow with "infinity" enchantment
    if (bow.hasItemMeta() && bow.getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)) {
      Inventory inv = this.playerCtx.getPlayer().getInventory();
      int slot = inv.first(Material.ARROW);
      ItemStack stack = inv.getItem(slot);
      stack.setAmount(stack.getAmount() - 1);
    }
  }

  @Override
  public void onInteractEntity(PlayerInteractEntityEvent event) {
//    List<Material> preventClickEggs = Arrays.asList(
//        Material.MONSTER_EGG,
//        Material.MONSTER_EGGS,
//        Material.DRAGON_EGG);
//    if (BedwarsRevol.getInstance().getCurrentVersion().startsWith("v1_8")) {
//      if (preventClickEggs.contains(player.getItemInHand().getType())) {
//        event.setCancelled(true);
//        return;
//      }
//    } else {
//      PlayerInventory inv = player.getInventory();
//      if (preventClickEggs.contains(inv.getItemInMainHand().getType())
//          || preventClickEggs.contains(inv.getItemInOffHand().getType())) {
//        event.setCancelled(true);
//        return;
//      }
//    }

//    if (event.getRightClicked() != null
//        && event.getRightClicked().getType() != EntityType.VILLAGER) {
//      List<EntityType> preventClickTypes =
//          Arrays.asList(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND);
//
//      if (preventClickTypes.contains(event.getRightClicked().getType())) {
//        event.setCancelled(true);
//      }
//      return;
//    }

//    BedwarsOpenShopEvent openShopEvent =
//        new BedwarsOpenShopEvent(game, player, game.getShopCategories(), event.getRightClicked());
//    BedwarsRevol.getInstance().getServer().getPluginManager().callEvent(openShopEvent);
//    if (openShopEvent.isCancelled()) {
//      return;
//    }

    event.setCancelled(true);
    if (event.getRightClicked().getType() != EntityType.VILLAGER) {
      return;
    }
    Shop shop = this.playerCtx.getShop();
    shop.resetCurrentCategory();
    shop.render();
  }

  @Override
  public void onInventoryClick(InventoryClickEvent event) {
    event.setCancelled(true);
    ItemStack clickedStack = event.getCurrentItem();
    if (clickedStack == null) {
      return;
    }
    this.playerCtx.getShop().handleClick(event);
  }

  @Override
  public void setGameMode() {
    this.playerCtx.getPlayer().setGameMode(GameMode.SURVIVAL);
  }

  public void sendTeamDeadMessage(TeamNew team) {
//    if (deathTeam.getPlayers().size() == 1 && deathTeam.isBedDestroyed(this.getGame())) {
//      for (Player aPlayer : this.getGame().getPlayers()) {
//        if (aPlayer.isOnline()) {
//          aPlayer.sendMessage(
//              ChatWriter.pluginMessage(
//                  BedwarsRevol._l(aPlayer, "ingame.team-dead", ImmutableMap.of("team",
//                      deathTeam.getChatColor() + deathTeam.getDisplayName()))));
//        }
//      }
//    }
  }

}
