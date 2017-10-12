package io.github.bedwarsrel.game;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerJoinTeamEvent;
import io.github.bedwarsrel.events.BedwarsPlayerSetNameEvent;
import io.github.bedwarsrel.shop.Specials.ArmorUpgradeEnum;
import io.github.bedwarsrel.shop.Specials.SwordUpgradeEnum;
import io.github.bedwarsrel.shop.Specials.UpgradeEnum;
import io.github.bedwarsrel.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@Data
@SerializableAs("Team")
public class Team implements ConfigurationSerializable {

    private List<Block> chests = null;
    private TeamColor color = null;
    private Inventory inventory = null;
    private int maxPlayers = 0;
    private String name = null;
    private org.bukkit.scoreboard.Team scoreboardTeam = null;
    private Location spawnLocation = null;
    private Location targetFeetBlock = null;
    private Location targetHeadBlock = null;
    private Location baseLoc1;
    private Location baseLoc2;
    private Location chestLoc;
    private List<UpgradeEnum> upgrades = new ArrayList<>();

    public Team(Map<String, Object> deserialize) {
        this.reset();
        this.setName(deserialize.get("name").toString());
        this.setMaxPlayers(Integer.parseInt(deserialize.get("maxplayers").toString()));
        this.setColor(TeamColor.valueOf(deserialize.get("color").toString().toUpperCase()));
        this.setSpawnLocation(Utils.locationDeserialize(deserialize.get("spawn")));
        this.setChests(new ArrayList<Block>());
        this.setBaseLoc1(Utils.locationDeserialize(deserialize.get("baseloc1")));
        this.setBaseLoc2(Utils.locationDeserialize(deserialize.get("baseloc2")));
        this.setChestLoc(Utils.locationDeserialize(deserialize.get("chestloc")));

        if (deserialize.containsKey("bedhead")) {
            this.setTargetHeadBlock(Utils.locationDeserialize(deserialize.get("bedhead")));

            if (this.getTargetHeadBlock() != null && deserialize.containsKey("bedfeed")
                    && this.getTargetHeadBlock().getBlock().getType().equals(Material.BED_BLOCK)) {
                this.setTargetFeetBlock(Utils.locationDeserialize(deserialize.get("bedfeed")));
            }
        }
    }

    public Team(String name, TeamColor color, int maxPlayers,
                org.bukkit.scoreboard.Team scoreboardTeam) {
        this.reset();
        this.setName(name);
        this.setColor(color);
        this.setMaxPlayers(maxPlayers);
        this.setScoreboardTeam(scoreboardTeam);
        this.setChests(new ArrayList<Block>());
    }

    public void reset() {
        this.inventory = null;
        this.chests = new ArrayList<>();
        this.upgrades = new ArrayList<>();
        this.upgrades.add(ArmorUpgradeEnum.PROTECTION0);
        this.upgrades.add(SwordUpgradeEnum.SHARPNESS0);
    }

    public void addChest(Block chestBlock) {
        this.getChests().add(chestBlock);
    }

    @SuppressWarnings("deprecation")
    public boolean addPlayer(Player player) {

        BedwarsPlayerJoinTeamEvent playerJoinTeamEvent = new BedwarsPlayerJoinTeamEvent(this, player);
        BedwarsRel.getInstance().getServer().getPluginManager().callEvent(playerJoinTeamEvent);

        if (playerJoinTeamEvent.isCancelled()) {
            return false;
        }

        if (BedwarsRel.getInstance().isSpigot()) {
            if (this.getScoreboardTeam().getEntries().size() >= this.getMaxPlayers()) {
                return false;
            }
        } else {
            if (this.getScoreboardTeam().getPlayers().size() >= this.getMaxPlayers()) {
                return false;
            }
        }

        String displayName = player.getDisplayName();
        String playerListName = player.getPlayerListName();

        if (BedwarsRel.getInstance().getBooleanConfig("overwrite-names", false)) {
            displayName = this.getChatColor() + ChatColor.stripColor(player.getName());
            playerListName = this.getChatColor() + ChatColor.stripColor(player.getName());
        }

        if (BedwarsRel.getInstance().getBooleanConfig("teamname-on-tab", true)) {
            playerListName = this.getChatColor() + this.getName() + ChatColor.WHITE + " | "
                    + this.getChatColor() + ChatColor.stripColor(player.getDisplayName());
        }

        BedwarsPlayerSetNameEvent playerSetNameEvent =
                new BedwarsPlayerSetNameEvent(this, displayName, playerListName, player);
        BedwarsRel.getInstance().getServer().getPluginManager().callEvent(playerSetNameEvent);

        if (!playerSetNameEvent.isCancelled()) {
            player.setDisplayName(playerSetNameEvent.getDisplayName());
            player.setPlayerListName(playerSetNameEvent.getPlayerListName());
        }

        if (BedwarsRel.getInstance().isSpigot()) {
            this.getScoreboardTeam().addEntry(player.getName());
        } else {
            this.getScoreboardTeam().addPlayer(player);
        }
        BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player).getPlayerStorage(player).prepareForBattle();

        return true;
    }

    public void createTeamInventory() {
        Inventory inventory =
                Bukkit.createInventory(null, InventoryType.ENDER_CHEST, BedwarsRel._l("ingame.teamchest"));
        this.setInventory(inventory);
    }

    public ChatColor getChatColor() {
        return this.getColor().getChatColor();
    }

    public String getDisplayName() {
        return this.getScoreboardTeam().getDisplayName();
    }

    public Block getFeetTarget() {
        if (this.getTargetFeetBlock() == null) {
            return null;
        }

        this.getTargetFeetBlock().getBlock().getChunk().load(true);
        return this.getTargetFeetBlock().getBlock();
    }

    public Block getHeadTarget() {
        if (this.targetHeadBlock == null) {
            return null;
        }

        this.getTargetHeadBlock().getBlock().getChunk().load(true);
        return this.getTargetHeadBlock().getBlock();
    }

    @SuppressWarnings("deprecation")
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        if (BedwarsRel.getInstance().isSpigot()) {
            for (String aPlayer : this.getScoreboardTeam().getEntries()) {
                Player player = BedwarsRel.getInstance().getServer().getPlayer(aPlayer);
                if (player != null
                        && BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player) != null
                        && !BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player)
                        .isSpectator(player)) {
                    players.add(player);
                }
            }
        } else {
            for (OfflinePlayer offlinePlayer : this.getScoreboardTeam().getPlayers()) {
                Player player = BedwarsRel.getInstance().getServer().getPlayer(offlinePlayer.getName());
                if (player != null
                        && BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player) != null
                        && !BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player)
                        .isSpectator(player)) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public boolean isBedDestroyed(Game game) {
        Material targetMaterial = game.getTargetMaterial();

        this.getTargetHeadBlock().getBlock().getChunk().load(true);
        if (this.getTargetFeetBlock() == null) {
            return this.getTargetHeadBlock().getBlock().getType() != targetMaterial;
        }

        this.getTargetFeetBlock().getBlock().getChunk().load(true);
        return (this.getTargetHeadBlock().getBlock().getType() != targetMaterial
                && this.getTargetFeetBlock().getBlock().getType() != targetMaterial);
    }

    @SuppressWarnings("deprecation")
    public boolean isInTeam(Player p) {
        if (BedwarsRel.getInstance().isSpigot()) {
            return this.getScoreboardTeam().hasEntry(p.getName());
        } else {
            return this.getScoreboardTeam().hasPlayer(p);
        }
    }

    public void removeChest(Block chest) {
        this.getChests().remove(chest);
        if (this.getChests().size() == 0) {
            this.setInventory(null);
        }
    }

    @SuppressWarnings("deprecation")
    public void removePlayer(Player player) {
        if (BedwarsRel.getInstance().isSpigot()) {
            if (this.getScoreboardTeam().hasEntry(player.getName())) {
                this.getScoreboardTeam().removeEntry(player.getName());
            }
        } else {
            if (this.getScoreboardTeam().hasPlayer(player)) {
                this.getScoreboardTeam().removePlayer(player);
            }
        }

        if (BedwarsRel.getInstance().getBooleanConfig("overwrite-names", false) && player.isOnline()) {
            player.setDisplayName(ChatColor.RESET + ChatColor.stripColor(player.getName()));
            player.setPlayerListName(ChatColor.RESET + player.getPlayer().getName());
        }
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> team = new HashMap<>();

        team.put("name", this.getName());
        team.put("color", this.getColor().toString());
        team.put("maxplayers", this.getMaxPlayers());
        team.put("spawn", Utils.locationSerialize(this.getSpawnLocation()));
        team.put("bedhead", Utils.locationSerialize(this.getTargetHeadBlock()));
        team.put("baseloc1", Utils.locationSerialize(this.baseLoc1));
        team.put("baseloc2", Utils.locationSerialize(this.baseLoc2));
        team.put("chestloc", Utils.locationSerialize(this.chestLoc));

        if (this.targetFeetBlock != null) {
            team.put("bedfeed", Utils.locationSerialize(this.targetFeetBlock));
        }

        return team;
    }

    public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
        scoreboardTeam.setDisplayName(this.getChatColor() + this.name);
        this.scoreboardTeam = scoreboardTeam;
    }

    public void setTargets(Block headBlock, Block feetBlock) {
        this.setTargetHeadBlock(headBlock.getLocation());
        if (feetBlock != null) {
            this.setTargetFeetBlock(feetBlock.getLocation());
        } else {
            this.setTargetFeetBlock(null);
        }
    }

    public void setBaseLoc(Location loc, String type) {
        if (type.equalsIgnoreCase("loc1")) {
            this.baseLoc1 = loc;
        } else {
            this.baseLoc2 = loc;
        }
    }

    public <T> T getUpgrade(Class<T> upgradeEnumClass) {
        for (UpgradeEnum upgrade : this.upgrades) {
            if (upgrade.getClass() == upgradeEnumClass) {
                return (T)upgrade;
            }
        }
        return null;
    }

    public void setUpgrade(UpgradeEnum upgrade) {
        // First remove existing upgrade of the same enum
        Iterator<UpgradeEnum> iter = this.upgrades.iterator();
        while (iter.hasNext()) {
            UpgradeEnum temp = iter.next();
            if (temp.getClass() == upgrade.getClass()) {
                iter.remove();
            }
        }
        this.upgrades.add(upgrade);
    }

}
