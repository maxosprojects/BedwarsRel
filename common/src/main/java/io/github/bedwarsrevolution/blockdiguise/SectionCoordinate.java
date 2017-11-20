package io.github.bedwarsrevolution.blockdiguise;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.base.Objects;

class SectionCoordinate implements Serializable {
  private final String world;
  private final int chunkX;
  private final int chunkZ;
  private final int sectionY;

  private SectionCoordinate(String world, int chunkX, int chunkZ, int sectionY) {
    this.world = world;
    this.chunkX = chunkX;
    this.chunkZ = chunkZ;
    this.sectionY = sectionY;
  }

  public static SectionCoordinate fromBlock(String world, int x, int z, int y) {
    return new SectionCoordinate(world, x >> 4, z >> 4, y >> 4);
  }

  public static SectionCoordinate fromBlock(Location loc) {
    return fromBlock(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), loc.getBlockY());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        this.world,
        this.chunkX,
        this.chunkZ,
        this.sectionY);
  }

  public int getChunkX() {
    return this.chunkX;
  }

  public int getChunkZ() {
    return this.chunkZ;
  }

  public int getSectionY() {
    return this.sectionY;
  }

  public String getWorld() {
    return this.world;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof SectionCoordinate) {
      SectionCoordinate other = (SectionCoordinate) obj;
      return this.world.equals(other.world)
          && this.chunkX == other.chunkX
          && this.chunkZ == other.chunkZ
          && this.sectionY == other.sectionY;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("[world: %s, chunkX: %d, chunkZ: %d, sectionY: %d]", this.world, this.chunkX, this.chunkZ, this.sectionY);
  }

}
