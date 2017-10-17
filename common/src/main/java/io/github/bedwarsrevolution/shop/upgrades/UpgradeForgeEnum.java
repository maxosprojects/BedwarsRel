package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;

public enum UpgradeForgeEnum implements UpgradeEnum {
    FORGE0,
    FORGE1,
    FORGE2,
    FORGE3,
    FORGE4;

    public boolean isHigherThan(UpgradeForgeEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public void equipTeam(GameContext game, TeamNew team) {
//        game.getUpgrades(ForgeUpgradeEnum.class);
        System.out.println("Equipping team " + team.getName() + " with " + this);
    }

}
