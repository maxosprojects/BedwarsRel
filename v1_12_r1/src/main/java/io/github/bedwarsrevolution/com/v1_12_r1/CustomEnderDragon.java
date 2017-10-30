package io.github.bedwarsrevolution.com.v1_12_r1;

import io.github.bedwarsrevolution.com.v1_12_r1.dragon.CustomDragonControllerManager;
import io.github.bedwarsrevolution.utils.NmsUtils;
import java.util.Set;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * Created by {maxos} 2017
 */
public class CustomEnderDragon extends EntityEnderDragon {

  public CustomEnderDragon(World world, final Set<Player> friendlyPlayers) {
    super(world);
    CustomDragonControllerManager controllerManager = new CustomDragonControllerManager(this, friendlyPlayers);
    NmsUtils.setPrivateField("bL", EntityEnderDragon.class, this, controllerManager);
  }

  public static EnderDragon spawn(Location location, Set<Player> friendlyPlayers) {
    // Make sure custom dragon is registered
    CustomEntityRegistry.getInstance();
    World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
    CustomEnderDragon customEnderDragon = new CustomEnderDragon(nmsWorld, friendlyPlayers);
    customEnderDragon.setPosition(location.getX(), location.getY(), location.getZ());
    if (!nmsWorld.addEntity(customEnderDragon, SpawnReason.CUSTOM)) {
      throw new RuntimeException("EnderDragon is DOA");
    }
    EnderDragon dragon = (EnderDragon) customEnderDragon.getBukkitEntity();
    dragon.setPhase(Phase.CIRCLING);
    return dragon;
  }

}
