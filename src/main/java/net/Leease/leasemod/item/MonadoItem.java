package net.Leease.leasemod.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.entity.LivingEntity;

public class MonadoItem extends SwordItem {

    public MonadoItem() {
        super(Tiers.NETHERITE, new Properties()
                .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4, -2.4F)));
    }
}