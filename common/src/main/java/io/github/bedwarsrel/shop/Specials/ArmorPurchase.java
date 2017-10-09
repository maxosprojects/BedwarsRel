package io.github.bedwarsrel.shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerStorage;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Serves as a trap base-wise, i.e. when a player enters another team's base (crosses
 * the boundary of the base) that has this trap installed the trap is activated.
 * This is superior to the @{@link Trap} because it can't be jumped over nor anything
 * can be placed on top of the trap.
 */
public class ArmorPurchase extends SpecialItem implements VirtualItem {
    private Game game;
    private Player player;
    private final ArmorPurchaseEnum purchase;

    public ArmorPurchase(ArmorPurchaseEnum purchase) {
        this.purchase = purchase;
    }

    @Override
    public VirtualItem create(Game game, Team team, Player player) {
        ArmorPurchase item = new ArmorPurchase(purchase);

        item.game = game;
        item.player = player;

        return item;
    }

    @Override
    public boolean init() {
        PlayerStorage storage = this.game.getPlayerStorage(this.player);
        ArmorPurchaseEnum existingArmor = storage.getInGameArmor();

        if (!this.purchase.isHigherThan(existingArmor)) {
            return false;
        }

        storage.setIngameArmor(this.purchase);

        Team team = this.game.getPlayerTeam(this.player);
        this.purchase.equipPlayer(this.player, team.getColor());

        String translation = BedwarsRel._l(player, "ingame.armor." + this.purchase.getTranslationKey());
        this.player.sendMessage(ChatWriter.pluginMessage(
                BedwarsRel._l(this.player, "success.armorpurchased",
                        ImmutableMap.of("type", translation))));

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
        return item.getType() == purchase.getRepresentation();
    }
}
