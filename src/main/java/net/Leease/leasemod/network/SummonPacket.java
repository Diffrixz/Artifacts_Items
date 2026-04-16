package net.Leease.leasemod.network;

import net.Leease.leasemod.summon.SummonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SummonPacket() implements CustomPacketPayload {

    public static final Type<SummonPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "summon")
    );

    public static final StreamCodec<FriendlyByteBuf, SummonPacket> CODEC =
            StreamCodec.unit(new SummonPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // c gerer côté serveur
    public static void handle(SummonPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                long currentTick = serverPlayer.level().getGameTime();

                if (!SummonManager.isOnCooldown(serverPlayer, currentTick)
                        && !SummonManager.hasWitherAlive(serverPlayer.getUUID())) {
                    SummonManager.summon(serverPlayer, currentTick);
                }
            }
        });
    }
}