package net.Leease.leasemod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PinkCrimsonItem extends Item {

    public PinkCrimsonItem() {
        super(new Properties().stacksTo(1));
    }

    // appele chaque tick tant que l'item est dans l'inventaire

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        long currentTick = level.getGameTime();

        // recharge naturelle KC comme dit dans l'abilite 1 charge toutes les 24h

        KingCrimsonAbilities.tickRecharge(player.getUUID(), currentTick);
    }

    // appele quand le joueur frappe une entite à mains nues

    // utilisé pour lier la cible Todo

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {

        // je vais gere le bind Todo côté serveur via le packet,

        // donc ici je ne fait rien a voir TodoBindPacket

        return false;
    }

    // appele quand le joueur tue une entite avec n'importe quel item

    //je vais gere ça via un event separe dans KingCrimsonEventHandler
}