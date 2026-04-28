package net.Leease.leasemod.item;

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

public class MonadoEffects {

    private static final Map<UUID, Long> cooldownUntil = new HashMap<>();

    // cooldowns en ticks pour chaque art
    private static final long[] ART_COOLDOWNS = {
            700,   // Buster 35s
            2000,  // Enchant 1m40
            1400,  // Shield 1m10
            900,   // Speed 45s
            3600,  // Eater 3m
            1200,  // Armour 1m
            1600,  // Cyclone 1m20
            1200   // Purge 1m
    };

    public static boolean isOnCooldown(ServerPlayer player) {
        long until = cooldownUntil.getOrDefault(player.getUUID(), 0L);
        return player.level().getGameTime() < until;
    }

    private static void setCooldown(ServerPlayer player, int artIndex) {
        cooldownUntil.put(player.getUUID(),
                player.level().getGameTime() + ART_COOLDOWNS[artIndex]);
        // Cooldown visuel sur l'item
        player.getCooldowns().addCooldown(
                ModItems.MONADO.get(), (int) ART_COOLDOWNS[artIndex]);
    }

    public static void applyArt(ServerPlayer player, int artIndex) {
        switch (artIndex) {
            case 0 -> applyBuster(player);
            case 1 -> applyEnchant(player);
            case 2 -> applyShield(player);
            case 3 -> applySpeed(player);
            case 4 -> applyEater(player);
            case 5 -> applyArmour(player);
            case 6 -> applyCyclone(player);
            case 7 -> applyPurge(player);
        }
        setCooldown(player, artIndex);
    }

    // Buster — projectile de particules 3.5 coeurs 15 blocs
    private static void applyBuster(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 start = player.getEyePosition();

        for (double d = 0; d <= 15; d += 0.5) {
            Vec3 pos = start.add(look.scale(d));
            AABB hitbox = new AABB(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5,
                    pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);

            List<LivingEntity> targets = player.level().getEntitiesOfClass(
                    LivingEntity.class, hitbox,
                    e -> !e.getUUID().equals(player.getUUID()));

            for (LivingEntity target : targets) {
                target.hurt(player.damageSources().magic(), 7.0F);
                return;
            }
        }
    }

    // Enchant — Force
    private static void applyEnchant(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_BOOST, 2000, 1, false, true, true));
    }

    // Shield — Resistance sans slowness
    private static void applyShield(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, 1400, 3, false, true, true));
    }

    // Speed
    private static void applySpeed(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED, 900, 1, false, true, true));
    }

    // Eater — cleanse tous les effets sur la cible
    private static void applyEater(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 start = player.getEyePosition();

        for (double d = 0; d <= 15; d += 0.5) {
            Vec3 pos = start.add(look.scale(d));
            AABB hitbox = new AABB(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5,
                    pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);

            List<LivingEntity> targets = player.level().getEntitiesOfClass(
                    LivingEntity.class, hitbox,
                    e -> !e.getUUID().equals(player.getUUID()));

            for (LivingEntity target : targets) {
                // Supprime tous les effets
                target.getActiveEffects().forEach(effect ->
                        target.removeEffect(effect.getEffect()));
                return;
            }
        }
    }

    // Armour — Absorption
    private static void applyArmour(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.ABSORPTION, 1200, 2, false, true, true));
    }

    // Cyclone — dégâts 3x3 vers les pieds
    private static void applyCyclone(ServerPlayer player) {
        AABB hitbox = new AABB(
                player.getX() - 1.5, player.getY() - 1, player.getZ() - 1.5,
                player.getX() + 1.5, player.getY() + 1, player.getZ() + 1.5);

        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, hitbox,
                e -> !e.getUUID().equals(player.getUUID()));

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().magic(), 7.0F);
        }
    }

    // Purge — projectile slowness + cécité
    private static void applyPurge(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 start = player.getEyePosition();

        for (double d = 0; d <= 15; d += 0.5) {
            Vec3 pos = start.add(look.scale(d));
            AABB hitbox = new AABB(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5,
                    pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);

            List<LivingEntity> targets = player.level().getEntitiesOfClass(
                    LivingEntity.class, hitbox,
                    e -> !e.getUUID().equals(player.getUUID()));

            for (LivingEntity target : targets) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, 100, 3, false, true, true));
                target.addEffect(new MobEffectInstance(
                        MobEffects.BLINDNESS, 100, 0, false, true, true));
                return;
            }
        }
    }
}