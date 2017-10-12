package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;

public enum UpgradeForgeEnum implements UpgradeEnum {
    FORGE0,
    FORGE1,
    FORGE2,
    FORGE3,
    FORGE4;

    public boolean isHigherThan(UpgradeForgeEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public void equipTeam(Game game, Team team) {
//        game.getUpgrades(ForgeUpgradeEnum.class);
        System.out.println("Equipping team " + team.getName() + " with " + this);
    }

}
