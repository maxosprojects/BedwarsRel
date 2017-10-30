package io.github.bedwarsrevolution.com.v1_12_r1;

import com.google.common.collect.ImmutableSet;
import io.github.bedwarsrevolution.utils.NmsUtils;
import java.util.Set;
import net.minecraft.server.v1_12_R1.AbstractDragonController;
import net.minecraft.server.v1_12_R1.DragonControllerLandedSearch;
import net.minecraft.server.v1_12_R1.DragonControllerLanding;
import net.minecraft.server.v1_12_R1.DragonControllerLandingFly;
import net.minecraft.server.v1_12_R1.DragonControllerManager;
import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.IDragonController;
import org.bukkit.entity.Player;

/**
 * Created by {maxos} 2017
 */
@SuppressWarnings("unchecked")
public class CustomDragonControllerManager extends DragonControllerManager {

  private static final ImmutableSet<Class<? extends AbstractDragonController>> overriddenControllers =
      ImmutableSet.of(
          DragonControllerLandingFly.class,
          DragonControllerLanding.class,
          DragonControllerLandedSearch.class);

  private final CustomEnderDragon dragon;
  private final Set<Player> friendlyPlayers;
  private CustomDragonControllerLandedSearch searchController = null;

  public CustomDragonControllerManager(CustomEnderDragon dragon, Set<Player> friendlyPlayers) {
    super(dragon);
    this.dragon = dragon;
    this.friendlyPlayers = friendlyPlayers;
  }

  @Override
  public void setControllerPhase(DragonControllerPhase<?> dragoncontrollerphase) {
    if (overriddenControllers.contains(dragoncontrollerphase)) {
      super.setControllerPhase(DragonControllerPhase.g);
    } else {
      super.setControllerPhase(dragoncontrollerphase);
    }
  }

  @Override
  public <T extends IDragonController> T b(DragonControllerPhase<T> controllerPhase) {
    Class<? extends IDragonController> controller = getPhase(controllerPhase);
    if (!overriddenControllers.contains(controller)) {
      return super.b(controllerPhase);
    }
    if (this.searchController == null) {
      this.searchController = new CustomDragonControllerLandedSearch(this.dragon, this.friendlyPlayers);
    }
    return (T) searchController;
  }

  private <T extends IDragonController> Class<? extends IDragonController> getPhase(
      DragonControllerPhase<T> controllerPhase) {
    return (Class<? extends IDragonController>)
        NmsUtils.getPrivateField("m", DragonControllerPhase.class, controllerPhase);
  }

}
