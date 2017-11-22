package io.github.bedwarsrevolution.blockdiguise;

import com.google.common.base.Objects;
import java.io.Serializable;
import org.bukkit.Location;

class ChunkCoordinate implements Serializable {
  private final String world;
  private final int chunkX;
  private final int chunkZ;

  private ChunkCoordinate(String world, int chunkX, int chunkZ) {
    this.world = world;
    this.chunkX = chunkX;
    this.chunkZ = chunkZ;
  }

  public static ChunkCoordinate fromChunk(String world, int x, int z) {
    return new ChunkCoordinate(world, x, z);
  }

  public static ChunkCoordinate fromBlock(String world, int x, int z) {
    return new ChunkCoordinate(world, x >> 4, z >> 4);
  }

  public static ChunkCoordinate fromBlock(Location loc) {
    return fromBlock(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        this.world,
        this.chunkX,
        this.chunkZ);
  }

  public int getChunkX() {
    return this.chunkX;
  }

  public int getChunkZ() {
    return this.chunkZ;
  }

  public String getWorld() {
    return this.world;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof ChunkCoordinate) {
      ChunkCoordinate other = (ChunkCoordinate) obj;
      return this.world.equals(other.world)
          && this.chunkX == other.chunkX
          && this.chunkZ == other.chunkZ;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("[world: %s, chunkX: %d, chunkZ: %d]", this.world, this.chunkX, this.chunkZ);
  }

}
