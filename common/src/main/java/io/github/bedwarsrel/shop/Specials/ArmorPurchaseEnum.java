package io.github.bedwarsrel.shop.Specials;

import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.game.TeamColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum ArmorPurchaseEnum {
    LEATHER(Material.LEATHER_LEGGINGS,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            "leather"),
    CHAINMAIL(Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            "chainmail"),
    IRON(Material.IRON_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            "iron"),
    DIAMOND(Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            "diamond");

    private final Material representation;
    private final Material leggings;
    private final Material boots;
    private final String translationKey;

    ArmorPurchaseEnum(Material representation, Material leggings, Material boots, String translationKey) {
        this.representation = representation;
        this.leggings = leggings;
        this.boots = boots;
        this.translationKey = translationKey;
    }

    public boolean isHigherThan(ArmorPurchaseEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public Material getRepresentation() {
        return representation;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public void equipPlayer(Player player, Team team) {
        TeamColor color = team.getColor();

        ItemStack leggings = new ItemStack(this.leggings, 1);
        ItemStack boots = new ItemStack(this.boots, 1);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);

        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(color.getColor());
        helmet.setItemMeta(meta);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(color.getColor());
        chestplate.setItemMeta(meta);

        if (this.representation == Material.LEATHER_LEGGINGS) {
            meta = (LeatherArmorMeta) leggings.getItemMeta();
            meta.setColor(color.getColor());
            leggings.setItemMeta(meta);

            meta = (LeatherArmorMeta) boots.getItemMeta();
            meta.setColor(color.getColor());
            boots.setItemMeta(meta);
        }

        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);

        player.updateInventory();

        team.getArmorUpgrade().equipPlayer(player);
    }
}
