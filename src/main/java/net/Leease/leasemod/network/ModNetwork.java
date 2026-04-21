package net.Leease.leasemod.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // enregistre le packet d'invocation pour skylorent
        registrar.playToServer(
                SummonPacket.TYPE,
                SummonPacket.CODEC,
                SummonPacket::handle
        );
        registrar.playToServer(
                BookUsePacket.TYPE,
                BookUsePacket.CODEC,
                BookUsePacket::handle
        );
        registrar.playToServer(
                ZephyrShieldPacket.TYPE,
                ZephyrShieldPacket.CODEC,
                ZephyrShieldPacket::handle
        );
    }
}