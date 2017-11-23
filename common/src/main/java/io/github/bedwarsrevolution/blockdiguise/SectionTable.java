package io.github.bedwarsrevolution.blockdiguise;

import java.util.Map;
import org.bukkit.Location;

/**
 * Created by {maxos} 2017
 */
public interface SectionTable {

  void process(ChunkBlocksProcessor chunkBlocksProcessor);

  /**
   *
   * @param x
   * @param y
   * @param z
   * @param block
   * @return true if new added/replaced, false if existed
   */
  boolean add(int x, int y, int z, BlockData block);

  /**
   *
   * @param location
   * @param block
   * @return true if new added/replaced, false if existed
   */
  boolean add(Location location, BlockData block);

  /**
   *
   * @param x
   * @param y
   * @param z
   * @return {@link BlockData} if existed at the location, null otherwise
   */
  BlockData remove(int x, int y, int z);

  /**
   *
   * @param location
   * @return {@link BlockData} if existed at the location, null otherwise
   */
  BlockData remove(Location location);

  BlockData get(int x, int y, int z);

  BlockData get(Location location);

  boolean isEmpty();

  Map<BlockCoordinate, BlockData> getMap();
}
