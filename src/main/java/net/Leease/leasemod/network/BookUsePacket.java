package net.Leease.leasemod.network;

import net.Leease.leasemod.client.ElementWheel;
import net.Leease.leasemod.item.ElementalEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BookUsePacket(int elementIndex) implements CustomPacketPayload {

    public static final Type<BookUsePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "book_use")
    );

    public static final StreamCodec<FriendlyByteBuf, BookUsePacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.elementIndex()),
                    buf -> new BookUsePacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BookUsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ElementWheel.Element element = ElementWheel.Element.values()[packet.elementIndex()];
                switch (element) {
                    case FIRE  -> ElementalEffects.applyFire(serverPlayer);
                    case WATER -> ElementalEffects.applyWater(serverPlayer);
                    case EARTH -> ElementalEffects.applyEarth(serverPlayer);
                    case AIR   -> ElementalEffects.applyAir(serverPlayer);
                }
            }
        });
    }
}