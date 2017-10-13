package io.github.bedwarsrel.shop.actions;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.PlayerFlags;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrel.shop.ShopTrade;
import java.util.Map;
import org.bukkit.entity.Player;

public class ShopActionStackPerShift extends ShopAction {

  public ShopActionStackPerShift(Player p, Shop s, ShopTrade t) {
    super(p, s);
  }

  @Override
  public void execute(Map<String, Object> args) {
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(this.player);
    PlayerFlags flags = game.getPlayerFlags(this.player);
    flags.setOneStackPerShift(!flags.isOneStackPerShift());
    this.shop.playButtonSound();
    this.shop.render();
  }

}
