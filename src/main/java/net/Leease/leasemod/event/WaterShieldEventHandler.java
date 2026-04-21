package net.Leease.leasemod.event;

import net.Leease.leasemod.item.ElementalEffects;
import net.Leease.leasemod.item.ZephyrBootsItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber(modid = "leasefactorymod")
public class WaterShieldEventHandler {

    // je vérifie que c'est un joueur
    // je vérifie que le bouclier eau est actif
    // je vérifie que la source de dégâts vient d'un projectile j'ai fait plus simple si on a bottes ou livre ça marche
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult hitResult)) return;
        if (!(hitResult.getEntity() instanceof Player player)) return;

        boolean bootsShield = ZephyrBootsItem.hasBootsShield(player.getUUID());
        boolean waterShield = ElementalEffects.hasWaterShield(
                player.getUUID(), player.level().getGameTime());

        // si l'un des deux boucliers (en gros si j'ai les bottes equipé ou j'ai appuier sur le boutton du spell) est actif on annule le projectile
        if (bootsShield || waterShield) {
            event.setCanceled(true);
        }
    }
}