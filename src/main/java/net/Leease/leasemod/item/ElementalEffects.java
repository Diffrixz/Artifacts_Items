package net.Leease.leasemod.item;

import net.Leease.leasemod.client.BookCooldownHud;
import net.Leease.leasemod.client.ElementWheel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ElementalEffects {

    private static final Map<UUID, Long> cooldownUntil = new HashMap<>();
    private static final Map<UUID, Long> cooldownStart = new HashMap<>();
    private static final Map<UUID, Long> cooldownTotal = new HashMap<>();
    private static final Map<UUID, Long> waterShieldUntil = new HashMap<>();

    public static boolean hasWaterShield(UUID uuid, long currentTick) {
        return currentTick < waterShieldUntil.getOrDefault(uuid, 0L);
    }

    public static boolean isOnCooldown(ServerPlayer player) {
        long until = cooldownUntil.getOrDefault(player.getUUID(), 0L);
        return player.level().getGameTime() < until;
    }

    public static long getCooldownRemaining(ServerPlayer player) {
        long until = cooldownUntil.getOrDefault(player.getUUID(), 0L);
        return Math.max(0, until - player.level().getGameTime());
    }

    public static long getCooldownTotal(UUID uuid) {
        return cooldownTotal.getOrDefault(uuid, 1L);
    }


    private static void setCooldown(ServerPlayer player, long durationTicks) {
        UUID uuid = player.getUUID();
        long now = player.level().getGameTime();
        cooldownUntil.put(uuid, now + durationTicks);
        cooldownStart.put(uuid, now);
        cooldownTotal.put(uuid, durationTicks);
    }

    // Feu en continu — sans cooldown, appelé chaque tick
    public static void applyFireTick(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 look = player.getLookAngle();
        Vec3 start = player.getEyePosition();

        for (double d = 0.5; d <= 2.0; d += 0.25) {
            Vec3 pos = start.add(look.scale(d));
            level.sendParticles(ParticleTypes.FLAME,
                    pos.x, pos.y, pos.z,
                    3, 0.2, 0.2, 0.2, 0.05);
        }

        AABB hitbox = new AABB(
                start.x - 1, start.y - 1, start.z - 1,
                start.x + 1, start.y + 1, start.z + 1
        ).move(look.scale(1.0));

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class, hitbox,
                e -> !e.getUUID().equals(player.getUUID()));

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().magic(), 3.0F);
        }
        // Pas de setCooldown ici
    }

    // Pose le cooldown du feu quand le joueur relâche
    public static void applyFireCooldown(ServerPlayer player) {
        BookCooldownHud.setLastUsed(ElementWheel.Element.FIRE);
        setCooldown(player, 24000L);
        player.getCooldowns().addCooldown(ModItems.ELEMENTAL_BOOK.get(), 24000);
    }

    public static void applyWater(ServerPlayer player) {
        if (isOnCooldown(player)) return;

        // Armure anti-projectile 7s = 140 ticks
        waterShieldUntil.put(player.getUUID(),
                player.level().getGameTime() + 140L);

        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED, 1200, 0, false, true, true));

        player.addEffect(new MobEffectInstance(
                MobEffects.WATER_BREATHING, 1200, 0, false, true, true));

        BookCooldownHud.setLastUsed(ElementWheel.Element.WATER);
        setCooldown(player, 6000L);
        player.getCooldowns().addCooldown(ModItems.ELEMENTAL_BOOK.get(), 6000);
    }

    public static void applyEarth(ServerPlayer player) {
        if (isOnCooldown(player)) return;
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, 140, 3, false, true, true));
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN, 140, 3, false, true, true));
        BookCooldownHud.setLastUsed(ElementWheel.Element.EARTH);
        setCooldown(player, 48000L);
        player.getCooldowns().addCooldown(ModItems.ELEMENTAL_BOOK.get(), 48000);
    }

    public static void applyAir(ServerPlayer player) {
        if (isOnCooldown(player)) return;

        ServerLevel level = player.serverLevel();
        Vec3 look = player.getLookAngle();

        for (int i = 0; i < 20; i++) {
            double spreadX = look.x + (Math.random() - 0.5) * 0.3;
            double spreadY = look.y + (Math.random() - 0.5) * 0.3;
            double spreadZ = look.z + (Math.random() - 0.5) * 0.3;

            // On utilise WindCharge via son EntityType comme Minecraft le fait en interne
            net.minecraft.world.entity.projectile.windcharge.WindCharge windCharge =
                    new net.minecraft.world.entity.projectile.windcharge.WindCharge(
                            net.minecraft.world.entity.EntityType.WIND_CHARGE,
                            level
                    );
            windCharge.setOwner(player);
            windCharge.setPos(player.getX(), player.getEyeY(), player.getZ());
            windCharge.setDeltaMovement(spreadX * 3.0, spreadY * 3.0, spreadZ * 3.0);
            level.addFreshEntity(windCharge);
        }

        //propulse les entités autour
        AABB area = new AABB(
                player.getX() - 8, player.getY() - 8, player.getZ() - 8,
                player.getX() + 8, player.getY() + 8, player.getZ() + 8);

        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class, area,
                e -> !e.getUUID().equals(player.getUUID()));

        for (LivingEntity entity : nearby) {
            Vec3 dir = entity.position().subtract(player.position()).normalize();
            entity.setDeltaMovement(dir.x * 4.0, 1.2, dir.z * 4.0);
            entity.hurtMarked = true;

            // Force la synchronisation du mouvement vers les clients

            ((net.minecraft.server.level.ServerLevel)level).sendParticles(
                    net.minecraft.core.particles.ParticleTypes.POOF,
                    entity.getX(), entity.getY(), entity.getZ(),
                    10, 0.5, 0.5, 0.5, 0.1);
        }

        net.Leease.leasemod.client.BookCooldownHud.setLastUsed(
                net.Leease.leasemod.client.ElementWheel.Element.AIR);

        //cooldown

        BookCooldownHud.setLastUsed(ElementWheel.Element.AIR);
        setCooldown(player, 12000L);
        player.getCooldowns().addCooldown(ModItems.ELEMENTAL_BOOK.get(), 12000);
    }

    public static void applyFire(ServerPlayer player) {

        // garde pour le switch de BookUsePacket jamais appelée en pratique

        if (isOnCooldown(player)) return;
        applyFireTick(player);
        applyFireCooldown(player);
        net.Leease.leasemod.client.BookCooldownHud.setLastUsed(net.Leease.leasemod.client.ElementWheel.Element.FIRE);
    }
}