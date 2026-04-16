package net.Leease.leasemod.entity;

import net.Leease.leasemod.effect.ModEffects;
import net.Leease.leasemod.particle.ModParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SlashEntity extends Entity {

    private static final EntityDataAccessor<Float> DIR_X = SynchedEntityData.defineId(SlashEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_Y = SynchedEntityData.defineId(SlashEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_Z = SynchedEntityData.defineId(SlashEntity.class, EntityDataSerializers.FLOAT);

    private int ticksAlive = 0;
    private static final int MAX_DISTANCE = 30;
    private java.util.UUID ownerUUID;

    public SlashEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public void setDirection(Vec3 direction) {
        this.entityData.set(DIR_X, (float) direction.x);
        this.entityData.set(DIR_Y, (float) direction.y);
        this.entityData.set(DIR_Z, (float) direction.z);
    }

    public Vec3 getSlashDirection() {
        return new Vec3(
                this.entityData.get(DIR_X),
                this.entityData.get(DIR_Y),
                this.entityData.get(DIR_Z)
        );
    }

    public void setOwner(net.minecraft.world.entity.player.Player player) {
        this.ownerUUID = player.getUUID();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DIR_X, 0f);
        builder.define(DIR_Y, 0f);
        builder.define(DIR_Z, 0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void tick() {
        super.tick();
        ticksAlive++;

        Vec3 direction = getSlashDirection();

        // avance d'1 bloc par seconde
        if (ticksAlive % 2 == 0) {
            this.setPos(this.getX() + direction.x * 1.4, this.getY() + direction.y * 1.4, this.getZ() + direction.z * 1.4);
        }
        if (!level().isClientSide && ticksAlive % 2 == 0) {
            AABB hitbox = new AABB(
                    this.getX() - 3.5, this.getY() - 0.5, this.getZ() - 3.5,
                    this.getX() + 3.5, this.getY() + 0.5, this.getZ() + 3.5
            );

            List<LivingEntity> targets = level().getEntitiesOfClass(
                    LivingEntity.class, hitbox,
                    entity -> !(entity instanceof net.minecraft.world.entity.player.Player p && p.getUUID().equals(ownerUUID)));

            for (LivingEntity target : targets) {

                //saignement logique duh
                target.addEffect(new MobEffectInstance(
                        ModEffects.BLEEDING, 140, 0, false, false, false
                ));

                //degats du slash duh générique pas magique donc
                target.hurt(this.damageSources().generic(), 4.0F);
            }

        }

        // arrete d'oublier les particules côté client tdbl
        if (level().isClientSide) {
            Vec3 perp = new Vec3(-direction.z, 0, direction.x).normalize();
            for (double i = -3.5; i <= 3.5; i += 0.5) {
                level().addParticle(
                        ModParticles.SLASH.get(),
                        this.getX() + perp.x * i,
                        this.getY(),
                        this.getZ() + perp.z * i,
                        0, 0, 0
                );
            }
        }

        // Disparait après 30 blocs
        if (ticksAlive >= MAX_DISTANCE * 20) {
            this.discard();
        }

    }
}