package io.github.bedwarsrel.game;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class DamageHolder {
    // Interval of time the damage is considered recent
    private static long RECENT_INTERVAL = 5000;

    private final Player damager;
    private final long happenedAt;

    public boolean wasCausedRecently() {
        return (happenedAt + RECENT_INTERVAL) > System.currentTimeMillis();
    }
}
