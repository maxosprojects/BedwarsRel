package io.github.bedwarsrel.shop.Specials;

public interface Upgrade {
  UpgradeType getScope();

  boolean matches(String type, int level);
}
