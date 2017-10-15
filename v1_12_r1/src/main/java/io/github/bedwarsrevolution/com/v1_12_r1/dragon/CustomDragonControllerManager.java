package io.github.bedwarsrevolution.com.v1_12_r1.dragon;

import io.github.bedwarsrevolution.com.v1_12_r1.CustomEnderDragon;
import java.util.Set;
import net.minecraft.server.v1_12_R1.DragonControllerManager;
import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.IDragonController;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by {maxos} 2017
 */
@SuppressWarnings("unchecked")
public class CustomDragonControllerManager extends DragonControllerManager {
  private final CustomEnderDragon dragon;
  private final Set<Player> friendlyPlayers;
  private final Location top;
  private CustomDragonControllerHold holdController;

  public CustomDragonControllerManager(CustomEnderDragon dragon, Set<Player> friendlyPlayers, Location top) {
    super(dragon);
    this.dragon = dragon;
    this.friendlyPlayers = friendlyPlayers;
    this.top = top;
  }

  @Override
  public void setControllerPhase(DragonControllerPhase<?> phase) {
    if (this.top != null) {
      super.setControllerPhase(DragonControllerPhase.a);
    }
  }

  @Override
  public <T extends IDragonController> T b(DragonControllerPhase<T> phase) {
    if (this.holdController == null) {
      this.holdController = new CustomDragonControllerHold(this.dragon, this.friendlyPlayers, this.top);
    }
    return (T) holdController;
  }

}
