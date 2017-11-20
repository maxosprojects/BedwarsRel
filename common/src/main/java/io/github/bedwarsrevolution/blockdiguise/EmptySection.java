package io.github.bedwarsrevolution.blockdiguise;

/**
 * Created by {maxos} 2017
 */
class EmptySection extends Section {

  protected EmptySection() {
  }

  @Override
  public Section nextSection() {
    return toSection(this.data, this.mask, this.continuous, this.overworld, this.sectionNumber + 1, this.offset);
  }

  @Override
  public DataManager getDataManager() {
    System.err.println("getDataManager() shouldn't have been called on empty section");
    return NoOpDataManager.instance();
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

}
