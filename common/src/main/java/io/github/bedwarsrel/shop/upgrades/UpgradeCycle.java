package io.github.bedwarsrel.shop.upgrades;

public enum UpgradeCycle {
  ONCE,
  RESPAWN;

  public boolean gte(UpgradeCycle other) {
    return this.ordinal() >= other.ordinal();
  }

}
