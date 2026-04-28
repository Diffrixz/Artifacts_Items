package net.Leease.leasemod.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class ZephyrBootsItem extends ArmorItem {

    private static final Map<UUID, Long> shieldUntil = new HashMap<>();
    private static final Map<UUID, Long> shieldCooldownUntil = new HashMap<>();
    private static final long SHIELD_DURATION = 1600L;  // 1min20s
    private static final long SHIELD_COOLDOWN = 24000L; // 20min

    public ZephyrBootsItem() {
        super(ArmorMaterials.NETHERITE, Type.BOOTS, new Properties());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

        if (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.FEET).equals(stack)) {

            if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED, 40, 0, false, false, false
                ));
            }

            player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.5);

            if (level.isClientSide && player.onGround() && player.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
                level.addParticle(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
            }
        }

        if (entity instanceof Player player && !player.getItemBySlot(EquipmentSlot.FEET).equals(stack)) {
            player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6);
        }
    }

    public static void activateShield(UUID uuid, long currentTick) {
        if (currentTick < shieldCooldownUntil.getOrDefault(uuid, 0L)) return;
        shieldUntil.put(uuid, currentTick + SHIELD_DURATION);
        shieldCooldownUntil.put(uuid, currentTick + SHIELD_DURATION + SHIELD_COOLDOWN);
    }

    public static boolean isShieldActive(UUID uuid, long currentTick) {
        return currentTick < shieldUntil.getOrDefault(uuid, 0L);
    }
}