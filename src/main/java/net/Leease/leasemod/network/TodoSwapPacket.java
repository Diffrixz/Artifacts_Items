package net.Leease.leasemod.network;

import net.Leease.leasemod.item.KingCrimsonAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TodoSwapPacket() implements CustomPacketPayload {

    public static final Type<TodoSwapPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "todo_swap")
    );

    public static final StreamCodec<FriendlyByteBuf, TodoSwapPacket> CODEC =
            StreamCodec.unit(new TodoSwapPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TodoSwapPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            boolean success = KingCrimsonAbilities.tryTodoSwap(player);

            if (success) {
                // son de clap
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        net.Leease.leasemod.ItemsMod.ModSounds.TODO_CLAP.get(),
                        player.getSoundSource(),
                        1.0F, 1.0F
                );
            }
        });
    }
}