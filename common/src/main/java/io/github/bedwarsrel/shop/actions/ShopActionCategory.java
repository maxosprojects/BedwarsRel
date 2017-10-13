package io.github.bedwarsrel.shop.actions;

import io.github.bedwarsrel.shop.MerchantCategory;
import io.github.bedwarsrel.shop.Shop;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;

public class ShopActionCategory extends ShopAction {
  @Getter
  private boolean active = false;
  private MerchantCategory category;

  public ShopActionCategory(Player p, Shop s) {
    super(p, s);
  }

  public void setActive(boolean act) {
    this.active = act;
  }

  public void setCategory(MerchantCategory category) {
    this.category = category;
  }

  @Override
  public void execute(Map<String, Object> args) {
    if (this.active) {
      this.shop.resetCurrentCategory();
    } else {
      this.shop.setCurrentCategory(this.category.getName());
    }
    this.shop.playButtonSound();
    this.shop.render();
  }

}
