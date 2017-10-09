package io.github.bedwarsrel.shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwordUpgrade extends SpecialItem implements VirtualItem {
    private Game game;
    private Team team;
    private final SwordUpgradeEnum upgrade;

    public SwordUpgrade(SwordUpgradeEnum upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public VirtualItem create(Game game, Team team, Player player) {
        SwordUpgrade item = new SwordUpgrade(this.upgrade);

        item.game = game;
        item.team = team;

        return item;
    }

    @Override
    public boolean init() {
        SwordUpgradeEnum existingUpgrade = this.team.getSwordUpgrade();

        if (!this.upgrade.isHigherThan(existingUpgrade)) {
            return false;
        }

        team.setSwordUpgrade(this.upgrade);

        this.upgrade.equipTeam(this.team);

        for (Player player : this.team.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            String translation = BedwarsRel._l(player, "ingame.swordupgrade." + this.upgrade.getTranslationKey());
            player.sendMessage(ChatWriter.pluginMessage(
                    BedwarsRel._l(player, "success.swordsupgraded",
                            ImmutableMap.of("upgrade", translation))));
        }

        return true;
    }

    @Override
    public Material getActivatedMaterial() {
        return null;
    }

    public Game getGame() {
        return this.game;
    }

    @Override
    public Material getItemMaterial() {
        return null;
    }

    @Override
    public boolean isRepresentation(ItemStack item) {
        return item.getType() == upgrade.getRepresentation();
    }
}
