package io.github.bedwarsrevolution.shop.actions;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.Shop;
import java.util.Map;

public class ShopActionNoOp extends ShopAction {

  public ShopActionNoOp(PlayerContext p, Shop s) {
    super(p, s);
  }

  @Override
  public void execute(Map<String, Object> args) {
    // NoOp
  }
}
