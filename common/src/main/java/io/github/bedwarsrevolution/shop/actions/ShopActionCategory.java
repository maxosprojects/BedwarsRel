package io.github.bedwarsrevolution.shop.actions;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.MerchantCategory;
import io.github.bedwarsrevolution.shop.Shop;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;

public class ShopActionCategory extends ShopAction {
  @Getter
  private boolean active = false;
  private MerchantCategory category;

  public ShopActionCategory(PlayerContext playerCtx, Shop shop) {
    super(playerCtx, shop);
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
