package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Bed;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

public class RegionNew {
  public final static int CHUNK_SIZE = 16;
  private HashMap<Block, Byte> brokenBlockData = null;
  private HashMap<Block, BlockFace> brokenBlockFace = null;
  private HashMap<Block, Boolean> brokenBlockPower = null;
  private HashMap<Block, Integer> brokendBlockTypes = null;
  private List<Block> brokenBlocks = null;
  private List<Inventory> inventories = null;
  private Location maxCorner = null;
  private Location minCorner = null;
  private String name = null;
  private List<Block> placedBlocks = null;
  private List<Block> placedUnbreakableBlocks = null;
  private List<Entity> removingEntities = null;
  private World world = null;

  public RegionNew(Location pos1, Location pos2, String name) {
    if (pos1 == null || pos2 == null) {
      return;
    }

    if (!pos1.getWorld().getName().equals(pos2.getWorld().getName())) {
      return;
    }

    this.world = pos1.getWorld();
    this.setMinMax(pos1, pos2);
    this.placedBlocks = new ArrayList<>();
    this.brokenBlocks = new ArrayList<>();
    this.brokendBlockTypes = new HashMap<>();
    this.brokenBlockData = new HashMap<>();
    this.brokenBlockFace = new HashMap<>();
    this.placedUnbreakableBlocks = new ArrayList<>();
    this.brokenBlockPower = new HashMap<>();
    this.inventories = new ArrayList<>();
    this.removingEntities = new ArrayList<>();

    this.name = name;
  }

  public RegionNew(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
    this(new Location(w, x1, y1, z1), new Location(w, x2, y2, z2), w.getName());
  }

  public void addBrokenBlock(Block brokenBlock) {
    if (brokenBlock.getState().getData() instanceof Directional) {
      this.brokenBlockFace.put(brokenBlock,
          ((Directional) brokenBlock.getState().getData()).getFacing());
    }

    this.brokendBlockTypes.put(brokenBlock, brokenBlock.getTypeId());
    this.brokenBlockData.put(brokenBlock, brokenBlock.getData());

    if (brokenBlock.getState().getData() instanceof Redstone) {
      this.brokenBlockPower.put(brokenBlock, ((Redstone) brokenBlock.getState().getData()).isPowered());
    }

    this.brokenBlocks.add(brokenBlock);
  }

  public void addInventory(Inventory inventory) {
    this.inventories.add(inventory);
  }

  @SuppressWarnings("deprecation")
  public void addPlacedBlock(Block placeBlock, BlockState replacedBlock) {
    this.placedBlocks.add(placeBlock);
    if (replacedBlock != null) {
      if (replacedBlock.getData() instanceof Directional) {
        this.brokenBlockFace.put(replacedBlock.getBlock(),
            ((Directional) replacedBlock.getData()).getFacing());
      }

      this.brokendBlockTypes.put(replacedBlock.getBlock(), replacedBlock.getTypeId());
      this.brokenBlockData.put(replacedBlock.getBlock(), replacedBlock.getData().getData());

      this.brokenBlocks.add(replacedBlock.getBlock());
    }
  }

  @SuppressWarnings("deprecation")
  public void addPlacedUnbreakableBlock(Block placed, BlockState replaced) {
    this.placedUnbreakableBlocks.add(placed);
    if (replaced != null) {
      if (replaced.getData() instanceof Directional) {
        this.brokenBlockFace.put(replaced.getBlock(),
            ((Directional) replaced.getData()).getFacing());
      }

      this.brokendBlockTypes.put(replaced.getBlock(), replaced.getTypeId());
      this.brokenBlockData.put(replaced.getBlock(), replaced.getData().getData());
      this.brokenBlocks.add(replaced.getBlock());

      if (replaced.getData() instanceof Redstone) {
        this.brokenBlockPower.put(placed, ((Redstone) replaced.getData()).isPowered());
      }
    }
  }

  public void addRemovingEntity(Entity removing) {
    this.removingEntities.add(removing);
  }

  public boolean check() {
    return (this.minCorner != null && this.maxCorner != null && this.world != null);
  }

  public boolean chunkIsInRegion(Chunk chunk) {
    return (chunk.getX() >= this.minCorner.getX() && chunk.getX() <= this.maxCorner.getX()
        && chunk.getZ() >= this.minCorner.getZ() && chunk.getZ() <= this.maxCorner.getZ());
  }

  public boolean chunkIsInRegion(double x, double z) {
    return (x >= this.minCorner.getX() && x <= this.maxCorner.getX() && z >= this.minCorner.getZ()
        && z <= this.maxCorner.getZ());
  }

  public List<Inventory> getInventories() {
    return this.inventories;
  }

  private Location getMaximumCorner(Location pos1, Location pos2) {
    return new Location(this.world, Math.max(pos1.getBlockX(), pos2.getBlockX()),
        Math.max(pos1.getBlockY(), pos2.getBlockY()), Math.max(pos1.getBlockZ(), pos2.getBlockZ()));
  }

  private Location getMinimumCorner(Location pos1, Location pos2) {
    return new Location(this.world, Math.min(pos1.getBlockX(), pos2.getBlockX()),
        Math.min(pos1.getBlockY(), pos2.getBlockY()), Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
  }

  public String getName() {
    if (this.name == null) {
      this.name = this.world.getName();
    }

    return this.name;
  }

  public World getWorld() {
    return this.minCorner.getWorld();
  }

  public boolean isInRegion(Location location) {
    if (!location.getWorld().equals(this.world)) {
      return false;
    }

    return (location.getBlockX() >= this.minCorner.getBlockX()
        && location.getBlockX() <= this.maxCorner.getBlockX()
        && location.getBlockY() >= this.minCorner.getBlockY()
        && location.getBlockY() <= this.maxCorner.getBlockY()
        && location.getBlockZ() >= this.minCorner.getBlockZ()
        && location.getBlockZ() <= this.maxCorner.getBlockZ());
  }

  public boolean isPlacedBlock(Block block) {
    return this.placedBlocks.contains(block);
  }

  public boolean isPlacedUnbreakableBlock(Block clickedBlock) {
    return this.placedUnbreakableBlocks.contains(clickedBlock);
  }

  public void loadChunks() {
    int minX = (int) Math.floor(this.minCorner.getX());
    int maxX = (int) Math.ceil(this.maxCorner.getX());
    int minZ = (int) Math.floor(this.minCorner.getZ());
    int maxZ = (int) Math.ceil(this.maxCorner.getZ());

    for (int x = minX; x <= maxX; x += RegionNew.CHUNK_SIZE) {
      for (int z = minZ; z <= maxZ; z += RegionNew.CHUNK_SIZE) {
        Chunk chunk = this.world.getChunkAt(x, z);
        if (!chunk.isLoaded()) {
          chunk.load();
        }
      }
    }
  }

  public void removePlacedBlock(Block block) {
    this.placedBlocks.remove(block);
  }

  public void removePlacedUnbreakableBlock(Block block) {
    this.placedUnbreakableBlocks.remove(block);
  }

  public void removeRemovingEntity(Entity removing) {
    this.removingEntities.remove(removing);
  }

  @SuppressWarnings("deprecation")
  public void reset(GameContext ctx) {
    this.loadChunks();

    for (Inventory inventory : this.inventories) {
      inventory.clear();
    }

    for (Block placed : this.placedBlocks) {
      Block blockInWorld = this.world.getBlockAt(placed.getLocation());
      if (blockInWorld.getType() == Material.AIR) {
        continue;
      }

      if (blockInWorld.equals(placed)) {
        blockInWorld.setType(Material.AIR);
      }
    }

    this.placedBlocks.clear();

    for (Block placed : this.placedUnbreakableBlocks) {
      Block blockInWorld = this.world.getBlockAt(placed.getLocation());
      if (blockInWorld.getType() == Material.AIR) {
        continue;
      }

      if (blockInWorld.getLocation().equals(placed.getLocation())) {
        blockInWorld.setType(Material.AIR);
      }
    }

    this.placedUnbreakableBlocks.clear();

    for (Block block : this.brokenBlocks) {
      Block theBlock = this.getWorld().getBlockAt(block.getLocation());
      theBlock.setTypeId(this.brokendBlockTypes.get(block));
      theBlock.setData(this.brokenBlockData.get(block));

      if (this.brokenBlockFace.containsKey(theBlock)) {
        MaterialData data = theBlock.getState().getData();
        if (data instanceof Directional) {
          ((Directional) data).setFacingDirection(this.brokenBlockFace.get(block));
          theBlock.getState().setData(data);
        }
      }

      if (theBlock.getState().getData() instanceof Lever) {
        Lever attach = (Lever) theBlock.getState().getData();
        BlockState supportState = theBlock.getState();
        BlockState initalState = theBlock.getState();
        attach.setPowered(this.brokenBlockPower.get(block));
        theBlock.getState().setData(attach);

        supportState.setType(Material.AIR);
        supportState.update(true, false);
        initalState.update(true);
      } else {
        theBlock.getState().update(true, true);
      }
    }

    this.brokenBlocks.clear();

    Material targetMaterial = ctx.getTargetMaterial();
    for (TeamNew team : ctx.getTeams().values()) {
      if (team.getHeadTarget() == null) {
        continue;
      }

      if ((targetMaterial.equals(Material.BED_BLOCK) || targetMaterial.equals(Material.BED))
          && team.getFeetTarget() != null) {
        Block blockHead = this.world.getBlockAt(team.getHeadTarget().getLocation());
        Block blockFeed = this.world.getBlockAt(team.getFeetTarget().getLocation());
        BlockState headState = blockHead.getState();
        BlockState feedState = blockFeed.getState();

        headState.setType(Material.BED_BLOCK);
        feedState.setType(Material.BED_BLOCK);
        headState.setRawData((byte) 0x0);
        feedState.setRawData((byte) 0x8);
        feedState.update(true, false);
        headState.update(true, false);

        Bed bedHead = (Bed) headState.getData();
        bedHead.setHeadOfBed(true);
        bedHead.setFacingDirection(blockHead.getFace(blockFeed).getOppositeFace());

        Bed bedFeed = (Bed) feedState.getData();
        bedFeed.setHeadOfBed(false);
        bedFeed.setFacingDirection(blockFeed.getFace(blockHead));

        feedState.update(true, false);
        headState.update(true, true);
      } else {
        Block blockHead = this.world.getBlockAt(team.getHeadTarget().getLocation());
        BlockState headState = blockHead.getState();

        headState.setType(targetMaterial);
        headState.update(true, true);
      }
    }

    ctx.getResourceSpawnerManager().loadChunks();

    for (Entity entity : this.removingEntities) {
      entity.remove();
    }

    Iterator<Entity> entityIterator = this.world.getEntities().iterator();
    while (entityIterator.hasNext()) {
      Entity e = entityIterator.next();

      if (this.removingEntities.contains(e)) {
        continue;
      }

      if (!this.isInRegion(e.getLocation())) {
        continue;
      }

      if (e instanceof Item) {
        e.remove();
        continue;
      }

      EntityType eType = e.getType();
      if (eType == EntityType.CREEPER
          || eType == EntityType.CAVE_SPIDER
          || eType == EntityType.SPIDER
          || eType == EntityType.ZOMBIE
          || eType == EntityType.SKELETON
          || eType == EntityType.SILVERFISH
          || eType == EntityType.ARROW
          || eType == EntityType.ARMOR_STAND) {
        e.remove();
        continue;
      }

      if (e instanceof LivingEntity) {
        LivingEntity le = (LivingEntity) e;
        le.setRemoveWhenFarAway(false);
      }
    }

    this.removingEntities.clear();
  }

  private void setMinMax(Location pos1, Location pos2) {
    this.minCorner = this.getMinimumCorner(pos1, pos2);
    this.maxCorner = this.getMaximumCorner(pos1, pos2);
  }

  public void setVillagerNametag() {
    Iterator<Entity> entityIterator = this.world.getEntities().iterator();
    while (entityIterator.hasNext()) {
      Entity e = entityIterator.next();

      if (!this.isInRegion(e.getLocation())) {
        continue;
      }

      if (e.getType() == EntityType.VILLAGER) {
        LivingEntity le = (LivingEntity) e;
        le.setCustomNameVisible(false);
        le.setCustomName(BedwarsRevol._l(BedwarsRevol.getInstance().getServer()
            .getConsoleSender(), "ingame.shop.name"));
      }
    }
  }

    public Location getTopMiddle() {
      double x = (maxCorner.getX() + minCorner.getX()) / 2;
      double y = maxCorner.getY();
      double z = (maxCorner.getZ() + minCorner.getZ()) / 2;
      return new Location(this.world, x, y, z);
    }
}
