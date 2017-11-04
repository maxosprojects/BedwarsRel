package io.github.bedwarsrevolution.com.v1_12_r1;

import io.github.bedwarsrevolution.com.v1_12_r1.dragon.CustomDragonControllerManager2;
import io.github.bedwarsrevolution.utils.NmsUtils;
import java.util.Set;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.IDragonController;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.Vec3D;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldGenEndTrophy;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * Created by {maxos} 2017
 */
public class CustomEnderDragon2 extends EntityEnderDragon {
  private final BlockPosition top;

  public CustomEnderDragon2(World world, final Set<Player> friendlyPlayers, Location top) {
    super(world);
    CustomDragonControllerManager2 controllerManager = new CustomDragonControllerManager2(this, friendlyPlayers);
    NmsUtils.setPrivateField("bL", EntityEnderDragon.class, this, controllerManager);
    this.top = new BlockPosition(top.getX(), top.getY(), top.getZ());
  }

  @Override
  public Vec3D a(float f) {
    IDragonController idragoncontroller = this.getControllerManager().a();
    DragonControllerPhase dragoncontrollerphase = idragoncontroller.getControllerPhase();
    float f1;
    Vec3D vec3d;
    if(dragoncontrollerphase != DragonControllerPhase.d && dragoncontrollerphase != DragonControllerPhase.e) {
      if(idragoncontroller.a()) {
        float f2 = this.pitch;
        f1 = 1.5F;
        this.pitch = -45.0F;
        vec3d = this.e(f);
        this.pitch = f2;
      } else {
        vec3d = this.e(f);
      }
    } else {
      BlockPosition blockposition = this.world.q(top);
      f1 = Math.max(MathHelper.sqrt(this.d(blockposition)) / 4.0F, 1.0F);
      float f3 = 6.0F / f1;
      float f4 = this.pitch;
      this.pitch = -f3 * 1.5F * 5.0F;
      vec3d = this.e(f);
      this.pitch = f4;
    }

    return vec3d;
  }

  private CustomDragonControllerManager2 getControllerManager() {
    return (CustomDragonControllerManager2) NmsUtils.getPrivateField("bL", EntityEnderDragon.class, this);
  }

  public static EnderDragon spawn(Location location, Set<Player> friendlyPlayers) {
    // Make sure custom dragon is registered
    CustomEntityRegistry.getInstance();
    World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
    CustomEnderDragon2 customEnderDragon = new CustomEnderDragon2(nmsWorld, friendlyPlayers, location);
    customEnderDragon.setPosition(location.getX(), location.getY(), location.getZ());
    if (!nmsWorld.addEntity(customEnderDragon, SpawnReason.CUSTOM)) {
      throw new RuntimeException("EnderDragon is DOA");
    }
    EnderDragon dragon = (EnderDragon) customEnderDragon.getBukkitEntity();
    dragon.setPhase(Phase.CIRCLING);
    return dragon;
  }

}
