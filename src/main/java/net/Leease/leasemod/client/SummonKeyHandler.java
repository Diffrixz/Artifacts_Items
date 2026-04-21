package net.Leease.leasemod.client;

import net.Leease.leasemod.item.ModItems;
import net.Leease.leasemod.summon.SummonManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SummonKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.level == null) return;

        // ça verifie que la touche summon est appuyer
        if (ClientEvents.SUMMON_KEY.consumeClick()) {
            // ça erifie que le joueur a la couronne dans son slot artefact

            // pour l'instant je verifie juste l'inventaire
            boolean hasCrown = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(ModItems.CROWN.get()));

            if (hasCrown) {
                // envoie un packet au serveur pour invoquer
                // je vais faire les packets apres feignon en gros ça envoie le packé de oe gros spawn les mobs
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.Leease.leasemod.network.SummonPacket());            }
        }
        // ça verifie que la touche de l'infini est appuyée
        if (ClientEvents.ZEPHYR_KEY.consumeClick()) {
            // ça verifie que le joueur a les bottes de zephyr sur ses iepds humm les iepds
            boolean hasBoots = player.getItemBySlot(
                            net.minecraft.world.entity.EquipmentSlot.FEET)
                    .is(ModItems.ZEPHYR.get());

            if (hasBoots) {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                        new net.Leease.leasemod.network.ZephyrShieldPacket());
            }
        }
    }
}