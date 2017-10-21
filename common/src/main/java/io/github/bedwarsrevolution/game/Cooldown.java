package io.github.bedwarsrevolution.game;

import lombok.Data;
import lombok.Getter;

/**
 * Created by {maxos} 2017
 */
@Data
public class Cooldown {
  public enum Scope {
    PLAYER,
    TEAM
  }

  private final Scope scope;
  private final int wait;
}
