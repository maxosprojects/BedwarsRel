package io.github.bedwarsrevolution.blockdiguise;

/**
 * Created by {maxos} 2017
 */
public class VarInt {
  private final int result;
  private final int size;

  private VarInt(int result, int size) {
    this.result = result;
    this.size = size;
  }

  public static VarInt read(byte[] data, int startIndex) {
    int index = startIndex;
    int numRead = 0;
    int result = 0;
    byte read;

    do {
      read = data[index];
      int value = (read & 0b01111111);
      result |= (value << (7 * numRead));
      index++;
      numRead++;
      if (numRead > 5) {
        throw new RuntimeException("VarInt is too big");
      }
    } while ((read & 0b10000000) != 0);

    return new VarInt(result, numRead);
  }

  public int getResult() {
    return result;
  }

  /**
   * Returns number of bytes result occupies in source data array
   * @return
   */
  public int getSize() {
    return size;
  }

}
