package io.github.bedwarsrevolution.blockdiguise;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.base.Objects;

class SectionCoordinate implements Serializable {
  private final String world;
  private final int chunkX;
  private final int sectionY;
  private final int chunkZ;

  private SectionCoordinate(String world, int chunkX, int sectionY, int chunkZ) {
    this.world = world;
    this.chunkX = chunkX;
    this.sectionY = sectionY;
    this.chunkZ = chunkZ;
  }

  public static SectionCoordinate fromSection(String world, int x, int y, int z) {
    return new SectionCoordinate(world, x, y, z);
  }

  public static SectionCoordinate fromBlock(String world, int x, int y, int z) {
    return new SectionCoordinate(world, x >> 4, y >> 4, z >> 4);
  }

  public static SectionCoordinate fromBlock(Location loc) {
    return fromBlock(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        this.world,
        this.chunkX,
        this.sectionY,
        this.chunkZ);
  }

  public int getChunkX() {
    return this.chunkX;
  }

  public int getSectionY() {
    return this.sectionY;
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

    if (obj instanceof SectionCoordinate) {
      SectionCoordinate other = (SectionCoordinate) obj;
      return this.world.equals(other.world)
          && this.chunkX == other.chunkX
          && this.sectionY == other.sectionY
          && this.chunkZ == other.chunkZ;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("[world: %s, chunkX: %d, sectionY: %d, chunkZ: %d]", this.world, this.chunkX, this.sectionY, this.chunkZ);
  }

}
