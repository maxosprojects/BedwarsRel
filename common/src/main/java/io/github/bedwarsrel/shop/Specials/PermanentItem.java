package io.github.bedwarsrel.shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerStorage;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.ShopReward;
import io.github.bedwarsrel.utils.ChatWriter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

public class PermanentItem extends SpecialItem implements VirtualItem {
    private Game game;
    private Player player;
    private final PermanentItemEnum purchase;

    public PermanentItem(PermanentItemEnum purchase) {
        this.purchase = purchase;
    }

    @Override
    public VirtualItem create(Game game, Team team, Player player) {
        PermanentItem item = new PermanentItem(this.purchase);

        item.game = game;
        item.player = player;

        return item;
    }

    @Override
    public boolean init() {
        PlayerStorage storage = this.game.getPlayerStorage(this.player);
        Set<PermanentItemEnum> existingItems = storage.getPermanentItems();

        if (existingItems.contains(this.purchase)) {
            return false;
        }

        storage.addPermanentItem(this.purchase);

        this.purchase.equipPlayer(this.player);

        String translation = BedwarsRel._l(player, "ingame.permanentitems." + this.purchase.getTranslationKey());
        this.player.sendMessage(ChatWriter.pluginMessage(
                BedwarsRel._l(this.player, "success.permanentitempurchased",
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
    public boolean isRepresentation(ShopReward holder) {
        return !holder.isUpgrade() && holder.getItem().getType() == purchase.getRepresentation();
    }
}
