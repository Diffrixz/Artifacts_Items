package net.Leease.leasemod.item;

import net.Leease.leasemod.ItemsMod;
import net.Leease.leasemod.effect.ModEffects;
import net.Leease.leasemod.entity.ModEntities;
import net.Leease.leasemod.entity.SlashEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ScytheItem extends SwordItem {

    public ScytheItem() {
        super(Tiers.NETHERITE, new Properties()
                .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 5, -3.2F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Tire l'ennemi
        Vec3 direction = attacker.position().subtract(target.position()).normalize();
        target.setDeltaMovement(direction.x * 0.37, 0.26, direction.z * 0.37);

        // Invisibilité totale 5s après un kill
        if (target instanceof Player targetPlayer && attacker instanceof Player attackerPlayer) {
            if (target.getHealth() - ((Player) attacker).getAttackStrengthScale(0) <= 0) {
                attackerPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false, false));
            }
        }

        return super.hurtEnemy(stack, target, attacker);

    }

    //c le clic droit :o
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            spawnSlashParticles(level, player);
            // ça joue le son du slash le bails en .ogg
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ItemsMod.ModSounds.BLOODWARD_SLASH.get(), player.getSoundSource(), 1.0F, 1.0F);
        }
        player.getCooldowns().addCooldown(this, 600);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private void spawnSlashParticles(Level level, Player player) {
        SlashEntity slash = new SlashEntity(ModEntities.SLASH.get(), level);
        slash.setPos(player.getX(), player.getEyeY(), player.getZ());
        slash.setDirection(player.getLookAngle());
        slash.setOwner(player);
        level.addFreshEntity(slash);
    }
}