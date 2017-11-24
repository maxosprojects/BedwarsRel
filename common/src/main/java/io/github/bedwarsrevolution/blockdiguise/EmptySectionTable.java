package io.github.bedwarsrevolution.blockdiguise;

import java.util.Collections;
import java.util.Map;
import org.bukkit.Location;

/**
 * Created by {maxos} 2017
 */
public class EmptySectionTable implements SectionTable {
  public static final EmptySectionTable instance = new EmptySectionTable();

  @Override
  public void process(ChunkBlocksProcessor chunkBlocksProcessor) {
  }

  @Override
  public BlockData get(Location location) {
    return null;
  }

  @Override
  public BlockData get(int x, int y, int z) {
    return null;
  }

  @Override
  public boolean add(int x, int y, int z, BlockData block) {
    return false;
  }

  @Override
  public boolean add(Location location, BlockData block) {
    return false;
  }

  @Override
  public BlockData remove(int x, int y, int z) {
    return null;
  }

  @Override
  public BlockData remove(Location location) {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public Map<BlockCoordinate, BlockData> getMap() {
    return Collections.emptyMap();
  }

}