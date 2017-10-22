package io.github.bedwarsrevolution.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.HashMap;
import java.util.Map;

public enum UpgradeForgeEnum implements UpgradeEnum {
    FORGE0(new HashMap<String, Double>()),
    FORGE1(ImmutableMap.of("iron", 1.5)),
    FORGE2(ImmutableMap.of("iron", 1.5)),
    FORGE3(ImmutableMap.of("iron", 1.5)),
    FORGE4(ImmutableMap.of("iron", 1.5));

    private final Map<String, Double> divisors;

    UpgradeForgeEnum(Map<String, Double> divisors) {
        this.divisors = divisors;
    }

    public boolean isHigherThan(UpgradeForgeEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public void equipTeam(GameContext game, TeamNew team) {
//        game.getUpgrades(ForgeUpgradeEnum.class);
        System.out.println("Equipping team " + team.getName() + " with " + this);
    }

}
