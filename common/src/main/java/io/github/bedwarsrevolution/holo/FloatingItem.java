package io.github.bedwarsrevolution.holo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
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

  /**
   * Constructs a new floating item and adds it to the items list
   *
   * @param location The location to spawn item at
   */
  public FloatingItem(Location location) {
    this.location = location.clone();
    this.origLocation = location.clone();
    this.goingDown = true;
  }

  /**
   * Spawns the floating item with the given text and item type
   *
   * @param itemStack The itemstack
   * @param big Whether the item should be big or not
   */
  public void spawn(ItemStack itemStack, boolean big, String... text) {
    this.helmetStand = (ArmorStand) this.location.getWorld().spawnEntity(
        this.location, EntityType.ARMOR_STAND);
    this.helmetStand.setGravity(false);
    this.helmetStand.setHelmet(itemStack);
    this.helmetStand.setVisible(false);
    this.helmetStand.setSmall(!big);
    addText(text);
  }

  /**
   * Updates the floating item
   */
  public void update() {
    if (this.helmetStand == null) {
      return;
    }
    Location currLoc = this.helmetStand.getLocation();
    if (this.goingDown) {
      currLoc.subtract(0, 0.01, 0);
      currLoc.setYaw((currLoc.getYaw() - 7.5F));
    } else {
      currLoc.add(0, 0.01, 0);
      currLoc.setYaw((currLoc.getYaw() + 7.5F));
    }
    this.helmetStand.teleport(currLoc);
    if (currLoc.getY() > (0.25 + this.origLocation.getY())) {
      this.goingDown = true;
    } else if (currLoc.getY() < (-0.25 + this.origLocation.getY())) {
      this.goingDown = false;    }
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

  /**
   * Deletes all text that the floating item has
   */
  public void deleteAllText() {
    for (ArmorStand stand : this.textStands) {
      this.origLocation.getChunk().load(true);
      this.origLocation.clone().add(0, 1, 0).getChunk().load(true);
      stand.remove();
    }
    this.textStands.clear();
  }

  /**
   * Deletes this floating item
   */
  public void delete() {
    deleteAllText();
    if (this.helmetStand != null) {
      this.origLocation.getChunk().load(true);
      this.origLocation.clone().add(0, 1, 0).getChunk().load(true);
      this.helmetStand.remove();
    }
  }

}
