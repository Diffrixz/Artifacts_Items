//c'est pour l'invisibilite de tt armure comprise
package net.Leease.leasemod.client;

import net.Leease.leasemod.item.KingCrimsonAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KingCrimsonRenderHandler {

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        // si le joueur a l'effet invisibilite KC j'ai decide d'annule tout le rendu prcq jsp ça marche pas

        if (player.hasEffect(MobEffects.INVISIBILITY)) {

            // verifie que c'est bien notre joueur local

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getUUID().equals(player.getUUID())) return;

            // annule le rendu complet du joueur

            event.setCanceled(true);
        }
    }
}