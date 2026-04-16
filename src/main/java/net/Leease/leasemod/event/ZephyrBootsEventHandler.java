package net.Leease.leasemod.event;

import net.Leease.leasemod.item.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = "leasefactorymod")
public class ZephyrBootsEventHandler {

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {

            // si les bottes Zephyr sont équipées et ça annule les degats de chute je le mets a 20

            if (player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.ZEPHYR.get())
                    && event.getDistance() < 20.0F) {
                event.setDamageMultiplier(0);
            }
        }
    }
}