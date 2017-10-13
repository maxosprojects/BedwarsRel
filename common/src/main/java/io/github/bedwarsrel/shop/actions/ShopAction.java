package io.github.bedwarsrel.shop.actions;

import io.github.bedwarsrel.shop.Shop;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class ShopAction {
  @Getter
  protected Player player;
  @Getter
  protected Shop shop;

  public ShopAction(Player p, Shop s) {
    this.player = p;
    this.shop = s;
  }

  public abstract void execute(Map<String, Object> args);

}
