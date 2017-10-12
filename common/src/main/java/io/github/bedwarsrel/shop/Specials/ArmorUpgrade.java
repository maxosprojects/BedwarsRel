package io.github.bedwarsrel.shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Reward;
import io.github.bedwarsrel.utils.ChatWriter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ArmorUpgrade extends SpecialItem implements VirtualItem, Upgrade {
    private Game game;
    private Team team;
    private final ArmorUpgradeEnum upgrade;

    public ArmorUpgrade(ArmorUpgradeEnum upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public VirtualItem create(Game game, Team team, Player player) {
        ArmorUpgrade item = new ArmorUpgrade(this.upgrade);

        item.game = game;
        item.team = team;

        return item;
    }

    @Override
    public boolean init() {
        ArmorUpgradeEnum existingUpgrade = this.team.getUpgrade(ArmorUpgradeEnum.class);

        if (!this.upgrade.isHigherThan(existingUpgrade)) {
            return false;
        }

        team.setUpgrade(this.upgrade);

        this.upgrade.equipTeam(this.team);

        for (Player player : this.team.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            String translation = BedwarsRel._l(player, "ingame.armorupgrade." + this.upgrade.getTranslationKey());
            player.sendMessage(ChatWriter.pluginMessage(
                    BedwarsRel._l(player, "success.armorupgraded",
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
    public boolean isRepresentation(Reward holder) {
        return holder.isUpgrade() && holder.getUpgrade().getScope() == this.getScope();
    }

    @Override
    public UpgradeType getScope() {
        return UpgradeType.TEAM;
    }

    @Override
    public boolean matches(String type, int level) {
        return type != null && type.equals("ARMOR") && level == this.upgrade.getLevel();
    }

}
