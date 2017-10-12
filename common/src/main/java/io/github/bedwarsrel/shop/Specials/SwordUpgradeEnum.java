package io.github.bedwarsrel.shop.Specials;

import io.github.bedwarsrel.game.Team;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SwordUpgradeEnum implements UpgradeEnum {
    SHARPNESS0(null, 0, ""),
    SHARPNESS1(Material.GOLD_SWORD, 1, "sharpness1");

    private final Material representation;
    private final int level;
    private final String translationKey;

    SwordUpgradeEnum(Material representation, int level, String translationKey) {
        this.representation = representation;
        this.level = level;
        this.translationKey = translationKey;
    }

    public boolean isHigherThan(SwordUpgradeEnum purchase) {
        return this.ordinal() > purchase.ordinal();
    }

    public Material getRepresentation() {
        return representation;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public void equipTeam(Team team) {
        for (Player player : team.getPlayers()) {
            this.equipPlayer(player);
        }
    }

    public void equipPlayer(Player player) {
        if (this.level == 0) {
            return;
        }
        Enchantment enchant = Enchantment.DAMAGE_ALL;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null
                || (item.getType() != Material.WOOD_SWORD
                    && item.getType() != Material.STONE_SWORD
                    && item.getType() != Material.IRON_SWORD
                    && item.getType() != Material.GOLD_SWORD
                    && item.getType() != Material.DIAMOND_SWORD)) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            meta.removeEnchant(enchant);
            meta.addEnchant(enchant, this.level, true);
            item.setItemMeta(meta);
        }
        player.updateInventory();
    }

  public int getLevel() {
    return level;
  }
}
