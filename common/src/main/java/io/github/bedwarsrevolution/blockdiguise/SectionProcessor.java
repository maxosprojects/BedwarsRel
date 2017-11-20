package io.github.bedwarsrevolution.blockdiguise;

/**
 * Created by {maxos} 2017
 */
public interface SectionProcessor {

  /**
   * Processes chunk section
   *  @param chunkX coordinate X of the chunk (world.x/16)
   * @param chunkZ coordinate Z of the chunk (world.z/16)
   * @param sectionY coordinate Y of the section (world.y/16)
   * @param section section to process
   */
  void processSection(int chunkX, int chunkZ, int sectionY, Section section);
}
