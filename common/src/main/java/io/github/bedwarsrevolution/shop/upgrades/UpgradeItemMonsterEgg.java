package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.NmsUtils;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by {maxos} 2017
 */
public class UpgradeItemMonsterEgg extends UpgradeItem {
  protected final String TYPE = "MONSTER_EGG_ITEM";

  @Override
  public boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    if (item.getType() == Material.MONSTER_EGG) {
      if (event.getAction() != Action.RIGHT_CLICK_BLOCK
          || event.getBlockFace() != BlockFace.UP) {
        return true;
      }
      this.spawnGolem(playerCtx, event.getClickedBlock());
      return true;
    }
    return false;
  }

  private void spawnGolem(PlayerContext playerCtx, Block block) {
    Player player = playerCtx.getPlayer();
    TeamNew team = playerCtx.getTeam();
    if (team.isGolemLimitReached()) {
      player.sendMessage(ChatWriterNew.pluginMessage(
          "&cOnly 5 living Iron Golems allowed per team"));
      return;
    }
    // Take one fireball from the player
    Inventory inv = player.getInventory();
    int slot = inv.first(Material.MONSTER_EGG);
    ItemStack stack = inv.getItem(slot);
    stack.setAmount(stack.getAmount() - 1);
    // Spawn golem
    Location loc = block.getLocation().clone().add(0, 1, 0);
    Set<Player> friendlyPlayers = new HashSet<>();
    for (PlayerContext otherPlayerCtx : team.getPlayers()) {
      friendlyPlayers.add(otherPlayerCtx.getPlayer());
    }
    IronGolem golem = NmsUtils.spawnCustomIronGolem(loc, friendlyPlayers);
    team.addGolem(golem);
  }
}
