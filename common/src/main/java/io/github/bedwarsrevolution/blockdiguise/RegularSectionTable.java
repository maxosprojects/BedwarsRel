package io.github.bedwarsrevolution.blockdiguise;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Location;

/**
 * Created by {maxos} 2017
 */
public class RegularSectionTable implements SectionTable {
  @Getter
  private final SectionCoordinate sectionCoordinate;
  private final Map<BlockCoordinate, BlockData> map = new HashMap<>();

  public RegularSectionTable(Location location) {
    this.sectionCoordinate = SectionCoordinate.fromBlock(location);
  }

  public RegularSectionTable(SectionCoordinate coord) {
    this.sectionCoordinate = coord;
  }

  @Override
  public void process(SectionExecutor sectionExecutor) {
    sectionExecutor.execute(this.map.values());
  }

  @Override
  public BlockData get(int x, int y, int z) {
    return this.map.get(new BlockCoordinate(x, y, z));
  }

  @Override
  public BlockData get(Location location) {
    return this.map.get(new BlockCoordinate(location));
  }

  @Override
  public boolean add(Location location, BlockData block) {
    return this.add(new BlockCoordinate(location), block);
  }

  @Override
  public boolean add(int x, int y, int z, BlockData block) {
    return this.add(new BlockCoordinate(x, y, z), block);
  }

  private boolean add(BlockCoordinate coord, BlockData block) {
    BlockData existing = this.map.get(coord);
    if (existing != null && existing.is(block.getType())) {
      return false;
    }
    this.map.put(coord, block);
    return true;
  }

  @Override
  public boolean remove(int x, int y, int z) {
    return this.map.remove(new BlockCoordinate(x, y, z)) != null;
  }

  @Override
  public boolean remove(Location location) {
    return this.map.remove(new BlockCoordinate(location)) != null;
  }

}
