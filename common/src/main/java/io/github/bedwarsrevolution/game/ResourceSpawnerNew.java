package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.shop.ItemStackParser;
import io.github.bedwarsrevolution.utils.UtilsNew;
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
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
@SerializableAs("ResourceSpawner")
public class ResourceSpawnerNew implements Runnable, ConfigurationSerializable {
  private GameContext ctx;
  private int interval = 1000;
  private List<ItemStack> resources = new ArrayList<>();
  private Location location;
  private double spread = 1.0;
  private String name;
  private String team;
  private BukkitTask task;

  public ResourceSpawnerNew(Map<String, Object> deserialize) {
    this.location = UtilsNew.locationDeserialize(deserialize.get("location"));
    this.name = deserialize.get("name").toString();

    if (!BedwarsRevol.getInstance().getConfig().contains("resource." + this.name)) {
      throw new IllegalArgumentException("Can't find resource " + this.name + " in config.yml");
    }
    parseResourceConfig(this.name);

    if (deserialize.containsKey("interval")) {
      this.interval = Integer.parseInt(deserialize.get("interval").toString());
    } else {
      this.interval = BedwarsRevol.getInstance()
          .getIntConfig("resource." + name + ".spawn-interval", 1000);
    }

    if (deserialize.containsKey("spread")) {
      this.spread = Double.parseDouble(deserialize.get("spread").toString());
    } else {
      this.spread = BedwarsRevol.getInstance().getConfig()
          .getDouble("resource." + name + ".spread", 1.0);
    }

    if (deserialize.containsKey("team")) {
      this.team = deserialize.get("team").toString();
    }
  }

  public ResourceSpawnerNew(GameContext ctx, String name, Location location) {
    this.ctx = ctx;
    this.name = name;
    if (!BedwarsRevol.getInstance().getConfig().contains("resource." + this.name)) {
      throw new IllegalArgumentException("Can't find resource " + this.name + " in config.yml");
    }
    parseResourceConfig(this.name);
    this.interval = BedwarsRevol.getInstance()
        .getIntConfig("resource." + name + ".spawn-interval", 1000);
    this.location = location;
    this.spread = BedwarsRevol.getInstance().getConfig()
        .getDouble("resource." + name + ".spread", 1.0);
  }

  private void parseResourceConfig(String name) {
    List<Object> resourceList =
        (List<Object>) BedwarsRevol.getInstance().getConfig().getList("resource." + name + ".item");
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
    Item item = this.ctx.getRegion().getWorld().dropItemNaturally(dropLocation, itemStack);
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

//      BedwarsResourceSpawnEvent resourceSpawnEvent =
//          new BedwarsResourceSpawnEvent(this.ctx, this.location, item);
//      BedwarsRel.getInstance().getServer().getPluginManager().callEvent(resourceSpawnEvent);
//
//      if (resourceSpawnEvent.isCancelled()) {
//        return;
//      }
//
//      item = resourceSpawnEvent.getResource();

      if (BedwarsRevol.getInstance().getBooleanConfig("spawn-resources-in-chest", true)) {
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
    rs.put("location", UtilsNew.locationSerialize(this.location));
    rs.put("name", this.name);
    rs.put("team", this.team);
    return rs;
  }

  public void restart(int intrvl) {
    if (this.task != null) {
      try {
        this.task.cancel();
      } catch (Exception ex) {
        // Ignore
      }
    }
    long ticks = Math.round((((double) intrvl) / 1000.0) * 20.0);
    this.task = BedwarsRevol.getInstance().getServer().getScheduler()
        .runTaskTimer(BedwarsRevol.getInstance(), this, ticks, ticks);
    this.ctx.addRunningTask(this.task);
  }
}
