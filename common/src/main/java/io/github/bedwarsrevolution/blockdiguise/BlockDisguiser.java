package io.github.bedwarsrevolution.blockdiguise;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerMultiBlockChange;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter.AdapterParameteters;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.bedwarsrevolution.BedwarsRevol;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
 * Created by {maxos} 2017
 */
public class BlockDisguiser {

  private final BedwarsRevol plugin;
  private Map<TeamNew, ChunksTable> chunksMap = new ConcurrentHashMap<>();
  private Map<Player, ChunksTable> gogglesMap = new ConcurrentHashMap<>();
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
        ChunksTable chunksTable = gogglesMap.get(player);
        if (chunksTable == null) {
          chunksTable = chunksMap.get(team);
        }
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
        SectionTable sectionTable = chunksTable.getSectionTable(player.getWorld().getName(), chunkX, sectionY, chunkZ);
        sectionTable.process(getChunkBlocksProcessor(chunkX, chunkZ, player));
      }
    };
  }

  private ChunkBlocksProcessor getChunkBlocksProcessor(final int chunkX, final int chunkZ, final Player player) {
    return new ChunkBlocksProcessor() {
      @Override
      public void process(Collection<BlockData> data) {
        if (data.size() == 0) {
          return;
        }
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
            if (player.getWorld().isChunkLoaded(chunkX, chunkZ)) {
              packet.sendPacket(player);
            }
          }
        }.runTaskLater(plugin, 1);
      }
    };
  }

  private void translateBlockChange(ChunksTable chunksTable, Player player, PacketContainer packet) {
    WrapperPlayServerBlockChange wrapped = new WrapperPlayServerBlockChange(packet);
    BlockPosition location = wrapped.getLocation();
    BlockData block = this.getBlockData(chunksTable, player.getWorld(), location);
    if (block == null) {
      return;
    }
    WrappedBlockData data = wrapped.getBlockData();
    data.setType(block.getType());
    data.setData(block.getMetaData());
    wrapped.setBlockData(data);
  }

  private void translateMultiBlockChange(ChunksTable chunksTable, Player player, PacketContainer packet) {
    World world = player.getWorld();
    WrapperPlayServerMultiBlockChange wrapped = new WrapperPlayServerMultiBlockChange(packet);
    for (MultiBlockChangeInfo record : wrapped.getRecords()) {
      Location location = record.getLocation(world);
      BlockData block = getBlockData(chunksTable, world, location);
      if (block == null) {
        continue;
      }
      WrappedBlockData data = record.getData();
      data.setType(block.getType());
      data.setData(block.getMetaData());
      record.setData(data);
    }
  }

  private BlockData getBlockData(ChunksTable chunksTable, World world, Location location) {
    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();
    return this.getBlockData(chunksTable, world, x, y, z);
  }

  private BlockData getBlockData(ChunksTable chunksTable, World world, BlockPosition location) {
    int x = location.getX();
    int y = location.getY();
    int z = location.getZ();
    return this.getBlockData(chunksTable, world, x, y, z);
  }

  private BlockData getBlockData(ChunksTable chunksTable, World world, int x, int y, int z) {
    SectionTable sectionTable = chunksTable.getSectionTable(world.getName(),
        x >> 4, y >> 4, z >> 4);
    return sectionTable.get(x, y, z);
  }

  public boolean addBlock(TeamNew team, Location location, Material material, int metaData) {
    ChunksTable table = this.chunksMap.get(team);
    if (table == null) {
      table = new ChunksTable();
      this.chunksMap.put(team, table);
    }
    boolean result = table.add(location, material, metaData);
    if (result) {
      BlockPosition pos = this.locationToPosition(location);
      WrapperPlayServerBlockChange packet = this.makePacket(pos, material, metaData);
      Set<Player> served = this.gogglesMap.keySet();
      for (Player player : served) {
        this.gogglesMap.get(player).add(location, material, metaData);
        this.sendBlock(player, packet, pos);
      }
      for (PlayerContext playerCtx : team.getPlayers()) {
        final Player player = playerCtx.getPlayer();
        if (!served.contains(player)) {
          this.sendBlock(player, packet, pos);
        }
      }
    }
    return result;
  }

  public boolean removeBlock(TeamNew team, Location location) {
    ChunksTable table = this.chunksMap.get(team);
    if (table == null) {
      return false;
    }
    BlockData result = table.remove(location);
    if (result != null) {
      this.resetBlock(team, location);
      this.removeAllGogglesUsers(location);
    }
    return result != null;
  }

  public void removeAllBlocks(Location location) {
    for (TeamNew team : this.chunksMap.keySet()) {
      ChunksTable table = this.chunksMap.get(team);
      if (table.remove(location) != null) {
        this.resetBlock(team, location);
      }
    }
    this.removeAllGogglesUsers(location);
  }

  private void resetBlock(TeamNew team, Location location) {
    Block block = location.getWorld().getBlockAt(location);
    BlockPosition pos = this.locationToPosition(location);
    WrapperPlayServerBlockChange packet = this.makePacket(pos, block.getType(), block.getData());
    for (PlayerContext playerCtx : team.getPlayers()) {
      final Player player = playerCtx.getPlayer();
      this.sendBlock(player, packet, pos);
    }
  }

  private BlockPosition locationToPosition(Location location) {
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    return new BlockPosition(x, y, z);
  }

  private WrapperPlayServerBlockChange makePacket(BlockPosition pos, Material material, int metaData) {
    final WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();
    packet.setLocation(pos);
    WrappedBlockData data = WrappedBlockData.createData(material, metaData);
    packet.setBlockData(data);
    return packet;
  }

//  private void sendBlock(Player player, Location location, Material material, int metaData) {
//    BlockPosition pos = this.locationToPosition(location);
//    this.sendBlock(player, pos, material, metaData);
//  }
//
//  private void sendBlock(final Player player, BlockPosition pos, Material material, int metaData) {
//    final WrapperPlayServerBlockChange packet = this.makePacket(pos, material, metaData);
//    this.sendBlock(player, packet, pos);
//  }

  private void sendBlock(final Player player, final WrapperPlayServerBlockChange packet, BlockPosition pos) {
    if (player.getWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
      new BukkitRunnable() {
        @Override
        public void run() {
          packet.sendPacket(player);
        }
      }.runTaskLater(plugin, 1);
    }
  }

  public void reset() {
    this.chunksMap = new ConcurrentHashMap<>();
    this.gogglesMap = new ConcurrentHashMap<>();
  }

  private void removeAllGogglesUsers(Location location) {
    boolean found = false;
    for (ChunksTable table : this.chunksMap.values()) {
      if (this.getBlockData(table, location.getWorld(), location) != null) {
        found = true;
        break;
      }
    }
    if (!found) {
      Iterator<Entry<Player, ChunksTable>> iter = this.gogglesMap.entrySet().iterator();
      Block block = location.getWorld().getBlockAt(location);
      BlockPosition pos = this.locationToPosition(location);
      WrapperPlayServerBlockChange packet = this.makePacket(pos, block.getType(), block.getData());
      while (iter.hasNext()) {
        Entry<Player, ChunksTable> entry = iter.next();
        ChunksTable table = entry.getValue();
        BlockData removed = table.remove(location);
        if (removed != null) {
          this.sendBlock(entry.getKey(), packet, pos);
        }
        if (table.isEmpty()) {
          iter.remove();
        }
      }
    }
  }

  public void addGogglesUser(PlayerContext playerCtx) {
    Player player = playerCtx.getPlayer();
    ChunksTable chunksTable = merge(this.chunksMap.values(), Material.REDSTONE_BLOCK, 0);
    this.gogglesMap.put(player, chunksTable);
    Multimap<ChunkCoordinate, BlockData> chunks = ArrayListMultimap.create();
    for (Entry<SectionCoordinate, SectionTable> entry : chunksTable.getMap().entrySet()) {
      SectionCoordinate coord = entry.getKey();
      for (BlockData block : entry.getValue().getAll()) {
        chunks.put(ChunkCoordinate.fromChunk(coord.getWorld(), coord.getChunkX(), coord.getChunkZ()), block);
      }
    }
    for (ChunkCoordinate chunk : chunks.keySet()) {
      ChunkBlocksProcessor processor = this.getChunkBlocksProcessor(chunk.getChunkX(), chunk.getChunkZ(), player);
      processor.process(chunks.get(chunk));
    }
  }

  public ChunksTable mergeChunks(Material material, int metaData) {
    ChunksTable res = new ChunksTable();
    for (Entry<TeamNew, ChunksTable> chunkEntry : this.chunksMap.entrySet()) {
      TeamNew team = chunkEntry.getKey();
      for (Entry<SectionCoordinate, SectionTable> entry : table.map.entrySet()) {
        SectionCoordinate sectionCoord = entry.getKey();
        for (BlockData block : entry.getValue().getAll()) {
          res.add(sectionCoord.getWorld(), block.getX(), block.getY(), block.getZ(),
              material, metaData);
        }
      }
    }
    return res;
  }

  public void removeGogglesUser(PlayerContext playerCtx) {
    Player player = playerCtx.getPlayer();
    ChunksTable teamTable = this.chunksMap.get(playerCtx.getTeam());
    World world = player.getWorld();

    ChunksTable chunksTable = this.gogglesMap.remove(player);
    if (chunksTable == null) {
      return;
    }
    Multimap<ChunkCoordinate, BlockData> chunks = ArrayListMultimap.create();
    for (Entry<SectionCoordinate, SectionTable> entry : chunksTable.getMap().entrySet()) {
      SectionCoordinate coord = entry.getKey();
      if (!coord.getWorld().equals(world.getName())) {
        continue;
      }
      for (BlockData blockData : entry.getValue().getAll()) {
        int x = blockData.getX();
        int y = blockData.getY();
        int z = blockData.getZ();
        BlockData resetBlock = null;
        if (teamTable != null) {
          SectionTable teamSection = teamTable
              .getSectionTable(world.getName(), coord.getChunkX(), coord.getSectionY(), coord.getChunkZ());
          resetBlock = teamSection.get(x, y, z);
          if (teamSection.get(x, y, z) != null) {
            continue;
          }
        }
        Block block = world.getBlockAt(x, y, z);
        if (resetBlock == null) {
          resetBlock = new BlockData(x, y, z, block.getType(), block.getData());
        }
        chunks.put(ChunkCoordinate.fromChunk(coord.getWorld(), coord.getChunkX(), coord.getChunkZ()), resetBlock);
      }
    }
    for (ChunkCoordinate chunk : chunks.keySet()) {
      ChunkBlocksProcessor processor = this.getChunkBlocksProcessor(chunk.getChunkX(), chunk.getChunkZ(), player);
      processor.process(chunks.get(chunk));
    }
  }
}
