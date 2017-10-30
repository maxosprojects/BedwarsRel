package io.github.bedwarsrevolution.com.v1_12_r1.dragon;

import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.DragonControllerStrafe;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;

/**
 * Disables Strafe behavior.
 * Cannot be simply replaced with another controller as there is a cast to DragonControllerStrafe
 * in DragonControllerHold after DragonControllerStrafe is set.
 *
 * Created by {maxos} 2017
 */
public class CustomDragonControllerStrafe extends DragonControllerStrafe {

  public CustomDragonControllerStrafe(EntityEnderDragon entityEnderDragon) {
    super(entityEnderDragon);
  }

  @Override
  public void c() {
    // Just switch to DragonControllerHold to prevent strafe
    this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.a);
  }

}
