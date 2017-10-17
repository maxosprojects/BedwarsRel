package io.github.bedwarsrevolution.shop.actions;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.Shop;
import java.util.Map;
import lombok.Getter;

public abstract class ShopAction {
  @Getter
  protected PlayerContext playerCtx;
  @Getter
  protected Shop shop;

  public ShopAction(PlayerContext playerCtx, Shop shop) {
    this.playerCtx = playerCtx;
    this.shop = shop;
  }

  public abstract void execute(Map<String, Object> args);

}
