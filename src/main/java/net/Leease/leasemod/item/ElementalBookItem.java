package net.Leease.leasemod.item;

import net.Leease.leasemod.client.ElementWheel;
import net.Leease.leasemod.network.BookUsePacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class ElementalBookItem extends Item {

    public ElementalBookItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            int index = ElementWheel.getSelected().ordinal();

            // Stocke l'élément actif dans le DataComponent
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, existing -> {
                CompoundTag tag = existing.copyTag();
                tag.putInt("activeElement", index);
                return CustomData.of(tag);
            });

            // Pour les éléments non-continus (eau, terre, air) on envoie le packet immédiatement
            if (index != 0) {
                PacketDistributor.sendToServer(new BookUsePacket(index));
            }
        }

        // Pour tous les éléments on démarre l'usage — mais seul le feu (index 0) utilise onUseTick
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) return;
        if (!(livingEntity instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;

        int index = customData.copyTag().getInt("activeElement");

        // Seulement le feu tourne en continu
        if (index == 0 && !ElementalEffects.isOnCooldown(serverPlayer)) {
            ElementalEffects.applyFireTick(serverPlayer);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (level.isClientSide) return;
        if (!(livingEntity instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;

        int index = customData.copyTag().getInt("activeElement");

        // Quand le joueur relâche — on pose le cooldown du feu
        if (index == 0) {
            ElementalEffects.applyFireCooldown(serverPlayer);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
}