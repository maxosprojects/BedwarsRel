package io.github.bedwarsrel.shop.upgrades;

public enum UpgradeScope {
  PLAYER,
  TEAM;

  public boolean gte(UpgradeScope other) {
    return this.ordinal() >= other.ordinal();
  }

}
