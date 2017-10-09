package io.github.bedwarsrel.shop.Specials;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public enum PermanentItemEnum {
    WOOD_SWORD(Material.WOOD_SWORD, "wood-sword"),
    SHEARS(Material.SHEARS, "shears");

    private final Material representation;
    private final String translationKey;

    PermanentItemEnum(Material representation, String translationKey) {
        this.representation = representation;
        this.translationKey = translationKey;
    }

    public boolean isHigherThan(PermanentItemEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public Material getRepresentation() {
        return representation;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public void equipPlayer(Player player) {
        Inventory inv = player.getInventory();
        if (inv.contains(this.representation)) {
            return;
        }
        ItemStack item = new ItemStack(this.representation);
        inv.addItem(item);
        player.updateInventory();
    }
}
