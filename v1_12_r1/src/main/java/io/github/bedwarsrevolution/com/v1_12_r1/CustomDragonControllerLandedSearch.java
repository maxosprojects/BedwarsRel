package io.github.bedwarsrevolution.com.v1_12_r1;

import com.google.common.base.Predicate;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.v1_12_R1.DragonControllerLandedSearch;
import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.Vec3D;
import org.bukkit.entity.Player;

/**
 * Created by {maxos} 2017
 */
public class CustomDragonControllerLandedSearch extends DragonControllerLandedSearch {
  private final Set<Player> friendlyPlayers;
  private final CustomEnderDragon dragon;

  public CustomDragonControllerLandedSearch(CustomEnderDragon dragon, Set<Player> friendlyPlayers) {
    super(dragon);
    this.dragon = dragon;
    this.friendlyPlayers = friendlyPlayers;
  }

  @Override
  public void c() {

    EntityHuman human = this.dragon.world.a(this.dragon.locX, this.dragon.locY, this.dragon.locZ,
        -1.0D, -1.0D, null, new Predicate<EntityHuman>() {
          @Override
          public boolean apply(@Nullable EntityHuman entity) {
            return !CustomDragonControllerLandedSearch.this.friendlyPlayers.contains(entity.getBukkitEntity());
          }
          @Override
          public boolean test(@Nullable EntityHuman entity) {
            return this.apply(entity);
          }
        });

    this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.e);
    if (human != null) {
      this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.i);
      (this.dragon.getDragonControllerManager().b(DragonControllerPhase.i))
          .a(new Vec3D(human.locX, human.locY, human.locZ));
    }

  }

}
