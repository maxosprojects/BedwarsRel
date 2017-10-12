package io.github.bedwarsrel.shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerStorage;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Reward;
import io.github.bedwarsrel.utils.ChatWriter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorPurchase extends SpecialItem implements VirtualItem {
    private Game game;
    private Player player;
    private final ArmorPurchaseEnum purchase;

    public ArmorPurchase(ArmorPurchaseEnum purchase) {
        this.purchase = purchase;
    }

    @Override
    public VirtualItem create(Game game, Team team, Player player) {
        ArmorPurchase item = new ArmorPurchase(this.purchase);

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
        this.purchase.equipPlayer(this.player, team);

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
    public boolean isRepresentation(Reward holder) {
        ItemStack item = holder.getItem();
        return item != null && item.getType() == purchase.getRepresentation();
    }
}
