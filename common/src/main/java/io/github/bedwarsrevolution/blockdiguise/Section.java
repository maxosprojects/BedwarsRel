package io.github.bedwarsrevolution.blockdiguise;

/**
 * Created by {maxos} 2017
 */
public class Section {
  private static final int SIZE_BLOCK_LIGHT = 4096 / 2; // 2048
  private static final int SIZE_SKY_LIGHT = 4096 / 2; // 2048

  protected byte[] data;
  protected int mask;
  protected boolean continuous;
  protected boolean overworld;
  protected int sectionNumber;
  protected int offset;
  private byte bitsPerBlock;
  private PaletteReader palette;
  private DataManager dataManager = null;

  protected Section() {
  }

  public Section nextSection() {
    if (sectionNumber >= 15) {
      return null;
    }
    this.palette.ffwd();
    int nextOffset = this.getDataManager().getDataEndIndex() + SIZE_BLOCK_LIGHT;
//    System.out.println(String.format("Section: %s, offset: 0x%06x, paletteEnd: 0x%06x, dataEnd: 0x%06x",
//        this.sectionNumber, this.offset, this.palette.getPaletteEndIndex(), this.getDataManager().getDataEndIndex()));
    if (this.overworld) {
      nextOffset += SIZE_SKY_LIGHT;
    }
    return toSection(this.data, this.mask, this.continuous, this.overworld, this.sectionNumber + 1, nextOffset);
  }

  public static Section toSection(byte[] data, int mask, boolean continuous, boolean overworld) {
    return toSection(data, mask, continuous, overworld, 0, 0);
  }

  protected static Section toSection(byte[] data, int mask, boolean continuous, boolean overworld, int sectionNumber, int offset) {
//    System.out.println(String.format("Section: %s, offset: 0x%06x", sectionNumber, offset));
    Section section;
    if (isEmptySection(sectionNumber, mask)) {
      section = new EmptySection();
    } else {
      section = new Section();
      section.bitsPerBlock = data[offset];
      section.palette = PaletteReader.read(data, offset + 1);
    }
    section.data = data;
    section.mask = mask;
    section.continuous = continuous;
    section.overworld = overworld;
    section.sectionNumber = sectionNumber;
    section.offset = offset;
    return section;
  }

  public DataManager getDataManager() {
    if (this.dataManager == null) {
      this.palette.ffwd();
      this.dataManager = DataManager.read(this.data, palette.getPaletteEndIndex(), bitsPerBlock);
    }
    return this.dataManager;
  }

  public boolean isEmpty() {
    return false;
  }

  private static boolean isEmptySection(int sectionNumber, int mask) {
    return (mask & (0x00000001 << sectionNumber)) == 0;
  }

}
