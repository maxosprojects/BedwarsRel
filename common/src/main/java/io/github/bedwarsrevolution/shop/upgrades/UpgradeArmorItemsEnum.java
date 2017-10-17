package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.MerchantCategory;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum UpgradeArmorItemsEnum {
  LEATHER(Material.LEATHER_LEGGINGS,
      Material.LEATHER_BOOTS,
      "leather"),
  CHAINMAIL(Material.CHAINMAIL_LEGGINGS,
      Material.CHAINMAIL_BOOTS,
      "chainmail"),
  IRON(Material.IRON_LEGGINGS,
      Material.IRON_BOOTS,
      "iron"),
  DIAMOND(Material.DIAMOND_LEGGINGS,
      Material.DIAMOND_BOOTS,
      "diamond");

  private final Material leggings;
  private final Material boots;
  private final String translationKey;

  UpgradeArmorItemsEnum(Material leggings, Material boots, String translationKey) {
    this.leggings = leggings;
    this.boots = boots;
    this.translationKey = translationKey;
  }

  public boolean isHigherThan(UpgradeArmorItemsEnum purchase) {
    return this.ordinal() > purchase.ordinal();
  }

  public String getTranslationKey() {
    return this.translationKey;
  }

  public void equipPlayer(PlayerContext playerCtx, TeamNew team) {
    Color color = team.getColor().getColor();

    ItemStack leggings = unreakableItem(this.leggings);
    ItemStack boots = unreakableItem(this.boots);
    ItemStack helmet = setColor(unreakableItem(Material.LEATHER_HELMET), color);
    ItemStack chestplate = setColor(unreakableItem(Material.LEATHER_CHESTPLATE), color);

    if (this.leggings == Material.LEATHER_LEGGINGS) {
      setColor(leggings, color);
      setColor(boots, color);
    }

    Player player = playerCtx.getPlayer();
    player.getInventory().setLeggings(leggings);
    player.getInventory().setBoots(boots);
    player.getInventory().setHelmet(helmet);
    player.getInventory().setChestplate(chestplate);

    player.updateInventory();

    UpgradeArmorProtection protection = team.getUpgrade(UpgradeArmorProtection.class);
    if (protection != null) {
      protection.getUpgrade().equipPlayer(playerCtx);
    }
  }

  private static ItemStack unreakableItem(Material material) {
    ItemStack item = MerchantCategory.fixMeta(new ItemStack(material, 1));
    return item;
  }

  private static ItemStack setColor(ItemStack item, Color color) {
    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    meta.setColor(color);
    item.setItemMeta(meta);
    return item;
  }

}
