package io.github.bedwarsrevolution.game.statemachine.player;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by {maxos} 2017
 */

@RunWith(MockitoJUnitRunner.class)
public class PlayerStatePlayingTest {
  private PlayerStatePlaying sut;

  @Mock
  private PlayerContext playerCtx;

  @Before
  public void setup() {
    sut = new PlayerStatePlaying(playerCtx);
  }

  private ItemStack makeStack(int amount) {
    return new ItemStack(Material.ARROW, amount);
  }

  private List<ItemStack> makeList(ItemStack ...input) {
    return Arrays.asList(input);
  }

  @Test
  public void singleStack() {
    List<ItemStack> list = makeList(makeStack(50));
    ItemStack[] res = sut.packItems(list);
    assertEquals(1, res.length);
    assertEquals(50, res[0].getAmount());
  }

  @Test
  public void twoStacksToOne() {
    List<ItemStack> list = makeList(
        makeStack(50),
        makeStack(10));
    ItemStack[] res = sut.packItems(list);
    assertEquals(1, res.length);
    assertEquals(60, res[0].getAmount());
  }

  @Test
  public void threeStacksToOne() {
    List<ItemStack> list = makeList(
        makeStack(40),
        makeStack(10),
        makeStack(10));
    ItemStack[] res = sut.packItems(list);
    assertEquals(1, res.length);
    assertEquals(60, res[0].getAmount());
  }

  @Test
  public void twoStacksToTwo() {
    List<ItemStack> list = makeList(
        makeStack(50),
        makeStack(20));
    ItemStack[] res = sut.packItems(list);
    assertEquals(2, res.length);
    assertEquals(64, res[0].getAmount());
    assertEquals(6, res[1].getAmount());
  }

  @Test
  public void twoStacksToTwoTightFit() {
    List<ItemStack> list = makeList(
        makeStack(64),
        makeStack(64));
    ItemStack[] res = sut.packItems(list);
    assertEquals(2, res.length);
    assertEquals(64, res[0].getAmount());
    assertEquals(64, res[1].getAmount());
  }

}
