package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Random;
import java.util.UUID;

public class CompanionMovement {
    public static final double WALK_SPEED = 0.53D;
    public static final double RUN_SPEED  = 0.65D;

    private final EntityCompanion companion;
    private final Random rand = new Random();

    public CompanionMovement(EntityCompanion companion) {
        this.companion = companion;
    }

    /** Движение «следуй за игроком» */
    public void handleFollow() {
        if (companion.getOwnerUUID() == null) return;
        Player owner = companion.level().getPlayerByUUID(companion.getOwnerUUID());
        if (owner == null) {
            companion.getNavigation().stop();
            return;
        }

        // Если в воде — плывём быстрее
        if (companion.isInWater()) {
            double swim = RUN_SPEED * 1.6;
            companion.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(swim);
            companion.getNavigation().moveTo(owner, swim);
            return;
        }

        double dist = companion.distanceTo(owner);

        if (dist <= 4.0D) {
            companion.getNavigation().stop();
            companion.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(WALK_SPEED);
        }
        else if (dist <= 10.0D) {
            companion.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(WALK_SPEED);
            companion.getNavigation().moveTo(owner, WALK_SPEED);
        }
        else {
            companion.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(RUN_SPEED);
            companion.getNavigation().moveTo(owner, RUN_SPEED);

            // Иногда — подпрыгиваем как игрок
            if (companion.onGround() && rand.nextInt(15) == 0) {
                companion.setDeltaMovement(
                        companion.getDeltaMovement().x,
                        0.42D,
                        companion.getDeltaMovement().z
                );
            }
        }

        // 💥 Если игрок ниже, а бот стоит — прыжок вниз
        if (companion.onGround()
                && (companion.getY() - owner.getY() > 1.5)
                && dist < 16.0D) {
            companion.setDeltaMovement(
                    companion.getDeltaMovement().x,
                    -0.42D, // вниз
                    companion.getDeltaMovement().z
            );
        }
    }

    /** Живой взгляд: поворачиваемся к игроку и иногда отворачиваемся */
    public void handleLook() {
        UUID ownerUUID = companion.getOwnerUUID();
        if (ownerUUID == null) return;

        Player owner = companion.level().getPlayerByUUID(ownerUUID);
        if (owner == null) return;

        double dist = companion.distanceTo(owner);
        if (dist < 16 && rand.nextInt(10) != 0) {
            companion.lookAt(owner, 30F, 30F);
        } else if (rand.nextInt(10) == 0) {
            float yaw = companion.getYRot() + (rand.nextBoolean() ? 1 : -1) * (10 + rand.nextInt(15));
            companion.setYRot(yaw);
            companion.yHeadRot = yaw;
            companion.yBodyRot = yaw;
        }
    }

    public void saveNBT(CompoundTag tag) {}
    public void loadNBT(CompoundTag tag) {}
}
