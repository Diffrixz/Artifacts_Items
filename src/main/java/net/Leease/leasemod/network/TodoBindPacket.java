package net.Leease.leasemod.network;

import net.Leease.leasemod.item.KingCrimsonAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TodoBindPacket(int targetEntityId) implements CustomPacketPayload {

    public static final Type<TodoBindPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "todo_bind")
    );

    public static final StreamCodec<FriendlyByteBuf, TodoBindPacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.targetEntityId()),
                    buf -> new TodoBindPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TodoBindPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Entity target = player.serverLevel().getEntity(packet.targetEntityId());

            // la cible doit être un joueur — Todo ne lie que des joueurs
            if (!(target instanceof Player targetPlayer)) return;

            // je ne peut pas me lier moi meme
            if (targetPlayer.getUUID().equals(player.getUUID())) return;

            // lie la cible
            KingCrimsonAbilities.bindTodoTarget(player.getUUID(), targetPlayer.getUUID());

            // Message de confirmation discret en action bar
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§5Boogie Woogie — " + targetPlayer.getName().getString()
                    ),
                    true // true = action bar, pas le chat
            );
        });
    }
}