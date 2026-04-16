package net.Leease.leasemod.summon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SummonManager {

    // groupe actuel de mob 0=Loups, 1=Zombies, 2=Squelettes, 3=Araignées, 4=Wither Squelettes, 5=Warden

    private static final Map<UUID, Integer> currentGroup = new HashMap<>();
    //currentGroup  stocke quel groupe chaque joueur peut invoquer actuellement.


    // tick auquel le prochain groupe sera disponible donc en gros cool down
    private static final Map<UUID, Long> cooldownUntil = new HashMap<>();

    // cooldowns en ticks entre chaque groupe
    public static final int[] GROUP_COOLDOWNS = {
            600,   // Loups → Zombies (30s)
            600,   // Zombies → Squelettes (30s)
            600,  // Squelettes → Araignées (1min)
            1200,  // Araignées → Wither Squelettes (1min30)
            6000,  // Wither Squelettes → Warden (5min)
            36000   // wither mort → retour Loups (30min)
    };

    public static int getCurrentGroup(Player player) {
        return currentGroup.getOrDefault(player.getUUID(), 0);
    }

    public static boolean isOnCooldown(Player player, long currentTick) {
        long until = cooldownUntil.getOrDefault(player.getUUID(), 0L);
        return currentTick < until;
    }

    public static void nextGroup(Player player, long currentTick) {
        int group = getCurrentGroup(player);
        int cooldown = GROUP_COOLDOWNS[group];
        cooldownUntil.put(player.getUUID(), currentTick + cooldown);
        currentGroup.put(player.getUUID(), (group + 1) % 6);
    }

    public static void reset(Player player, long currentTick) {

        // si le warden meurt = 30min de cooldown et de retour aux loups

        cooldownUntil.put(player.getUUID(), currentTick + GROUP_COOLDOWNS[5]);
        currentGroup.put(player.getUUID(), 0);
    }

    // la methode pour faire spawn regarde ce qui doit etre invoqué
    public static void summon(ServerPlayer player, long currentTick) {
        int group = getCurrentGroup(player);

        //je les mets en case sinn jvois d'autre solution ptr stupide un peu
        switch (group) {
            case 0 -> spawnGroup(player, EntityType.WOLF, 6);
            case 1 -> spawnGroup(player, EntityType.ZOMBIE, 6);
            case 2 -> spawnGroup(player, EntityType.SKELETON, 6);
            case 3 -> spawnGroup(player, EntityType.CAVE_SPIDER, 4);
            case 4 -> spawnGroup(player, EntityType.WITHER_SKELETON, 3);
            case 5 -> {
                net.minecraft.world.entity.boss.wither.WitherBoss wither =
                        (net.minecraft.world.entity.boss.wither.WitherBoss) net.minecraft.world.entity.EntityType.WITHER.create(player.level());
                if (wither != null) {
                    wither.setPos(player.getX() + 2, player.getY() + 5, player.getZ());
                    wither.setPersistenceRequired();
                    wither.setAggressive(true);
                    SummonManager.setWitherAlive(player.getUUID(), true);
                    player.level().addFreshEntity(wither);
                    wither.addTag("summoned_by_" + player.getUUID().toString());
                }
            }}

        nextGroup(player, currentTick);
    }

    private static void spawnGroup(net.minecraft.server.level.ServerPlayer player, net.minecraft.world.entity.EntityType<?> type, int count) {
        for (int i = 0; i < count; i++) {
            net.minecraft.world.entity.Mob mob = (net.minecraft.world.entity.Mob) type.create(player.level());
            if (mob != null) {
                mob.setPos(player.getX() + i, player.getY(), player.getZ());

                // les mobs attaquent tout le monde sauf le joueur je le fait quand je fini
                player.level().addFreshEntity(mob);

                // c bon jéfini les mobs n'attaquent pas le joueur
                mob.setTarget(null);
                ((net.minecraft.world.entity.Mob) mob).addTag("summoned_by_" + player.getUUID().toString());


                // force les mobs à être hostile
                if (mob instanceof net.minecraft.world.entity.monster.Monster) {
                    mob.setAggressive(true);
                }
                // cherche une cible proche automatiquement sinn ils tapents prsn

                mob.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(
                        mob, net.minecraft.world.entity.LivingEntity.class, true,
                        target -> !(target instanceof net.minecraft.world.entity.player.Player p &&
                                mob.getTags().stream().anyMatch(tag -> tag.equals("summoned_by_" + p.getUUID())))
                                && !target.getTags().stream().anyMatch(tag -> tag.startsWith("summoned_by_"))
                ));
            }
        }
    }
    private static final java.util.Set<java.util.UUID> witherAlive = new java.util.HashSet<>();
    public static void setWitherAlive(java.util.UUID uuid, boolean alive) {
        if (alive) witherAlive.add(uuid);
        else witherAlive.remove(uuid);
    }

    public static boolean hasWitherAlive(java.util.UUID uuid) {
        return witherAlive.contains(uuid);
    }
}