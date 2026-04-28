package net.Leease.leasemod.network;

import net.Leease.leasemod.item.MonadoEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MonadoUsePacket(int artIndex) implements CustomPacketPayload {

    public static final Type<MonadoUsePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "monado_use")
    );

    public static final StreamCodec<FriendlyByteBuf, MonadoUsePacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.artIndex()),
                    buf -> new MonadoUsePacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MonadoUsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                // Vérifie cooldown avant d'appliquer
                if (!MonadoEffects.isOnCooldown(serverPlayer)) {
                    MonadoEffects.applyArt(serverPlayer, packet.artIndex());
                }
            }
        });
    }
}