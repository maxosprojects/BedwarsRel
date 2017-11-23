package io.github.bedwarsrevolution.blockdiguise;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bukkit.Location;

/**
 * Created by {maxos} 2017
 */
public class RegularSectionTable implements SectionTable {
  @Getter
  private final SectionCoordinate sectionCoordinate;
  private final Map<BlockCoordinate, BlockData> map = new ConcurrentHashMap<>();

  public RegularSectionTable(Location location) {
    this.sectionCoordinate = SectionCoordinate.fromBlock(location);
  }

  public RegularSectionTable(SectionCoordinate coord) {
    this.sectionCoordinate = coord;
  }

  @Override
  public void process(ChunkBlocksProcessor chunkBlocksProcessor) {
    chunkBlocksProcessor.process(this.map.values());
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
    if (existing != null && existing.is(block.getType(), block.getMetaData())) {
      return false;
    }
    this.map.put(coord, block);
    return true;
  }

  @Override
  public BlockData remove(int x, int y, int z) {
    return this.map.remove(new BlockCoordinate(x, y, z));
  }

  @Override
  public BlockData remove(Location location) {
    return this.map.remove(new BlockCoordinate(location));
  }

  @Override
  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  @Override
  public Map<BlockCoordinate, BlockData> getMap() {
    return Collections.unmodifiableMap(this.map);
  }

}
