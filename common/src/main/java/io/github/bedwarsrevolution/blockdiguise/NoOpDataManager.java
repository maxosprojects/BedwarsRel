package io.github.bedwarsrevolution.blockdiguise;

/**
 * Created by {maxos} 2017
 */
public class NoOpDataManager extends DataManager {

  private NoOpDataManager() {
  }

  @Override
  public int getBlockData(int localX, int localY, int localZ) {
    System.err.println("NoOpDataManager shouldn't have been called on empty section");
    return 0;
  }

  @Override
  public void setBlockData(int localX, int localY, int localZ, int val) {
  }

  public static DataManager instance() {
    return new NoOpDataManager();
  }

}
