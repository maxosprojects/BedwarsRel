package io.github.bedwarsrevolution.game.statemachine.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.DamageHolder;
import io.github.bedwarsrevolution.listeners.InvisibilityPotionListenerNew;
import io.github.bedwarsrevolution.shop.Shop;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.NmsUtils;
import io.github.bedwarsrevolution.utils.SoundMachineNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by {maxos} 2017
 */
public class PlayerStatePlaying extends PlayerState {

  public PlayerStatePlaying(PlayerContext playerCtx) {
    super(playerCtx);
  }

  @Override
  public void onDeath(boolean byVoid) {
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

    DamageHolder damage = this.playerCtx.getLastDamagedBy();
    boolean damageCausedRecently = (damage != null && damage.wasCausedRecently());

    BedwarsRevol.getInstance().getInvisibilityPotionListener().unhideArmor(this.playerCtx);
    BedwarsRevol.getInstance().getBlockDisguiser().removeGogglesUser(this.playerCtx);
    this.playerCtx.died();

    for (PlayerContext aPlayerCtx : this.playerCtx.getGameContext().getPlayers()) {
      Player aPlayer = aPlayerCtx.getPlayer();
      if (byVoid) {
        if (damageCausedRecently) {
          this.tellPlayerKilled(aPlayer, damage.getDamager(), "knockedintovoid");
        } else {
          this.tellPlayerDied(aPlayer, "fellintovoid");
        }
      } else {
        if (damageCausedRecently) {
          this.tellPlayerKilled(aPlayer, damage.getDamager(), "killed");
        } else {
          this.tellPlayerDied(aPlayer, "died");
        }
      }
    }
//    this.sendTeamDeadMessage(team);

    if (damageCausedRecently) {
      Player damager = damage.getDamager();
      PlayerContext damagerCtx = this.playerCtx.getGameContext().getPlayerContext(damager);
      if (damagerCtx != null) {
        String resources = damagerCtx.getState().takeResources(this.playerCtx);
        if (!resources.isEmpty()) {
          damager.sendMessage(ChatWriterNew.pluginMessage(BedwarsRevol._l(
              damager, "ingame.player.gotresources",
              ImmutableMap.of("resources", resources,
                  "victim", this.playerCtx.getPlayer().getDisplayName()))));
        }
      }
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
      newState.moveToTopCenter();
    } else {
      PlayerStateWaitingRespawn newState = new PlayerStateWaitingRespawn(this.playerCtx);
      this.playerCtx.setState(newState);
      newState.runWaitingRespawn(true);
    }
  }

  @Override
  protected String takeResources(PlayerContext from) {
    PlayerInventory destInv = this.playerCtx.getPlayer().getInventory();
    Multimap<Material, ItemStack> map = ArrayListMultimap.create();
    Set<Material> types = from.getGameContext().getResourceSpawnerManager().getTypes();
    // Collect resources into map
    for (ItemStack item : from.getPlayer().getInventory().getContents()) {
      if (item != null && types.contains(item.getType())) {
        map.put(item.getType(), item);
      }
    }
    List<String> counts = new ArrayList<>();
    // Pack items into smallest possible number of itemstacks
    for (Collection<ItemStack> toPack : map.asMap().values()) {
      ItemStack[] stacks = packItems(toPack);
      destInv.addItem(stacks);
      ItemStack item = toPack.iterator().next();
      ItemMeta meta = item.getItemMeta();
      String name;
      if (meta.hasDisplayName()) {
        name = meta.getDisplayName();
      } else {
        name = StringUtils.capitalize(item.getType().toString().toLowerCase());
      }
      counts.add(BedwarsRevol._l(this.playerCtx.getPlayer(), "ingame.player.resource",
          ImmutableMap.of("resource", name, "amount", countItems(toPack).toString())));
    }
    return StringUtils.join(counts, ", ");
  }

  private Integer countItems(Collection<ItemStack> toPack) {
    int count = 0;
    for (ItemStack item : toPack) {
      count += item.getAmount();
    }
    return count;
  }

  ItemStack[] packItems(Collection<ItemStack> toPack) {
    List<ItemStack> res = new ArrayList<>();
    Iterator<ItemStack> iter = toPack.iterator();
    ItemStack item = iter.next();
    int maxSize = item.getMaxStackSize();
    res.add(item);
    while (iter.hasNext()) {
      ItemStack nextItem = iter.next();
      int left = item.getAmount() + nextItem.getAmount() - maxSize;
      if (left > 0) {
        item.setAmount(maxSize);
        // Make a new stack with leftovers
        item = item.clone();
        res.add(item);
        item.setAmount(left);
      } else {
        item.setAmount(item.getAmount() + nextItem.getAmount());
      }
    }
    return res.toArray(new ItemStack[0]);
  }

  private void tellPlayerDied(Player recipient, String translationKey) {
    String msg = ChatWriterNew.pluginMessage(
        BedwarsRevol._l(recipient, "ingame.player." + translationKey,
            ImmutableMap.of("player", UtilsNew.getPlayerWithTeamString(this.playerCtx))));
    recipient.sendMessage(msg);
  }

  private void tellPlayerKilled(Player recipient, Player killer, String translationKey) {
    PlayerContext killerCtx = this.playerCtx.getGameContext().getPlayerContext(killer);
    String killerName = killer.getDisplayName();
    if (killerCtx != null) {
      killerName = UtilsNew.getPlayerWithTeamString(killerCtx);
    }
    String msg = ChatWriterNew.pluginMessage(
        BedwarsRevol._l(recipient, "ingame.player." + translationKey,
            ImmutableMap.of("player", UtilsNew.getPlayerWithTeamString(this.playerCtx),
                "killer", killerName)));
    recipient.sendMessage(msg);
  }

  @Override
  public boolean onDamageToPlayer(final EntityDamageEvent event, final Player damager) {
    if (this.playerCtx.isProtectd() && event.getCause() != DamageCause.VOID) {
      event.setCancelled(true);
      return false;
    }

    if (event.getCause() == DamageCause.VOID) {
      event.setCancelled(true);
      this.onDeath(true);
      return true;
    }

    if (damager != null) {
      this.playerCtx.setDamager(damager);
    }

    if (event.getDamage() >= this.playerCtx.getPlayer().getHealth()) {
      event.setCancelled(true);
      this.onDeath(false);
      return true;
    }

    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent eventByEntity = (EntityDamageByEntityEvent) event;
      EntityType damagerType = eventByEntity.getDamager().getType();
      if (damager != null && damagerType == EntityType.ARROW) {
        new BukkitRunnable() {
          @Override
          public void run() {
            damager.playSound(damager.getLocation(), SoundMachineNew.get(
                "SUCCESSFUL_HIT", "ENTITY_ARROW_HIT_PLAYER"),
                Float.valueOf("1.0"), Float.valueOf("1.0"));
            double healthLeft = PlayerStatePlaying.this.playerCtx.getPlayer().getHealth();
            damager.sendMessage(ChatWriterNew.pluginMessage(
                BedwarsRevol._l(damager, "ingame.player.hit",
                    ImmutableMap.of("player", UtilsNew.getPlayerWithTeamString(
                        PlayerStatePlaying.this.playerCtx),
                        "health", String.valueOf(
                            UtilsNew.formatHealth(healthLeft))))));
          }
        }.runTaskLater(BedwarsRevol.getInstance(), 1L);
      } else if (damagerType == EntityType.ENDER_DRAGON) {
        Set<Player> friendlies = NmsUtils.getCustomEnderDragonFrendlies(
            (EnderDragon) eventByEntity.getDamager());
        if (friendlies != null && friendlies.contains(this.playerCtx.getPlayer())) {
          event.setCancelled(true);
        }
      }
    }
    return false;
  }

  @Override
  public boolean onDamageByPlayer(EntityDamageEvent event) {
    if (event.getEntityType() == EntityType.IRON_GOLEM) {
      IronGolem golem = (IronGolem) event.getEntity();
      if (this.playerCtx.getTeam().ownsGolem(golem)) {
        event.setCancelled(true);
      }
    } else {
      event.setCancelled(true);
    }
    return false;
  }

  @Override
  public void onDropItem(PlayerDropItemEvent event) {
    this.playerCtx.getShop().handleDrop(event);
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
    this.playerCtx.getShop().handleClick(event);
  }

  @Override
  public void setGameMode() {
    this.playerCtx.getPlayer().setGameMode(GameMode.SURVIVAL);
  }

//  public void sendTeamDeadMessage(TeamNew team) {
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
//  }

}
