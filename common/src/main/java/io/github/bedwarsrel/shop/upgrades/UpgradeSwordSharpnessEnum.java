package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.game.Team;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum UpgradeSwordSharpnessEnum implements UpgradeEnum {
    SHARPNESS0(0, ""),
    SHARPNESS1(1, "sharpness1");

    private final int level;
    private final String translationKey;

    UpgradeSwordSharpnessEnum(int level, String translationKey) {
        this.level = level;
        this.translationKey = translationKey;
    }

    public boolean isHigherThan(UpgradeSwordSharpnessEnum purchase) {
        return this.ordinal() > purchase.ordinal();
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
