package io.github.bedwarsrevolution.blockdiguise;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Created by {maxos} 2017
 */
public class ChunksTable {
  private Map<SectionCoordinate, SectionTable> map = new ConcurrentHashMap<>();

  public BlockData getBlock(Location location) {
    SectionTable section = this.getSectionTable(SectionCoordinate.fromBlock(location));
    return section.get(location);
  }

  public SectionTable getSectionTable(String world, int chunkX, int sectionY, int chunkZ) {
    return this.getSectionTable(SectionCoordinate.fromSection(world, chunkX, sectionY, chunkZ));
  }

  public SectionTable getSectionTable(SectionCoordinate coord) {
    SectionTable section = this.map.get(coord);
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

  public boolean add(String world, int x, int y, int z, Material material, int metaData) {
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

}
