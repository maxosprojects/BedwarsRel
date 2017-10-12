package io.github.bedwarsrel.shop.actions;

import io.github.bedwarsrel.shop.Shop;
import java.util.Map;
import org.bukkit.entity.Player;

public class ShopActionNoOp extends ShopAction {

  public ShopActionNoOp(Player p, Shop s) {
    super(p, s);
  }

  @Override
  public void execute(Map<String, Object> args) {
    // NoOp
  }
}
