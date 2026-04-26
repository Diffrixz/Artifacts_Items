package net.Leease.leasemod.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KingCrimsonAbilities {

    // KING CRIMSON

    // cooldown de 2 minutes entre chaque tp (2min donc 2400 ticks)

    private static final long KC_COOLDOWN_TICKS = 0L;

    // cooldown 24h si reserve vide (24h = 1 728 000 ticks)

    private static final long KC_PENALTY_TICKS = 1728000L;

    // reserve de base

    private static final int KC_MAX_CHARGES = 100; // A CHANGER

    // cap absolu

    private static final int KC_OVERCAP = 15;

    // tick jusqu'auquel le joueur ne peut pas utiliser KC

    private static final Map<UUID, Long> kcCooldownUntil = new HashMap<>();

    // nombre de charges restantes

    private static final Map<UUID, Integer> kcCharges = new HashMap<>();

    // tick de la dernière recharge naturelle

    private static final Map<UUID, Long> kcLastRecharge = new HashMap<>();

    // Durée invincibilité après tp (0.4s = 8 ticks)

    private static final long KC_INVINCIBILITY_TICKS = 40L;
    private static final Map<UUID, Long> kcInvincibleUntil = new HashMap<>();

    //TODO

    // Cooldown swap 30s = 600 ticks

    private static final long TODO_COOLDOWN_TICKS = 600L;
    private static final Map<UUID, Long> todoCooldownUntil = new HashMap<>();

    // UUID de la cible liée

    private static final Map<UUID, UUID> todoTarget = new HashMap<>();

    // KC GETTERS  CHECKS

    public static int getCharges(UUID uuid) {
        return kcCharges.getOrDefault(uuid, KC_MAX_CHARGES);
    }

    public static boolean isKCOnCooldown(UUID uuid, long currentTick) {
        return currentTick < kcCooldownUntil.getOrDefault(uuid, 0L);
    }

    public static boolean isInvincible(UUID uuid, long currentTick) {
        return currentTick < kcInvincibleUntil.getOrDefault(uuid, 0L);
    }

    // KC RECHARGE NATURELLE

    // a appeler chaque tick serveur (via l'item inventoryTick)

    // 1 charge toutes les 24h soit 1 728 000 ticks

    private static final long KC_RECHARGE_INTERVAL = 1728000L;

    public static void tickRecharge(UUID uuid, long currentTick) {
        long lastRecharge = kcLastRecharge.getOrDefault(uuid, currentTick);
        if (currentTick - lastRecharge >= KC_RECHARGE_INTERVAL) {
            int charges = getCharges(uuid);
            // recharge seulement si en dessous du cap absolu
            if (charges < KC_OVERCAP) {
                kcCharges.put(uuid, charges + 1);
            }
            kcLastRecharge.put(uuid, currentTick);
        }
    }

    //KC TELEPORTATION

    public static boolean tryKCTeleport(ServerPlayer player, Entity target) {
        UUID uuid = player.getUUID();
        long currentTick = player.level().getGameTime();

        // verifie cooldown
        if (isKCOnCooldown(uuid, currentTick)) return false;

        // verifie charges
        int charges = getCharges(uuid);
        if (charges <= 0) return false;

        // verifie distance en 3 dimension max 20 blocs
        double dx = player.getX() - target.getX();
        double dz = player.getZ() - target.getZ();
        if (Math.sqrt(dx * dx + dz * dz) > 20.0) return false;

        // calcule la position 2 blocs derrière la cible
        // "derrière" = direction opposée au regard de la cible
        float targetYaw = (float) Math.toRadians(target.getYRot());
        double behindX = target.getX() + Math.sin(targetYaw) * 2;
        double behindZ = target.getZ() - Math.cos(targetYaw) * 2;
        double tpX = behindX;
        double tpY = target.getY();
        double tpZ = behindZ;

        // verifie que les 2 blocs à cette position sont de l'air
        // (Steve fait 2 blocs de haut donc on vérifie Y et Y+1)
        if (!isClearSpace(player, tpX, tpY, tpZ)) return false;

        // tp le joueur modifié pour la 17eme fois btw
        double finalY = tpY;
        for (int dy = -1; dy <= 1; dy++) {
            net.minecraft.core.BlockPos feet = net.minecraft.core.BlockPos.containing(tpX, tpY + dy, tpZ);
            net.minecraft.core.BlockPos head = feet.above();
            if (player.level().getBlockState(feet).isAir()
                    && player.level().getBlockState(head).isAir()) {
                finalY = tpY + dy;
                break;
            }
        }

        player.teleportTo(tpX, finalY, tpZ);

        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.INVISIBILITY,
                (int) KC_INVINCIBILITY_TICKS,
                0, false, false, false
        ));

        // invincibilité 0.4s
        kcInvincibleUntil.put(uuid, currentTick + KC_INVINCIBILITY_TICKS);

        // pose le cooldown 2min
        kcCooldownUntil.put(uuid, currentTick + KC_COOLDOWN_TICKS);

        // consomme une charge
        kcCharges.put(uuid, charges - 1);

        // si reserve vide cooldown 24h par-dessus
        if (charges - 1 <= 0) {
            kcCooldownUntil.put(uuid, currentTick + KC_PENALTY_TICKS);
        }

        return true;
    }

    // verifie que les 2 blocs de haut et de large sont libres donc en gros 2x2 connard
    private static boolean isClearSpace(ServerPlayer player, double x, double y, double z) {
        // verifie Y, Y+1 mais aussi Y-1 au cas où le terrain est inégal prcq ça marchait pas avant si j'etait un bloc plus haut ou plus bas
        for (int dy = -1; dy <= 1; dy++) {
            net.minecraft.core.BlockPos feet = net.minecraft.core.BlockPos.containing(x, y + dy, z);
            net.minecraft.core.BlockPos head = feet.above();
            if (player.level().getBlockState(feet).isAir()
                    && player.level().getBlockState(head).isAir()) {
                return true;
            }
        }
        return false;
    }
    // KC KILL

    // appeler quand le joueur tue un joueur avec l'item
    public static void onPlayerKill(UUID uuid) {
        int charges = getCharges(uuid);
        if (charges >= KC_MAX_CHARGES) {
            // deja à 10+ → +1 jusqu'au cap 15
            kcCharges.put(uuid, Math.min(charges + 1, KC_OVERCAP));
        } else {
            // recharge à 10
            kcCharges.put(uuid, KC_MAX_CHARGES);
        }
        // Un kill annule aussi le cooldown 24h si pénalité active
        // (le joueur a prouvé qu'il joue bien)
        // On laisse le cooldown 2min normal en place par contre
    }

    // TODO GETTERS CHECKS

    public static boolean hasTodoTarget(UUID uuid) {
        return todoTarget.containsKey(uuid);
    }

    public static UUID getTodoTarget(UUID uuid) {
        return todoTarget.get(uuid);
    }

    public static boolean isTodoOnCooldown(UUID uuid, long currentTick) {
        return currentTick < todoCooldownUntil.getOrDefault(uuid, 0L);
    }

    // TODO LIENT UNE CIBLE

    public static void bindTodoTarget(UUID playerUUID, UUID targetUUID) {
        todoTarget.put(playerUUID, targetUUID);
    }

    public static void clearTodoTarget(UUID playerUUID) {
        todoTarget.remove(playerUUID);
    }

    // TODO SWAP

    public static boolean tryTodoSwap(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long currentTick = player.level().getGameTime();

        // verifie qu'une cible est liee
        if (!hasTodoTarget(uuid)) return false;

        // verifie cooldown
        if (isTodoOnCooldown(uuid, currentTick)) return false;

        UUID targetUUID = getTodoTarget(uuid);
        // cherche le joueur cible sur le serveur
        ServerPlayer target = player.serverLevel().getServer()
                .getPlayerList().getPlayer(targetUUID);

        // cible introuvable ou deconnectee echoue silencieusement genre vide absolue
        if (target == null) {
            return false;
        }

        // verifie distance max 100 blocs
        if (player.distanceTo(target) > 100.0) return false;

        // sauvegarde les positions
        Vec3 playerPos = player.position();
        float playerYRot = player.getYRot();
        Vec3 targetPos = target.position();
        float targetYRot = target.getYRot();

        // swap
        player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        player.setYRot(targetYRot);
        target.teleportTo(playerPos.x, playerPos.y, playerPos.z);
        target.setYRot(playerYRot);

        // cooldown 30s
        todoCooldownUntil.put(uuid, currentTick + TODO_COOLDOWN_TICKS);

        return true;
    }

    // appele quand la cible boit du lait (LivingEntityUseItemEvent)
    public static void onTargetDrinkMilk(UUID targetUUID) {
        // cherche si quelqu'un a ce joueur comme cible Todo
        todoTarget.entrySet().removeIf(entry -> entry.getValue().equals(targetUUID));
    }

    // appele quand un joueur se déconnecte ne fait rien ni tp ni rien
    public static void onPlayerDisconnect(UUID uuid) {
    }
}