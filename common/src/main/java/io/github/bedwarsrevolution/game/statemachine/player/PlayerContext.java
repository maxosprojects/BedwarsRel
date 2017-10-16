package io.github.bedwarsrevolution.game.statemachine.player;

import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Shop;
import io.github.bedwarsrevolution.game.DamageHolder;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * Created by {maxos} 2017
 */
public class PlayerContext {
  @Getter
  private Player player;
  @Getter
  private PlayerState state = new PlayerStateWaitingGame();
  @Getter
  @Setter
  private boolean protectd;
  private Shop shop;
  @Getter
  private GameContext gameContext;
  @Getter
  private DamageHolder lastDamagedBy;
  @Getter
  @Setter
  private Team team;

  public PlayerContext(Player player, GameContext gameContext) {
    this.player = player;
    this.gameContext = gameContext;
  }

  public void setDamager(Player damager) {
    this.lastDamagedBy = new DamageHolder(damager);
  }

  public Shop getShop() {
    return shop;
  }

}
