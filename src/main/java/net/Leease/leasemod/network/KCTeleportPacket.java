package net.Leease.leasemod.network;

import net.Leease.leasemod.item.KingCrimsonAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KCTeleportPacket(int targetEntityId) implements CustomPacketPayload {

    public static final Type<KCTeleportPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "kc_teleport")
    );

    public static final StreamCodec<FriendlyByteBuf, KCTeleportPacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.targetEntityId()),
                    buf -> new KCTeleportPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KCTeleportPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            // je recupere l'entite cible par son ID réseau

            Entity target = player.serverLevel().getEntity(packet.targetEntityId());
            if (target == null) return;

            // tente le tp KingCrimson verifie tout cooldown charges distance espace libre
            boolean success = KingCrimsonAbilities.tryKCTeleport(player, target);

            //son KC joueur proche entendent le son
            if (success) {
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        net.Leease.leasemod.ItemsMod.ModSounds.KC_TELEPORT.get(),
                        player.getSoundSource(),
                        1.0F, 1.0F
                );
            }
        });
    }
}