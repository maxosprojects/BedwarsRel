package io.github.bedwarsrevolution.game.statemachine.player;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by {maxos} 2017
 */

@RunWith(MockitoJUnitRunner.class)
public class PlayerStatePlayingTest {
  PlayerStatePlaying sut;

  @Mock
  private PlayerContext playerCtx;

  @Before
  public void setup() {
    sut = new PlayerStatePlaying(playerCtx);
  }

  @Test
  public void itemsPackedWhenOneStack() {
    List<ItemStack> stacks = new ArrayList<>();
    stacks.add(new ItemStack(Material.ARROW, 50));

    
  }

}
