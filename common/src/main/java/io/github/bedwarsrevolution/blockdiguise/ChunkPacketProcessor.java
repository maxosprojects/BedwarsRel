package io.github.bedwarsrevolution.blockdiguise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.World;
import org.bukkit.World.Environment;

/**
 * @author maxos
 */
public class ChunkPacketProcessor {
  private static final int SECTIONS_IN_CHUNK = 16;

  private int chunkX;
  private int chunkZ;
  private int chunkMask;
  private boolean isContinuous;

  public byte[] data;
  private World world;

  private ChunkPacketProcessor() {
  }

  /**
   *
   * @param packet - the map chunk packet.
   * @return The chunk packet processor.
   */
  public static ChunkPacketProcessor read(PacketContainer packet, World world) {
    if (!packet.getType().equals(PacketType.Play.Server.MAP_CHUNK)) {
      throw new IllegalArgumentException(packet + " must be a MAP_CHUNK packet.");
    }

    StructureModifier<Integer> ints = packet.getIntegers();
    StructureModifier<byte[]> byteArray = packet.getByteArrays();

    ChunkPacketProcessor processor = new ChunkPacketProcessor();
    processor.world = world;
    processor.chunkX = ints.read(0); 	 // packet.a;
    processor.chunkZ = ints.read(1); 	 // packet.b;
    processor.chunkMask = ints.read(2);  // packet.c;
    processor.data = byteArray.read(0);  // packet.d;
    processor.isContinuous = packet.getBooleans().read(0); // packet.f
//    System.out.println(String.format("bytes: %s, isContinuous: %s, chunkMask: 0x%04x",
//        processor.data.length, processor.isContinuous, processor.chunkMask));
    return processor;
  }

  /**
   * Processes chunk with the provided section processor
   *
   * @param processor - section processor
   */
  public void process(SectionProcessor processor) {
//    System.out.println(String.format("Starting to process chunk (%s,%s)", this.chunkX, this.chunkZ));
    Section section = Section.toSection(this.data, this.chunkMask, this.isOverworld(), this.isContinuous);
    for (int i = 0; i < SECTIONS_IN_CHUNK; i++) {
      processor.processSection(this.chunkX, i, this.chunkZ, section);
      if (i < SECTIONS_IN_CHUNK - 1) {
        section = section.nextSection();
      }
    }
  }

  public boolean isOverworld() {
    return this.world.getEnvironment() == Environment.NORMAL;
  }

}
