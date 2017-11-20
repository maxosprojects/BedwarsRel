package io.github.bedwarsrevolution.blockdiguise;

import org.bukkit.Location;

/**
 * Created by {maxos} 2017
 */
public interface SectionTable {

  void process(SectionExecutor sectionExecutor);

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
   * @return true if removed, false if did not exist
   */
  boolean remove(int x, int y, int z);

  /**
   *
   * @param location
   * @return true if removed, false if did not exist
   */
  boolean remove(Location location);

  BlockData get(int x, int y, int z);

  BlockData get(Location location);

}
