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

//l'item d'astrah

public class ZephyrBootsItem extends ArmorItem {

    private static final Set<UUID> playersWithBoots = new HashSet<>();

    public ZephyrBootsItem() {

        super(ArmorMaterials.NETHERITE, Type.BOOTS, new Properties());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

        if (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.FEET).equals(stack)) {


            playersWithBoots.add(player.getUUID());
            // l'effet de speed

            if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED, 40, 0, false, false, false
                ));
            }

            // c le stepheight

            player.getAttribute(Attributes.STEP_HEIGHT)
                    .setBaseValue(1.5);


            // les particules quand il marche sur les bottes humm les pids a astra

            if (level.isClientSide && player.onGround() && player.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
                level.addParticle(
                        ParticleTypes.POOF,
                        player.getX(), player.getY(), player.getZ(),
                        0, 0, 0
                );
            }
        }

        // c'est ça qui remet le stepheight normal et l'infini si les bottes sont pas equipe sinn ça reste a jamais

        if (entity instanceof Player player && !player.getItemBySlot(EquipmentSlot.FEET).equals(stack)) {
            player.getAttribute(Attributes.STEP_HEIGHT)
                    .setBaseValue(0.6);
                    playersWithBoots.remove(player.getUUID());

        }
    }
    private static final Map<UUID, Long> shieldUntil = new HashMap<>();
    private static final long SHIELD_DURATION = 5400L; // 4min30

    public static void activateShield(UUID uuid, long currentTick) {
        shieldUntil.put(uuid, currentTick + SHIELD_DURATION);
    }

    public static boolean hasBootsShield(UUID uuid) {
        // je garde hasBootsShield mais maintenant il verifie le temp
        // au lieu d'être passif permanent
        return false; // sera mis à jour avec le tick
    }

    public static boolean isShieldActive(UUID uuid, long currentTick) {
        return currentTick < shieldUntil.getOrDefault(uuid, 0L);
    }
}

