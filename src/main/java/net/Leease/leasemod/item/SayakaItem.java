package net.Leease.leasemod.item;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class SayakaItem extends SwordItem {
    public SayakaItem() {
        super(Tiers.NETHERITE, new Properties()
                .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.0F)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && player.getMainHandItem().equals(stack)) {

            // regen 3 permanent
            if (!player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION, 40, 2, false, false, false
                ));
            }

            // speed 1
            if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED, 40, 0, false, false, false
                ));
            }

            if (!player.hasEffect(MobEffects.ABSORPTION)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.ABSORPTION, 40, 0, false, false, false
                ));
            }

            // weakness 1             if (!player.hasEffect(MobEffects.WEAKNESS)) { a voir si je le mets ou pa pour l'instant je garde comme ça
            //                player.addEffect(new MobEffectInstance(
            //                        MobEffects.WEAKNESS, 40, 0, false, false, false
            //                ));
            //            }
        }

    }
    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, net.minecraft.world.InteractionHand hand) {
        if (!level.isClientSide) {
            // Force 2 pendant 20s
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_BOOST, 400, 1, false, false, false
            ));
        }
        player.getCooldowns().addCooldown(this, 1800);
        return net.minecraft.world.InteractionResultHolder.success(player.getItemInHand(hand));
    }
}