package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
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

  public boolean isHigherThan(
      UpgradeSwordSharpnessEnum purchase) {
    return this.ordinal() > purchase.ordinal();
  }

  public String getTranslationKey() {
    return this.translationKey;
  }

  public void equipTeam(TeamNew team) {
    for (PlayerContext playerCtx : team.getPlayers()) {
      this.equipPlayer(playerCtx);
    }
    this.equipInventory(team.getInventory());
  }

  public void equipPlayer(PlayerContext playerCtx) {
    if (this.level == 0) {
      return;
    }
    this.equipInventory(playerCtx.getPlayer().getInventory());
    playerCtx.getPlayer().updateInventory();
  }

  private void equipInventory(Inventory inventory) {
    Enchantment enchant = Enchantment.DAMAGE_ALL;
    for (ItemStack item : inventory.getContents()) {
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
  }

  public int getLevel() {
    return level;
  }
}
