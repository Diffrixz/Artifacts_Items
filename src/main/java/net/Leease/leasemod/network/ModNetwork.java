package net.Leease.leasemod.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.Leease.leasemod.network.KCTeleportPacket;
import net.Leease.leasemod.network.TodoSwapPacket;
import net.Leease.leasemod.network.TodoBindPacket;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // enregistre le packet d'invocation pour les items
        //skylorent
        registrar.playToServer(
                SummonPacket.TYPE,
                SummonPacket.CODEC,
                SummonPacket::handle
        );
        //fafa
        registrar.playToServer(
                BookUsePacket.TYPE,
                BookUsePacket.CODEC,
                BookUsePacket::handle
        );
        //astrah
        registrar.playToServer(
                ZephyrShieldPacket.TYPE,
                ZephyrShieldPacket.CODEC,
                ZephyrShieldPacket::handle
        );
        //lease
        registrar.playToServer(
                KCTeleportPacket.TYPE,
                KCTeleportPacket.CODEC,
                KCTeleportPacket::handle
        );
        registrar.playToServer(
                TodoSwapPacket.TYPE,
                TodoSwapPacket.CODEC,
                TodoSwapPacket::handle
        );
        registrar.playToServer(
                TodoBindPacket.TYPE,
                TodoBindPacket.CODEC,
                TodoBindPacket::handle
        );
        registrar.playToServer(
                MonadoUsePacket.TYPE,
                MonadoUsePacket.CODEC,
                MonadoUsePacket::handle
        );
        //suite ?
    }
}