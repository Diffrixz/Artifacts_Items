package net.Leease.leasemod.event;

import net.Leease.leasemod.summon.SummonManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber(modid = "leasefactorymod")
public class SummonEventHandler {

    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && event.getNewAboutToBeSetTarget() instanceof Player player) {

            // si le mob a été invoqué par ce joueur il l'attaque pas

            if (mob.getTags().stream().anyMatch(tag -> tag.equals("summoned_by_" + player.getUUID()))) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onWitherDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.boss.wither.WitherBoss wither) {
            wither.getTags().stream()
                    .filter(tag -> tag.startsWith("summoned_by_"))
                    .findFirst()
                    .ifPresent(tag -> {
                        String uuidStr = tag.replace("summoned_by_", "");
                        java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
                        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) wither.level();
                        net.minecraft.world.entity.player.Player player = level.getPlayerByUUID(uuid);
                        if (player != null) {
                            // Wither mort = reset 30min + retour aux loups
                            SummonManager.setWitherAlive(uuid, false);
                            SummonManager.reset(player, level.getGameTime());
                        }
                    });
        }
    }
}