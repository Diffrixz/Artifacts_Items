package net.Leease.leasemod.network;

import net.Leease.leasemod.item.ZephyrBootsItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ZephyrShieldPacket() implements CustomPacketPayload {

    public static final Type<ZephyrShieldPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "zephyr_shield")
    );

    public static final StreamCodec<FriendlyByteBuf, ZephyrShieldPacket> CODEC =
            StreamCodec.unit(new ZephyrShieldPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ZephyrShieldPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                // active l'infini pendant 4min30 = 5400 ticks
                ZephyrBootsItem.activateShield(serverPlayer.getUUID(),
                        serverPlayer.level().getGameTime());
            }
        });
    }
}