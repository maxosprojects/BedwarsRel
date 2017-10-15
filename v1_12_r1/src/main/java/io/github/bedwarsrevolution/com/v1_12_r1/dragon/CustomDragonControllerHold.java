package io.github.bedwarsrevolution.com.v1_12_r1.dragon;

import com.google.common.base.Predicate;
import io.github.bedwarsrevolution.com.v1_12_r1.CustomEnderDragon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.DragonControllerHold;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CustomDragonControllerHold extends DragonControllerHold {
  private static final double RADIUS = 40;

  private final Set<Player> friendlyPlayers;
  private final Vec3D top;
  private Vec3D currPoint;

  public CustomDragonControllerHold(CustomEnderDragon dragon, Set<Player> friendlyPlayers, Location top) {
    super(dragon);
    this.friendlyPlayers = friendlyPlayers;
    this.top = new Vec3D(top.getX(), top.getY(), top.getZ());
    this.currPoint = this.randomPoint(this.a.getRandom());
  }

  @Override
  public void c() {
    double distanceSquared = this.currPoint.c(this.a.locX, this.a.locY, this.a.locZ);
    if(distanceSquared < 20.0D) {
      this.nextPoint();
    }
  }

  private void nextPoint() {
    Random rnd = this.a.getRandom();
    if (rnd.nextBoolean()) {
      this.currPoint = this.randomPoint(rnd);
      return;
    }
    Vec3D humanPos = this.getHumanPosition();
    if (humanPos == null) {
      this.currPoint = this.randomPoint(rnd);
    } else {
      this.currPoint = humanPos;
    }
  }

  private Vec3D randomPoint(Random rnd) {
    double angle = Math.PI * 2.0D * rnd.nextDouble();
    return this.top.add(Math.sin(angle) * RADIUS, 0, Math.cos(angle) * RADIUS);
  }

  private Vec3D getHumanPosition() {
    EntityHuman human = this.a.world.a(this.a.locX, this.a.locY, this.a.locZ,
        -1.0D, -1.0D, null, new Predicate<EntityHuman>() {
          @Override
          public boolean apply(@Nullable EntityHuman entity) {
            if (entity == null) {
              return false;
            }
            return !CustomDragonControllerHold.this.friendlyPlayers.contains(entity.getBukkitEntity());
          }
          @Override
          public boolean test(@Nullable EntityHuman entity) {
            return this.apply(entity);
          }
        });
    if (human == null) {
      return null;
    }
    return new Vec3D(human.locX, human.locY, human.locZ);
  }

  @Nullable
  @Override
  public Vec3D g() {
    return this.currPoint;
  }

  @Override
  public void a(EntityEnderCrystal entityEnderCrystal, BlockPosition blockPosition,
      DamageSource damageSource, @Nullable EntityHuman entityHuman) {
  }

}
