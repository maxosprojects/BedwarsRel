package io.github.bedwarsrevolution.blockdiguise;

import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;

/**
 * Created by {maxos} 2017
 */
public class BlockData {

  private final int x;
  private final int y;
  private final int z;
  private final Material material;
  private final int metaData;
  private final WrappedBlockData blockData;
  private final short compactLocation;

  public BlockData(int x, int y, int z, Material material, int metaData) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.material = material;
    this.metaData = metaData;
    this.blockData = WrappedBlockData.createData(material);
    this.compactLocation = this.getCompactLocation();
  }

  /**
   * Returns location in compact form coonosumable by {@link com.comphenix.protocol.wrappers.MultiBlockChangeInfo}
   *
   * @return
   */
  private short getCompactLocation() {
    return (short) ((x & 15) << 12 | (z & 15) << 8 | y);
  }

  public Material getType() {
    return this.material;
  }

  public int getMetaData() {
    return this.metaData;
  }

  public MultiBlockChangeInfo toChangeInfo(ChunkCoordIntPair chunkCoord) {
    return new MultiBlockChangeInfo(this.compactLocation, blockData, chunkCoord);
  }

  public boolean is(int x, int y, int z) {
    return this.x == x && this.y == y && this.z == z;
  }

  public boolean is(Material otherMaterial, int otherMetaData) {
    return otherMaterial == this.material && otherMetaData == this.metaData;
  }
}
