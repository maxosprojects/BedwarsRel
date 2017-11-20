package io.github.bedwarsrevolution.blockdiguise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter.AdapterParameteters;
import io.github.bedwarsrevolution.BedwarsRevol;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.google.common.collect.HashBasedTable;
import org.bukkit.entity.Player;

/**
 * Simple class that can be used to alter the apperance of a number of blocks.
 *
 * @author Kristian
 */
public class BlockDisguiser {

  private final BedwarsRevol plugin;
  private final Map<Player, HashBasedTable<ChunkCoordinate, BlockCoordinate, Integer>> translations = new HashMap<>();
  private PacketAdapter listener;

  public BlockDisguiser(BedwarsRevol plugin) {
    this.plugin = plugin;
  }

  /**
   * Create a new translated block that have a different block ID on the server and visually for a
   * client.
   *
   * @param loc - the location of the block.
   * @param replaceWith - the material this block will appear as on the client side.
   */
  public void setTranslatedBlock(Player player, Location loc, int replaceWith) {
    HashBasedTable<ChunkCoordinate, BlockCoordinate, Integer> map = this.translations.get(player);
    if (map == null) {
      map = HashBasedTable.create();
      this.translations.put(player, map);
    }
    map.put(ChunkCoordinate.fromBlock(loc), new BlockCoordinate(loc), replaceWith);
  }

  public void registerListener() {
    this.plugin.getProtocolManager().addPacketListener(new PacketAdapter(new AdapterParameteters()
        .plugin(this.plugin)
        .serverSide()
        .listenerPriority(ListenerPriority.HIGHEST)
        .types(Server.BLOCK_CHANGE,
            Server.MULTI_BLOCK_CHANGE,
            Server.MAP_CHUNK,
            Server.MAP_CHUNK_BULK)) {
      @Override
      public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        World world = event.getPlayer().getWorld();
        PacketType type = event.getPacketType();
        Player player = event.getPlayer();
        if (type == Server.BLOCK_CHANGE) {
          translateBlockChange(player, packet, world);
        } else if (type == Server.MULTI_BLOCK_CHANGE) {
          translateMultiBlockChange(player, packet, world);
        } else if (type == Server.MAP_CHUNK) {
          final SectionProcessor sectionProcessor = getSectionProcessor(player);
          ChunkPacketProcessor.read(packet, world).process(sectionProcessor);
//          if (proc.chunkX == 0 && proc.chunkZ == 0) {
//            StringBuilder sb = new StringBuilder();
//            for (byte bite : proc.data) {
//              sb.append(String.format("0x%02x ", bite));
//            }
//            System.out.println(String.format("bytes: %s, characters: %s", proc.data.length, sb.length()));
//            System.out.println(sb);
//            System.out.println(String.format("bytes: %s, isContinuous: %s, chunkMask: 0x%04x", proc.data.length, proc.isContinuous, proc.chunkMask));
//            try {
//              FileOutputStream os = new FileOutputStream("packet-data.bin");
//              os.write(proc.data);
//              os.flush();
//              os.close();
//            } catch (IOException e) {
//              e.printStackTrace();
//            }
//          }
        } else if (type == Server.MAP_CHUNK_BULK) {
          // TODO: do something for 1.8
//              for (ChunkPacketProcessor chunk : ChunkPacketProcessor.fromMapBulkPacket(packet, world)) {
//                chunk.process(processor);
//              }
        }
      }
    });
  }

  public void close() {
    if (this.listener != null) {
      this.plugin.getProtocolManager().removePacketListener(this.listener);
      this.listener = null;
    }
  }

  private SectionProcessor getSectionProcessor(final Player player) {
    return new SectionProcessor() {
      @Override
      public void processSection(int chunkX, int chunkZ, int sectionY, Section section) {
        if (chunkX == 0 && chunkZ == 0 && sectionY == 3 && !section.isEmpty()) {
//          DataManager data = section.getDataManager();
//          int blockData = data.getBlockData(x, y, z);
//          System.out.println(String.format("(%s,%s,%s, block: %d:%d",
//              x, sectionY * 16 + y, z, blockData >> 4, blockData & 0x0f));
//          data.setBlockData(x, y, z, 1);
        }
      }
    };
  }

  private void translateBlockChange(Player player, PacketContainer packet, World world)
      throws FieldAccessException {
    StructureModifier<Integer> ints = packet.getIntegers();
    int x = ints.read(0);
    int y = ints.read(1);
    int z = ints.read(2);
    int blockID = ints.read(3);

    System.out.println("Block change: " + x + ", " + y + ", " + z);

    // Convert using the tables
    ints.write(3, translateBlockID(player, world, x, y, z, blockID));
  }

  private void translateMultiBlockChange(Player player, PacketContainer packet, World world)
      throws FieldAccessException {
//    StructureModifier<byte[]> byteArrays = packet.getByteArrays();
//    StructureModifier<Integer> ints = packet.getIntegers();
//
//    int baseX = ints.read(0) << 4;
//    int baseZ = ints.read(1) << 4;
//    BlockChangeArray data = new BlockChangeArray(byteArrays.read(0));
//
//    for (int i = 0; i < data.getSize(); i++) {
//      BlockChange change = data.getBlockChange(i);
//      change.setBlockID(translateBlockID(
//          player,
//          world,
//          baseX + change.getRelativeX(),
//          change.getAbsoluteY(),
//          baseZ + change.getRelativeZ(),
//          change.getBlockID()
//      ));
//    }
//    byteArrays.write(0, data.toByteArray());
  }

  private int translateBlockID(Player player, World world, int x, int y, int z, int blockID) {
    HashBasedTable<ChunkCoordinate, BlockCoordinate, Integer> map = this.translations.get(player);
    if (map == null) {
      return blockID;
    }
    Integer translate = map.get(
        ChunkCoordinate.fromBlock(world, x, z), new BlockCoordinate(x, y, z));

    // Use the existing block ID if not found
    return translate == null ? blockID : translate;
  }
}
