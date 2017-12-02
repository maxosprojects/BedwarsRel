package io.github.bedwarsrevolution.maps;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.RegionNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.bukkit.block.Block;

/**
 * Created by {maxos} 2017
 */
public class MapsManager {
  private static final String MAPS_FOLDER = "maps";

  private final boolean renderOnUpdates;

  public MapsManager(boolean renderOnUpdates) {
    this.renderOnUpdates = renderOnUpdates;
  }

  public boolean store(String game) {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameContext(game);
    RegionNew region = ctx.getRegion();
    String world = region.getWorld().getName();
    BufferedImage img = BedwarsRevol.getInstance().getDynmap().getRegion(
        world,
        region.getMinCorner().getBlockX(),
        region.getMinCorner().getBlockZ(),
        region.getMaxCorner().getBlockX(),
        region.getMaxCorner().getBlockZ());
    try {
      ensureMapsFolderExists();
      ImageIO.write(img, "PNG", gameMapFile(game));
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  private static void ensureMapsFolderExists() {
    File mapsFolder = new File(UtilsNew.join(File.separator, BedwarsRevol.getInstance().getDataFolder(), MAPS_FOLDER));
    if (!mapsFolder.exists()) {
      mapsFolder.mkdir();
    } else if (!mapsFolder.isDirectory()) {
      mapsFolder.delete();
      mapsFolder.mkdir();
    }
  }

  private static File gameMapFile(String game) {
    return new File(UtilsNew.join(File.separator,
        BedwarsRevol.getInstance().getDataFolder(), MAPS_FOLDER, game + ".png"));
  }

  public void update(Block block) {
    if (!this.renderOnUpdates) {
      return;
    }

    BedwarsRevol.getInstance().getDynmap().triggerRenderOfBlock(
        block.getWorld().getName(),
        block.getX(),
        block.getY(),
        block.getZ());
  }
}
