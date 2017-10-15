package io.github.bedwarsrevolution.shop.actions;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.Shop;
import io.github.bedwarsrevolution.shop.ShopTrade;
import java.util.Map;

public class ShopActionStackPerShift extends ShopAction {

  public ShopActionStackPerShift(PlayerContext p, Shop s, ShopTrade t) {
    super(p, s);
  }

  @Override
  public void execute(Map<String, Object> args) {
    this.playerCtx.setOneStackPerShift(!this.playerCtx.isOneStackPerShift());
    this.shop.playButtonSound();
    this.shop.render();
  }

}
