package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.game.Team;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum UpgradeArmorProtectionEnum implements UpgradeEnum {
    PROTECTION0(0, ""),
    PROTECTION1(1, "protection1"),
    PROTECTION2(2, "protection2"),
    PROTECTION3(3, "protection3"),
    PROTECTION4(4, "protection4");

    private final int level;
    private final String translationKey;

    UpgradeArmorProtectionEnum(int level, String translationKey) {
        this.level = level;
        this.translationKey = translationKey;
    }

    public boolean isHigherThan(UpgradeArmorProtectionEnum purchase) {
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
        Enchantment enchant = Enchantment.PROTECTION_ENVIRONMENTAL;
        for (ItemStack item : player.getInventory().getArmorContents()) {
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
