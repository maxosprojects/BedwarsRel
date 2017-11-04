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
  private final CustomEnderDragon2 dragon;
  private final Set<Player> friendlyPlayers;
  private CustomDragonControllerHold2 holdController;

  static {
    DragonControllerPhase<?>[] phases = (DragonControllerPhase<?>[]) NmsUtils.getPrivateField("l", DragonControllerPhase.class, null);
    NmsUtils.setPrivateField("m", DragonControllerPhase.class, phases[0], CustomDragonControllerHold2.class);
  }

  public CustomDragonControllerManager2(CustomEnderDragon2 dragon, Set<Player> friendlyPlayers) {
    super(dragon);
    this.dragon = dragon;
    this.friendlyPlayers = friendlyPlayers;
  }

//  @Override
//  public void setControllerPhase(DragonControllerPhase<?> phase) {
//    super.setControllerPhase(DragonControllerPhase.a);
//  }
//
//  @Override
//  public <T extends IDragonController> T b(DragonControllerPhase<T> phase) {
//    if (this.dragon == null) {
//      return super.b(phase);
//    }
//    if (this.holdController == null) {
//      this.holdController = new CustomDragonControllerHold2(this.dragon);
//    }
//    return (T) holdController;
//  }

}
