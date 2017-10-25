package io.github.bedwarsrevolution.holo;

import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

/**
 * Original author: Kristoffer
 * Redesigned and implemented: maxos
 */
public class FloatingItem {
  // How often to update the list of players in radius
  private static final long PLAYERS_LIST_UPDATE_INTERVAL = 1000;

  private Location location, origLocation;
  private ArmorStand helmetStand;
  private List<ArmorStand> textStands = new ArrayList<>();
  private long lastPlayersInRadiusUpdate = 0;
  private List<Player> players;

  public FloatingItem(Location location) {
    this.location = location.clone();
    this.origLocation = location.clone();
  }

  public void init(ItemStack itemStack, boolean small, String... text) {
    this.helmetStand = (ArmorStand) this.location.getWorld().spawnEntity(
        this.location, EntityType.ARMOR_STAND);
    this.helmetStand.setGravity(false);
    this.helmetStand.setHelmet(itemStack);
    this.helmetStand.setVisible(false);
    this.helmetStand.setSmall(small);
    addText(text);
  }

  public void update(double dyFromOrigin, float yaw) {
    long now = System.currentTimeMillis();
    if (now > this.lastPlayersInRadiusUpdate + PLAYERS_LIST_UPDATE_INTERVAL) {
      this.lastPlayersInRadiusUpdate = now;
      this.players = new ArrayList<>();
      for (Entity entity : this.helmetStand.getNearbyEntities(100, 100, 100)) {
        if (entity instanceof Player) {
          this.players.add((Player) entity);
        }
      }
      this.move((this.origLocation.getY() + dyFromOrigin) - this.location.getY(), yaw);
      teleport();
    } else {
      this.move((this.origLocation.getY() + dyFromOrigin) - this.location.getY(), yaw);
    }
  }

  private void move(double dy, float yaw) {
    for (Player player : this.players) {
      if (!player.isOnline()) {
        continue;
      }
      WrapperPlayServerRelEntityMoveLook packet = new WrapperPlayServerRelEntityMoveLook();
      packet.setEntityID(this.helmetStand.getEntityId());
      packet.setDx(0);
      packet.setDy((this.origLocation.getY() + dy) - this.location.getY());
      packet.setDz(0);
      packet.setYaw(yaw);
      packet.setPitch(0);
      packet.setOnGround(false);
      packet.sendPacket(player);
    }
    this.location.setY(this.origLocation.getY() + dy);
    this.location.setYaw(yaw);
  }

  private void teleport() {
    for (Player player : this.players) {
      if (!player.isOnline()) {
        continue;
      }
      WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
      packet.setEntityID(this.helmetStand.getEntityId());
      packet.setX(this.location.getX());
      packet.setY(this.location.getY());
      packet.setZ(this.location.getZ());
      packet.setYaw(this.location.getYaw());
      packet.setPitch(0);
      packet.setOnGround(false);
      packet.sendPacket(player);
    }
  }

  private void addText(String... text) {
    List<String> lines = Arrays.asList(text);
    lines = Lists.reverse(lines);

    double y = 0.25D;

    for (String line : lines) {
      ArmorStand stand = (ArmorStand) this.location.getWorld().spawnEntity(
          this.location.clone().add(0, y, 0), EntityType.ARMOR_STAND);
      stand.setGravity(false);
      stand.setCustomName(line.replace('&', '§'));
      stand.setCustomNameVisible(true);
      stand.setVisible(false);
      y += 0.21D;

      this.textStands.add(stand);
    }
  }

  private void deleteText() {
    for (ArmorStand stand : this.textStands) {
      this.origLocation.getChunk().load(true);
      this.origLocation.clone().add(0, 1, 0).getChunk().load(true);
      stand.remove();
    }
    this.textStands.clear();
  }

  public void delete() {
    deleteText();
    if (this.helmetStand != null) {
      this.origLocation.getChunk().load(true);
      this.origLocation.clone().add(0, 1, 0).getChunk().load(true);
      this.helmetStand.remove();
    }
  }

  public void setText(int num, String text) {
    this.textStands.get(num).setCustomName(text.replace('&', '§'));
  }

}
