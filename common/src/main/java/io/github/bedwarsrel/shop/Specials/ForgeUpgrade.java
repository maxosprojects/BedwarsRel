package io.github.bedwarsrel.shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.ShopReward;
import io.github.bedwarsrel.utils.ChatWriter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ForgeUpgrade extends SpecialItem implements VirtualItem, Upgrade {
    private Game game;
    private Team team;
    private final ForgeUpgradeEnum upgrade;

    public ForgeUpgrade(ForgeUpgradeEnum upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public VirtualItem create(Game game, Team team, Player player) {
        ForgeUpgrade item = new ForgeUpgrade(this.upgrade);

        item.game = game;
        item.team = team;

        return item;
    }

    @Override
    public boolean init() {
        ForgeUpgradeEnum existingUpgrade = this.team.getUpgrade(ForgeUpgradeEnum.class);

        if (!this.upgrade.isHigherThan(existingUpgrade)) {
            return false;
        }

        team.setUpgrade(this.upgrade);

        this.upgrade.equipTeam(this.game, this.team);

        for (Player player : this.team.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            String translation = BedwarsRel._l(player, "ingame.forgeupgrade.forge" + upgrade.ordinal());
            player.sendMessage(ChatWriter.pluginMessage(
                    BedwarsRel._l(player, "success.forgeupgraded",
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
    public boolean isRepresentation(ShopReward holder) {
        return holder.isUpgrade() && holder.getUpgrade().getScope() == this.getScope();
    }

    @Override
    public UpgradeType getScope() {
        return UpgradeType.TEAM;
    }

    @Override
    public boolean matches(String type, int level) {
        return type != null && type.equals("FORGE") && level == this.upgrade.ordinal();
    }
}
