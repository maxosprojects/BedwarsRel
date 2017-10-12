package io.github.bedwarsrel.shop.Specials;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;

public enum ForgeUpgradeEnum implements UpgradeEnum {
    FORGE0,
    FORGE1,
    FORGE2,
    FORGE3,
    FORGE4;

    public boolean isHigherThan(ForgeUpgradeEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public void equipTeam(Game game, Team team) {
//        game.getUpgrade(ForgeUpgradeEnum.class);
        System.out.println("Equipping team " + team.getName() + " with " + this);
    }

}
