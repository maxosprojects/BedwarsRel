package io.github.bedwarsrevolution.blockdiguise;

import io.github.bedwarsrevolution.game.TeamNew;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Created by {maxos} 2017
 */
public class ChunksTable {
  private Map<SectionCoordinate, SectionTable> map = new ConcurrentHashMap<>();

  public SectionTable getSectionTable(String world, int chunkX, int sectionY, int chunkZ) {
    SectionTable section = this.map.get(SectionCoordinate.fromSection(world, chunkX, sectionY, chunkZ));
    if (section == null) {
      return EmptySectionTable.instance;
    }
    return section;
  }

  /**
   *
   * @param location
   * @param material
   * @param metaData
   * @return true if added/replaced, false otherwise (if existed already)
   */
  public boolean add(Location location, Material material, int metaData) {
    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();
    return this.add(location.getWorld().getName(), x, y, z, material, metaData);
  }

  private boolean add(String world, int x, int y, int z, Material material, int metaData) {
    BlockData block = new BlockData(x, y, z, material, metaData);
    SectionCoordinate coord = SectionCoordinate.fromBlock(world, x, y, z);
    SectionTable section = this.map.get(coord);
    if (section == null) {
      section = new RegularSectionTable(coord);
      this.map.put(coord, section);
    }
    return section.add(x, y, z, block);
  }

  public BlockData remove(Location location) {
    SectionCoordinate coord = SectionCoordinate.fromBlock(location);
    SectionTable section = this.map.get(coord);
    if (section == null) {
      return null;
    }
    BlockData res = section.remove(location);
    if (section.isEmpty()) {
      this.map.remove(coord);
    }
    return res;
  }

  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  public Map<SectionCoordinate, SectionTable> getMap() {
    return Collections.unmodifiableMap(this.map);
  }

  public Map<BlockCoordinate,BlockData> getAllBlocks() {
    ChunksTable res = new ChunksTable();
    for (Entry<TeamNew, ChunksTable> chunkEntry : this.chunksMap.entrySet()) {
      TeamNew team = chunkEntry.getKey();
      ChunksTable teamTable = chunkEntry.getValue();
      Map<BlockCoordinate, BlockData> teamBlocks = teamTable.getAllBlocks();
      for (Entry<SectionCoordinate, SectionTable> entry : table.map.entrySet()) {
        SectionCoordinate sectionCoord = entry.getKey();
        for (BlockData block : entry.getValue().getAll()) {
          res.add(sectionCoord.getWorld(), block.getX(), block.getY(), block.getZ(),
              material, metaData);
        }
      }
    }
  }
}
