package io.github.bedwarsrevolution.game;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsResourceSpawnEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.ItemStackParser;
import io.github.bedwarsrel.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@SerializableAs("ResourceSpawner")
public class ResourceSpawner implements Runnable, ConfigurationSerializable {

  private Game game;
  private int interval = 1000;
  private List<ItemStack> resources = new ArrayList<>();
  private Location location;
  private double spread = 1.0;
  private String name;
  private String team;

  public ResourceSpawner(Map<String, Object> deserialize) {
    this.location = Utils.locationDeserialize(deserialize.get("location"));
    this.name = deserialize.get("name").toString();

    if (!BedwarsRel.getInstance().getConfig().contains("resource." + this.name)) {
      throw new IllegalArgumentException("Can't find resource " + this.name + " in config.yml");
    }
    parseResourceConfig(this.name);

    if (deserialize.containsKey("interval")) {
      this.interval = Integer.parseInt(deserialize.get("interval").toString());
    } else {
      this.interval =
          BedwarsRel.getInstance().getIntConfig("resource." + name + ".spawn-interval", 1000);
    }

    if (deserialize.containsKey("spread")) {
      this.spread = Double.parseDouble(deserialize.get("spread").toString());
    } else {
      this.spread =
          BedwarsRel.getInstance().getConfig().getDouble("resource." + name + ".spread", 1.0);
    }

    if (deserialize.containsKey("team")) {
      this.team = deserialize.get("team").toString();
    }
  }

  public ResourceSpawner(Game game, String name, Location location) {
    this.game = game;
    this.name = name;
    if (!BedwarsRel.getInstance().getConfig().contains("resource." + this.name)) {
      throw new IllegalArgumentException("Can't find resource " + this.name + " in config.yml");
    }
    parseResourceConfig(this.name);
    this.interval =
        BedwarsRel.getInstance().getIntConfig("resource." + name + ".spawn-interval", 1000);
    this.location = location;
    this.spread =
        BedwarsRel.getInstance().getConfig().getDouble("resource." + name + ".spread", 1.0);
  }

  private void parseResourceConfig(String name) {
    List<Object> resourceList =
        (List<Object>) BedwarsRel.getInstance().getConfig().getList("resource." + name + ".item");
    for (Object resource : resourceList) {
      ItemStack itemStack = ItemStack.deserialize((Map<String, Object>) resource);
      if (itemStack != null) {
        this.resources.add(itemStack);
      }
    }
  }

  public static ItemStack createSpawnerStackByConfig(Object section) {
    ItemStackParser parser = new ItemStackParser(section);
    return parser.parse();
  }

  public boolean canContainItem(Inventory inv, ItemStack item) {
    int space = 0;
    for (ItemStack stack : inv.getContents()) {
      if (stack == null) {
        space += item.getMaxStackSize();
      } else if (stack.getType() == item.getType()
          && stack.getDurability() == item.getDurability()) {
        space += item.getMaxStackSize() - stack.getAmount();
      }
    }
    return space >= item.getAmount();
  }

  public void dropItem(Location dropLocation, ItemStack itemStack) {
    Item item = this.game.getRegion().getWorld().dropItemNaturally(dropLocation, itemStack);
    item.setPickupDelay(0);
    if (this.spread != 1.0) {
      item.setVelocity(item.getVelocity().multiply(this.spread));
    }
  }

  public boolean isOfTeam(String teamName) {
    return team != null && team.equals(teamName);
  }

  @Override
  public void run() {
    Location dropLocation = this.location.clone();
    for (ItemStack itemStack : this.resources) {
      ItemStack item = itemStack.clone();

      BedwarsResourceSpawnEvent resourceSpawnEvent =
          new BedwarsResourceSpawnEvent(this.game, this.location, item);
      BedwarsRel.getInstance().getServer().getPluginManager().callEvent(resourceSpawnEvent);

      if (resourceSpawnEvent.isCancelled()) {
        return;
      }

      item = resourceSpawnEvent.getResource();

      if (BedwarsRel.getInstance().getBooleanConfig("spawn-resources-in-chest", true)) {
        BlockState blockState = dropLocation.getBlock().getState();
        if (blockState instanceof Chest) {
          Chest chest = (Chest) blockState;
          if (canContainItem(chest.getInventory(), item)) {
            chest.getInventory().addItem(item);
            continue;
          } else {
            dropLocation.setY(dropLocation.getY() + 1);
          }
        }
      }
      dropItem(dropLocation, item);
    }
  }

  @Override
  public Map<String, Object> serialize() {
    HashMap<String, Object> rs = new HashMap<>();
    rs.put("location", Utils.locationSerialize(this.location));
    rs.put("name", this.name);
    rs.put("team", this.team);
    return rs;
  }

}
