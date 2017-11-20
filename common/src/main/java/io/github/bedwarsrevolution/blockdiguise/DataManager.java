package io.github.bedwarsrevolution.blockdiguise;

/**
 * Reads Data array (block state IDs pointing to the Palette) in the chunk packet
 *
 * Created by {maxos} 2017
 */
public class DataManager {
  private static final int SIZE_LONG = 8;

  private byte[] data;
  private int bitsPerBlock;
  private long maxBlockValue;
  private int longsLength;
  private int length;
  private int startIndex;
  private long[] longsData;

  protected DataManager() {
  }

  public static DataManager read(byte[] data, int offset, int bitsPerBlock) {
    DataManager reader = new DataManager();
    VarInt lengthVarInt = VarInt.read(data, offset);
    reader.data = data;
    reader.bitsPerBlock = bitsPerBlock;
    reader.maxBlockValue = (1L << reader.bitsPerBlock) - 1;
    reader.longsLength = lengthVarInt.getResult();
    reader.length = reader.longsLength * SIZE_LONG;
    reader.startIndex = offset + lengthVarInt.getSize();
    return reader;
  }

  public int getBlockData(int localX, int localY, int localZ) {
    int flatBlockLocation = toFlatLocation(localX, localY, localZ);
    long[] longs = this.getLongsData();
    int bitIndex = flatBlockLocation * this.bitsPerBlock;
    int startIndex = bitIndex / 64;
    int endIndex = ((flatBlockLocation + 1) * this.bitsPerBlock - 1) / 64;
    int startBitSubIndex = bitIndex % 64;
    if(startIndex == endIndex) {
      return (int) (longs[startIndex] >>> startBitSubIndex & this.maxBlockValue);
    } else {
      int endBitSubIndex = 64 - startBitSubIndex;
      return (int) ((longs[startIndex] >>> startBitSubIndex | longs[endIndex] << endBitSubIndex) & this.maxBlockValue);
    }
  }

  private long[] getLongsData() {
    if (this.longsData == null) {
      this.longsData = new long[this.longsLength];
      int currByteIndex = this.startIndex;
      for (int i = 0; i < this.longsLength; i++) {
        this.longsData[i] =
            ((long) this.data[currByteIndex] << 56)
          + ((long) (this.data[currByteIndex + 1] & 255) << 48)
          + ((long) (this.data[currByteIndex + 2] & 255) << 40)
          + ((long) (this.data[currByteIndex + 3] & 255) << 32)
          + ((long) (this.data[currByteIndex + 4] & 255) << 24)
          + ((long) (this.data[currByteIndex + 5] & 255) << 16)
          + ((long) (this.data[currByteIndex + 6] & 255) << 8)
          + ((long) (this.data[currByteIndex + 7] & 255));
        currByteIndex += SIZE_LONG;
      }
    }
    return this.longsData;
  }

  private void saveLongsData(long long1, long long2, int longIndex1, int longIndex2) {
    this.saveLong(long1, longIndex1);
    this.saveLong(long2, longIndex2);
  }

  private void saveLong(long val, int index) {
    int currIndex = this.startIndex + index * SIZE_LONG;
    this.data[currIndex] = (byte) (val >>> 56);
    this.data[currIndex + 1] = (byte) (val >>> 48);
    this.data[currIndex + 2] = (byte) (val >>> 40);
    this.data[currIndex + 3] = (byte) (val >>> 32);
    this.data[currIndex + 4] = (byte) (val >>> 24);
    this.data[currIndex + 5] = (byte) (val >>> 16);
    this.data[currIndex + 6] = (byte) (val >>> 8);
    this.data[currIndex + 7] = (byte) val;
  }

  public void setBlockData(int localX, int localY, int localZ, int val) {
    int flatBlockLocation = toFlatLocation(localX, localY, localZ);
    long[] longs = this.getLongsData();
    int bitIndex = flatBlockLocation * this.bitsPerBlock;
    int startIndex = bitIndex / 64;
    int endIndex = ((flatBlockLocation + 1) * this.bitsPerBlock - 1) / 64;
    int startBitSubIndex = bitIndex % 64;
    longs[startIndex] = longs[startIndex] & ~(this.maxBlockValue << startBitSubIndex) | ((long) val & this.maxBlockValue) << startBitSubIndex;
    if(startIndex != endIndex) {
      int endBitSubIndex = 64 - startBitSubIndex;
      longs[endIndex] = longs[endIndex] >>> endBitSubIndex << endBitSubIndex | ((long) val & this.maxBlockValue) >> endBitSubIndex;
    }

    this.saveLongsData(longs[startIndex], longs[endIndex], startIndex, endIndex);
  }

  private static int toFlatLocation(int x, int y, int z) {
    return y << 8 | z << 4 | x;
  }

  /**
   * Returns data array size in bytes
   *
   * @return
   */
  public int getLength() {
    return length;
  }

  /**
   * Returns index to the Data array where Block IDs end and Block Light data starts
   *
   * @return
   */
  public int getDataEndIndex() {
    return this.startIndex + this.length;
  }

}
