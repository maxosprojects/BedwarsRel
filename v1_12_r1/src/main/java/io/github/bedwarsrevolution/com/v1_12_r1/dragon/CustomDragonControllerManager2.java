package io.github.bedwarsrevolution.com.v1_12_r1.dragon;

import com.google.common.collect.ImmutableSet;
import io.github.bedwarsrevolution.com.v1_12_r1.CustomEnderDragon2;
import io.github.bedwarsrevolution.utils.NmsUtils;
import java.util.Set;
import net.minecraft.server.v1_12_R1.AbstractDragonController;
import net.minecraft.server.v1_12_R1.DragonControllerLandedSearch;
import net.minecraft.server.v1_12_R1.DragonControllerLanding;
import net.minecraft.server.v1_12_R1.DragonControllerLandingFly;
import net.minecraft.server.v1_12_R1.DragonControllerManager;
import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.DragonControllerStrafe;
import net.minecraft.server.v1_12_R1.IDragonController;
import org.bukkit.entity.Player;

/**
 * Created by {maxos} 2017
 */
@SuppressWarnings("unchecked")
public class CustomDragonControllerManager2 extends DragonControllerManager {

  private static final ImmutableSet<Class<? extends AbstractDragonController>> replacedControllers =
      ImmutableSet.of(
          DragonControllerLandingFly.class,
          DragonControllerLanding.class,
          DragonControllerLandedSearch.class);

  private final CustomEnderDragon2 dragon;
  private final Set<Player> friendlyPlayers;
  private CustomDragonControllerLandedSearch2 searchController = null;
  private CustomDragonControllerStrafe strafeController;

  public CustomDragonControllerManager2(CustomEnderDragon2 dragon, Set<Player> friendlyPlayers) {
    super(dragon);
    this.dragon = dragon;
    this.friendlyPlayers = friendlyPlayers;
  }

  @Override
  public void setControllerPhase(DragonControllerPhase<?> phase) {
    Class<? extends IDragonController> controller = getController(phase);
    if (replacedControllers.contains(controller)) {
      super.setControllerPhase(DragonControllerPhase.g);
    } else {
      super.setControllerPhase(phase);
    }
  }

  @Override
  public <T extends IDragonController> T b(DragonControllerPhase<T> phase) {
    Class<? extends IDragonController> controller = getController(phase);
    if (!replacedControllers.contains(controller) && controller != DragonControllerStrafe.class) {
      return super.b(phase);
    }
    /** ControllerStrafe is a special case. See {@link CustomDragonControllerStrafe} **/
    if (controller == DragonControllerStrafe.class) {
      if (this.strafeController == null) {
        this.strafeController = new CustomDragonControllerStrafe(this.dragon);
      }
      return (T) this.strafeController;
    }
    if (this.searchController == null) {
      this.searchController = new CustomDragonControllerLandedSearch2(this.dragon, this.friendlyPlayers);
    }
    return (T) searchController;
  }

  private <T extends IDragonController> Class<? extends IDragonController> getController(
      DragonControllerPhase<T> controllerPhase) {
    return (Class<? extends IDragonController>)
        NmsUtils.getPrivateField("m", DragonControllerPhase.class, controllerPhase);
  }

}
