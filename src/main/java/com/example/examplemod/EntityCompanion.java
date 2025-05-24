package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


import java.util.UUID;

public class EntityCompanion extends Mob {
    private UUID ownerUUID;
    public final CompanionInventory inventoryLogic;
    public final CompanionMovement movementLogic;
    public final CompanionNaming namingLogic;

    public EntityCompanion(EntityType<? extends Mob> type, Level world) {
        super(type, world);
        this.inventoryLogic = new CompanionInventory(this);
        this.movementLogic  = new CompanionMovement(this);
        this.namingLogic    = new CompanionNaming(this);
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void tick() {
        super.tick();

        // Всегда смотрим на игрока (живое поведение)
        movementLogic.handleLook();

        // Если есть владелец в мире — подбираем предметы и следуем за ним
        if (ownerUUID != null) {
            Player owner = level().getPlayerByUUID(ownerUUID);
            if (owner != null) {
                inventoryLogic.handleItemPickup();
                movementLogic.handleFollow();
            } else {
                // если игрок ещё не прогрузился — просто стоим
                getNavigation().stop();
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        inventoryLogic.dropAllItemsOnDeath();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        inventoryLogic.saveNBT(tag);
        namingLogic.saveNBT(tag);
        movementLogic.saveNBT(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventoryLogic.loadNBT(tag);
        namingLogic.loadNBT(tag);
        movementLogic.loadNBT(tag);
    }

    public static BlockPos findSafeSpawn(Level world, double x, double y, double z) {
        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
        while (!world.getBlockState(pos).isAir() && pos.getY() < world.getMaxBuildHeight()) {
            pos = pos.above();
        }
        while (world.getBlockState(pos.below()).isAir() && pos.getY() > world.getMinBuildHeight()) {
            pos = pos.below();
        }
        return pos;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, CompanionMovement.WALK_SPEED);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new FloatGoal(this));
    }
}
