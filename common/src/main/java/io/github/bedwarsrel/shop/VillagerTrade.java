package io.github.bedwarsrel.shop;

import org.bukkit.inventory.ItemStack;

public class VillagerTrade {

  private ItemStack item1;
  private ItemStack item2;
  private Reward reward;

  public VillagerTrade(ItemStack item1, ItemStack item2, Reward reward) {
    this.item1 = item1;
    this.item2 = item2;
    this.reward = reward;
  }

  public VillagerTrade(ItemStack item1, Reward reward) {
    this(item1, null, reward);
  }

//  public VillagerTrade(MerchantRecipe handle) {
//    this.item1 = new CraftItemStack(handle.getItem1()).asBukkitCopy();
//    this.item2 =
//        (handle.getItem1() == null ? null : new CraftItemStack(handle.getItem2()).asBukkitCopy());
//    this.reward = new ItemStackHolder(
//        new CraftItemStack(handle.getReward()).asBukkitCopy(), null);
//  }

  public MerchantRecipe getHandle() {
    if (this.item2 == null) {
      return new MerchantRecipe(new CraftItemStack(this.item1).asNMSCopy(),
          new CraftItemStack(this.reward.getItem()).asNMSCopy());
    }
    return new MerchantRecipe(new CraftItemStack(this.item1).asNMSCopy(),
        new CraftItemStack(this.item2).asNMSCopy(),
        new CraftItemStack(this.reward.getItem()).asNMSCopy());
  }

  public ItemStack getItem1() {
    return this.item1;
  }

  public ItemStack getItem2() {
    return this.item2;
  }

  public Reward getReward() {
    return this.reward;
  }

  public boolean hasItem2() {
    return this.item2 != null;
  }

}
