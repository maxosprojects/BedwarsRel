package io.github.bedwarsrevolution.blockdiguise;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Created by {maxos} 2017
 */
public class PaletteReader implements Iterator<Integer> {
  private static int incr = 0;

  private int startIndex;
  private int currIndex;
  private byte[] data;
  private int length;
  private int counter = 0;

  public static PaletteReader read(byte[] data, int offset) {
    PaletteReader palette = new PaletteReader();
    VarInt lengthVarInt = VarInt.read(data, offset);
//    try {
//      lengthVarInt = VarInt.read(data, offset);
//    } catch (Exception e) {
//      System.out.println("Exception occurred, offset: " + offset);
//      try {
//        OutputStream os = new FileOutputStream("packet-data-ex" + incr + ".bin");
//        incr++;
//        os.write(data);
//        os.flush();
//        os.close();
//      } catch (java.io.IOException e1) {
//        e1.printStackTrace();
//      }
//      throw e;
//    }
    palette.data = data;
    palette.length = lengthVarInt.getResult();
    palette.startIndex = offset + lengthVarInt.getSize();
    palette.currIndex = palette.startIndex;
    return palette;
  }

  /**
   * Fast forwards  array.
   * Used when it is needed to find the size of the palette array (in bytes) or the offset of
   * the data array that follows palette
   */
  public void ffwd() {
    while(this.hasNext()) {
      this.next();
    }
  }

  /**
   * Returns the length of the array (how many elements)
   * @return
   */
  public int getArrayLength() {
    return this.length;
  }

  /**
   * Returns the size of the array (how many bytes).
   * Returns correct value only after data array has been iterated through to the end (e.g. ffwd used)
   * @return
   */
  public int getArraySize() {
    return this.currIndex - this.startIndex;
  }

  /**
   * Returns the index to the Data array where Palette ends and Block ID data starts.
   * Returns correct value only after data array has been iterated through to the end (e.g. ffwd used)
   * @return
   */
  public int getPaletteEndIndex() {
    return this.currIndex;
  }

  @Override
  public boolean hasNext() {
    return this.counter < this.length;
  }

  @Override
  public Integer next() {
    VarInt varInt = VarInt.read(this.data, this.currIndex);
    this.counter++;
    this.currIndex += varInt.getSize();
    return varInt.getResult();
  }

  @Override
  public void remove() {
    throw new RuntimeException("Not implemented");
  }
}
