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
    int result = 0;
    int index = startIndex;
    int shift = 0;
    byte read;

    do {
      read = data[index];
      result |= (read & 127) << (shift * 7);
      index++;
      shift++;
      if (shift > 5) {
        throw new RuntimeException("VarInt is too big");
      }
    } while ((read & 128) != 0);

    return new VarInt(result, shift);
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
