package io.github.bedwarsrevolution.blockdiguise;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerMultiBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter.AdapterParameteters;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.collect.ImmutableSet;
import io.github.bedwarsrevolution.BedwarsRevol;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Simple class that can be used to alter the apperance of a number of blocks.
 *
 * @author Kristian
 */
public class BlockDisguiser {

  private final BedwarsRevol plugin;
  private final Map<TeamNew, ChunksTable> chunksMap = new HashMap<>();
  private PacketAdapter listener;

  public BlockDisguiser(BedwarsRevol plugin) {
    this.plugin = plugin;
  }

  public void registerListener() {
    this.plugin.getProtocolManager().addPacketListener(new PacketAdapter(new AdapterParameteters()
        .plugin(this.plugin)
        .serverSide()
        .listenerPriority(ListenerPriority.HIGHEST)
        .types(Server.BLOCK_CHANGE,
            Server.MULTI_BLOCK_CHANGE,
            Server.MAP_CHUNK)) {
      @Override
      public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        GameContext game = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
          return;
        }
        TeamNew team = game.getPlayerContext(player).getTeam();
        if (team == null) {
          return;
        }
        ChunksTable chunksTable = chunksMap.get(team);
        if (chunksTable == null) {
          return;
        }
        World world = event.getPlayer().getWorld();
        PacketContainer packet = event.getPacket();
        PacketType type = event.getPacketType();
        if (type == Server.BLOCK_CHANGE) {
          translateBlockChange(chunksTable, player, packet);
        } else if (type == Server.MULTI_BLOCK_CHANGE) {
          translateMultiBlockChange(chunksTable, player, packet);
        } else if (type == Server.MAP_CHUNK) {
          final SectionProcessor sectionProcessor = getSectionProcessor(chunksTable, player);
          ChunkPacketProcessor.read(packet, world).process(sectionProcessor);
        }
      }
    });
//    this.alsoRegister();
  }

  private void alsoRegister() {
    PacketType[] types = Server.getInstance().values().toArray(new PacketType[0]);
    final ImmutableSet<PacketType> notTypes = ImmutableSet.of(
        Server.PLAYER_INFO,
        Server.KEEP_ALIVE,
        Server.UPDATE_TIME,
        Server.REL_ENTITY_MOVE_LOOK,
        Server.NAMED_SOUND_EFFECT,
        Server.ENTITY_METADATA,
        Server.SCOREBOARD_SCORE,
        Server.ENTITY_VELOCITY,
        Server.ENTITY_TELEPORT,
        Server.REL_ENTITY_MOVE,
        Server.ENTITY_HEAD_ROTATION);
    this.plugin.getProtocolManager().addPacketListener(new PacketAdapter(new AdapterParameteters()
        .plugin(this.plugin)
        .serverSide()
        .listenerPriority(ListenerPriority.HIGHEST)
        .types(types)) {
      @Override
      public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        PacketType type = event.getPacketType();
        if (!notTypes.contains(type)) {
          System.out.println(type);
          if (type == Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity wrapped = new WrapperPlayServerSpawnEntity(packet);

            System.out.println(String.format("type: %s, id: %s", wrapped.getType(), wrapped.getEntityID()));

          } else if (type == Server.ENTITY_DESTROY) {
            WrapperPlayServerEntityDestroy wrapped = new WrapperPlayServerEntityDestroy(packet);
            System.out.println(wrapped.getEntityIDs().toString());
          }
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

  private SectionProcessor getSectionProcessor(final ChunksTable chunksTable, final Player player) {
    return new SectionProcessor() {
      @Override
      public void processSection(final int chunkX, final int sectionY, final int chunkZ, Section section) {

//        System.out.println(String.format("Processing section (%s,%s,%s)", chunkX, sectionY, chunkZ));

        SectionTable sectionTable = chunksTable.getSectionTable(player.getWorld().getName(), chunkX, sectionY, chunkZ);
        sectionTable.process(new SectionExecutor() {
          @Override
          public void execute(Collection<BlockData> data) {
            if (data.size() == 0) {
              return;
            }

//            System.out.println(String.format("Data found for section (%s,%s,%s)", chunkX, sectionY, chunkZ));

            final WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange();
            ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
            packet.setChunk(chunkCoords);
            MultiBlockChangeInfo[] records = new MultiBlockChangeInfo[data.size()];
            int index = 0;
            for (BlockData block : data) {
              records[index] = block.toChangeInfo(chunkCoords);
              index++;
            }
            packet.setRecords(records);
            new BukkitRunnable() {
              @Override
              public void run() {

//                System.out.println(String.format("Sending MultiBlockChange packet for (%s,%s,%s)", chunkX, sectionY, chunkZ));

                packet.sendPacket(player);
              }
            }.runTaskLater(plugin, 1);
          }
        });
      }
    };
  }

  private void translateBlockChange(ChunksTable chunksTable, Player player, PacketContainer packet) {
    WrapperPlayServerBlockChange wrapped = new WrapperPlayServerBlockChange(packet);
    BlockPosition location = wrapped.getLocation();
    int x = location.getX();
    int y = location.getY();
    int z = location.getZ();

//    System.out.println(String.format("Processing Block change: (%s,%s,%s)", x, y, z));

    SectionTable sectionTable = chunksTable.getSectionTable(player.getWorld().getName(),
        x >> 4, y >> 4, z >> 4);
    BlockData block = sectionTable.get(x, y, z);
    if (block == null) {
      return;
    }
    WrappedBlockData data = wrapped.getBlockData();
    data.setType(block.getType());
    data.setData(block.getMetaData());
    wrapped.setBlockData(data);

//    System.out.println(String.format("Updated Block change: (%s,%s,%s) %s", x, y, z, block.getType()));
  }

  private void translateMultiBlockChange(ChunksTable chunksTable, Player player,
      PacketContainer packet) {
    World world = player.getWorld();
    WrapperPlayServerMultiBlockChange wrapped = new WrapperPlayServerMultiBlockChange(packet);
    for (MultiBlockChangeInfo record : wrapped.getRecords()) {
      Location location = record.getLocation(world);
      int x = location.getBlockX();
      int y = location.getBlockY();
      int z = location.getBlockZ();

//      System.out.println(String.format("Processing MultiBlock change: (%s,%s,%s)", x, y, z));

      SectionTable sectionTable = chunksTable.getSectionTable(player.getWorld().getName(),
          x >> 4, y >> 4, z >> 4);
      BlockData block = sectionTable.get(x, y, z);
      if (block == null) {
        continue;
      }
      WrappedBlockData data = record.getData();
      data.setType(block.getType());
      data.setData(block.getMetaData());
      record.setData(data);

//      System.out.println(String.format("Updated MultiBlock change: (%s,%s,%s)", x, y, z));
    }
  }

  public boolean add(TeamNew team, Location location, Material material, int metaData) {
    ChunksTable table = this.chunksMap.get(team);
    if (table == null) {
      table = new ChunksTable();
      this.chunksMap.put(team, table);
    }
    boolean result = table.add(location, material, metaData);
    if (result) {
      final WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();
      final int x = location.getBlockX();
      final int y = location.getBlockY();
      final int z = location.getBlockZ();
      BlockPosition pos = new BlockPosition(x, y, z);
      packet.setLocation(pos);
      WrappedBlockData data = WrappedBlockData.createData(material, metaData);
      packet.setBlockData(data);
      for (PlayerContext playerCtx : team.getPlayers()) {
        final Player player = playerCtx.getPlayer();
        if (player.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
          new BukkitRunnable() {
            @Override
            public void run() {
//              System.out.println(String.format("Sending BlockChange packet for (%s,%s,%s)", x, y, z));
              packet.sendPacket(player);
            }
          }.runTaskLater(plugin, 1);
        }
      }
    }
    return result;
  }

  public boolean remove(TeamNew team, Location location) {
    ChunksTable table = this.chunksMap.get(team);
    if (table == null) {
      return false;
    }
    BlockData result = table.remove(location);
    if (result != null) {
      this.resetBlock(team, location);
    }
    return result != null;
  }

  public void removeAll(Location location) {
    for (TeamNew team : this.chunksMap.keySet()) {
      ChunksTable table = this.chunksMap.get(team);
      if (table.remove(location) != null) {
        this.resetBlock(team, location);
      }
    }
  }

  private void resetBlock(TeamNew team, Location location) {
    Block block = location.getWorld().getBlockAt(location);
    final WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    BlockPosition pos = new BlockPosition(x, y, z);
    packet.setLocation(pos);
    WrappedBlockData data = WrappedBlockData.createData(block.getType(), block.getData());
    packet.setBlockData(data);
    for (PlayerContext playerCtx : team.getPlayers()) {
      final Player player = playerCtx.getPlayer();
      if (player.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
        new BukkitRunnable() {
          @Override
          public void run() {
//            System.out.println(String.format("Resetting with BlockChange packet for (%s,%s,%s)", x, y, z));
            packet.sendPacket(player);
          }
        }.runTaskLater(plugin, 1);
      }
    }
  }

}
