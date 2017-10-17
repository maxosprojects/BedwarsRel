package io.github.bedwarsrevolution.shop.upgrades;

public enum UpgradeScope {
  PLAYER,
  TEAM;

  public boolean gte(UpgradeScope other) {
    return this.ordinal() >= other.ordinal();
  }

}
