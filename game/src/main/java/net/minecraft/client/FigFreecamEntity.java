package net.minecraft.client;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class FigFreecamEntity extends Entity {
   public FigFreecamEntity(final Level level, final Player player) {
      super(EntityTypes.ARMOR_STAND, level);
      this.setPos(player.position());
      this.setRot(player.getYRot(), player.getXRot());
   }

   public void moveFromKeys(final Options options) {
      double forward = (options.keyUp.isDown() ? 1.0 : 0.0) - (options.keyDown.isDown() ? 1.0 : 0.0);
      double strafe = (options.keyRight.isDown() ? 1.0 : 0.0) - (options.keyLeft.isDown() ? 1.0 : 0.0);
      double vertical = (options.keyJump.isDown() ? 1.0 : 0.0) - (options.keyShift.isDown() ? 1.0 : 0.0);
      double inputLength = Math.sqrt(forward * forward + strafe * strafe + vertical * vertical);
      if (inputLength > 1.0) {
         forward /= inputLength;
         strafe /= inputLength;
         vertical /= inputLength;
      }
      double speed = 0.12;
      double yaw = Math.toRadians(this.getYRot());
      double x = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * speed;
      double z = (Math.cos(yaw) * forward + Math.sin(yaw) * strafe) * speed;
      this.setPos(this.getX() + x, this.getY() + vertical * speed, this.getZ() + z);
   }

   @Override
   protected void defineSynchedData(final net.minecraft.network.syncher.SynchedEntityData.Builder entityData) {
   }

   @Override
   protected void readAdditionalSaveData(final ValueInput input) {
   }

   @Override
   protected void addAdditionalSaveData(final ValueOutput output) {
   }

   @Override
   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float amount) {
      return false;
   }
}