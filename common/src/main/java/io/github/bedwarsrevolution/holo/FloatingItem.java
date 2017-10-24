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
 * <b>Idea from Hypixels Bedwars diamond and emerald generators</b>
 * @author Kristoffer
 */
public class FloatingItem {
  private Location location, origLocation;
  private ArmorStand helmetStand;
  private boolean goingDown;
  private List<ArmorStand> textStands = new ArrayList<>();

  public FloatingItem(Location location) {
    this.location = location.clone();
    this.origLocation = location.clone();
    this.goingDown = true;
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

  public void update() {
    if (this.helmetStand == null) {
      return;
    }
    if (this.goingDown) {
      this.location.setY(this.location.getY() + 0.01);
      this.location.setYaw(this.location.getYaw() - 7.5F);
    } else {
      this.location.setY(this.location.getY() - 0.01);
      this.location.setYaw(this.location.getYaw() + 7.5F);
    }
    if (this.location.getY() > (0.25 + this.origLocation.getY())) {
      this.goingDown = true;
    } else if (this.location.getY() < (-0.25 + this.origLocation.getY())) {
      this.goingDown = false;
    }
    this.teleport();
  }

  public void update(double dy, float yaw) {
//    this.location.setY(this.origLocation.getY() + dy);
//    this.location.setYaw(yaw);
//    this.teleport();
    for (Entity entity : this.helmetStand.getNearbyEntities(100, 100, 100)) {
      if (!(entity instanceof Player)) {
        continue;
      }
      Player player = (Player) entity;
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
    for (Entity entity : this.helmetStand.getNearbyEntities(100, 100, 100)) {
      if (!(entity instanceof Player)) {
        continue;
      }
      Player player = (Player) entity;
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
