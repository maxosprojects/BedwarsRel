package io.github.bedwarsrel.shop.Specials;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.inventory.ItemStack;

public interface VirtualItem {
    /**
     * Creates and returns a new uninitialized (not yet added to the game) @{@link VirtualItem}
     *
     * @param game
     * @param team
     * @return
     */
    VirtualItem create(Game game, Team team);

    /**
     * Initializes the item and adds it to the game.
     *
     * @return whether item initialization was successful (e.g. was successfully added to the game).
     */
    boolean init();

    boolean isRepresentation(ItemStack item);
}
