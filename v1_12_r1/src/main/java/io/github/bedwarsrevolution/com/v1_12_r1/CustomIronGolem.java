package io.github.bedwarsrevolution.com.v1_12_r1;

import com.google.common.base.Predicate;
import io.github.bedwarsrevolution.utils.NmsUtils;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityIronGolem;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MobEffect;
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_12_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * Created by {maxos} 2017
 */
public class CustomIronGolem extends EntityIronGolem {

  @SuppressWarnings("unchecked")
  public CustomIronGolem(World world, final Set<Player> friendlyPlayers) {
    super(world);

    Set goalB = (Set) NmsUtils.getPrivateField("b", PathfinderGoalSelector.class, this.goalSelector);
    goalB.clear();
    Set goalC = (Set) NmsUtils.getPrivateField("c", PathfinderGoalSelector.class, this.goalSelector);
    goalC.clear();
    Set targetB = (Set) NmsUtils.getPrivateField("b", PathfinderGoalSelector.class, this.targetSelector);
    targetB.clear();
    Set targetC = (Set) NmsUtils.getPrivateField("c", PathfinderGoalSelector.class, this.targetSelector);
    targetC.clear();

    this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
    this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
    this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityPlayer.class, 10, false, true, new Predicate() {
      public boolean apply(@Nullable Object object) {
        EntityPlayer player = (EntityPlayer) object;
        return player != null && !this.isFriendly(player) && !this.isInvisible(player);
      }
      @Override
      public boolean test(@Nullable Object input) {
        return this.apply(input);
      }
      private boolean isFriendly(EntityPlayer player) {
        return friendlyPlayers.contains(player.getBukkitEntity());
      }
      private boolean isInvisible(EntityPlayer player) {
        for (MobEffect effect : player.getEffects()) {
          if ("effect.invisibility".equals(effect.f())) {
            return true;
          }
        }
        return false;
      }
    }));
  }

  public static IronGolem spawn(Location location, Set<Player> friendlyPlayers) {
    // Make sure custom golem is registered
    CustomEntityRegistry.getInstance();
    World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
    CustomIronGolem golem = new CustomIronGolem(nmsWorld, friendlyPlayers);
    golem.setPosition(location.getX(), location.getY(), location.getZ());
    golem.setPlayerCreated(false);
    if (!nmsWorld.addEntity(golem, SpawnReason.CUSTOM)) {
      throw new RuntimeException("Iron Golem is DOA");
    }
    return (IronGolem) golem.getBukkitEntity();
  }

}
