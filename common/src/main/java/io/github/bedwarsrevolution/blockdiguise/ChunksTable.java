package io.github.bedwarsrevolution.blockdiguise;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Created by {maxos} 2017
 */
public class ChunksTable {
  private Map<SectionCoordinate, SectionTable> map = new HashMap<>();

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
    BlockData block = new BlockData(x, y, z, material, metaData);

    SectionCoordinate coord = SectionCoordinate.fromBlock(location);
    SectionTable section = this.map.get(coord);
    if (section == null) {
      section = new RegularSectionTable(coord);
      this.map.put(coord, section);
    }
    return section.add(location, block);
  }

  public boolean remove(Location location) {
    SectionCoordinate coord = SectionCoordinate.fromBlock(location);
    SectionTable section = this.map.get(coord);
    if (section == null) {
      return false;
    }
    return section.remove(location);
  }

}
