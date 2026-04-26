package net.Leease.leasemod.client;

import net.Leease.leasemod.item.ModItems;
import net.Leease.leasemod.network.KCTeleportPacket;
import net.Leease.leasemod.network.TodoSwapPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KingCrimsonKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;
        if (mc.screen != null) return;

        // verifie que le joueur a le Pink Crimson dans son inventaire

        boolean hasItem = player.getInventory().items.stream()
                .anyMatch(stack -> stack.is(ModItems.PINK_CRIMSON.get()));
        if (!hasItem) return;

        //  KING CRIMSON

        if (ClientEvents.KC_KEY.consumeClick()) {

            // laser jusqu'à 20 blocs vu que je suis trop con pour faire fonctionné un truc juste au curseur

            double range = 20.0;
            Vec3 start = player.getEyePosition();
            Vec3 end = start.add(player.getLookAngle().scale(range));

            Entity target = null;
            double closestDist = range;

            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == player) continue;

                // j'ai agrandit legerement la hitbox pour faciliter la vise prcq jpue la mrd skill issue

                net.minecraft.world.phys.AABB box = entity.getBoundingBox().inflate(0.3);
                java.util.Optional<Vec3> hit = box.clip(start, end);
                if (hit.isPresent()) {
                    double dist = start.distanceTo(hit.get());
                    if (dist < closestDist) {
                        closestDist = dist;
                        target = entity;
                    }
                }
            }

            if (target != null) {
                PacketDistributor.sendToServer(new KCTeleportPacket(target.getId()));
            }
        }

        // TODO

        if (ClientEvents.TODO_KEY.consumeClick()) {

            // pas besoin d'envoyer de cible le serveur connaît normalement deja la cible liée (israel)

            PacketDistributor.sendToServer(new TodoSwapPacket());
        }
    }
}