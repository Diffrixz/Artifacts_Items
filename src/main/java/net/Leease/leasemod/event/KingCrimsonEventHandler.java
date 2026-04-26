package net.Leease.leasemod.event;

import net.Leease.leasemod.item.KingCrimsonAbilities;
import net.Leease.leasemod.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = "leasefactorymod")
public class KingCrimsonEventHandler {

    //  TODO BIND oblige frappe a mains nues

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();

        // verifie que le joueur a le Pink Crimson dans l'inventaire
        boolean hasItem = player.getInventory().items.stream()
                .anyMatch(stack -> stack.is(ModItems.PINK_CRIMSON.get()));
        if (!hasItem) return;

        // verifie que le joueur frappe à mains nues
        if (!player.getMainHandItem().isEmpty()) return;

        Entity target = event.getTarget();

        // la cible doit être un joueur
        if (!(target instanceof Player)) return;

        // envoie le packet de bind cote serveur uniquement
        if (!player.level().isClientSide) {
            KingCrimsonAbilities.bindTodoTarget(
                    player.getUUID(),
                    target.getUUID()
            );
            // Message action bar discret
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§5Boogie Woogie — " + ((Player) target).getName().getString()
                    ),
                    true
            );
        }
    }

    // TODO : lait casse le lien

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {

        // verifie que c'est un joueur qui finit de boire

        if (!(event.getEntity() instanceof Player player)) return;

        // verifie que c'est du lait

        if (!event.getItem().is(Items.MILK_BUCKET)) return;

        // Casse tous les liens Todo qui pointent vers ce joueur

        KingCrimsonAbilities.onTargetDrinkMilk(player.getUUID());
    }

    //  KC : kill joueu recharge l'item

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {

        // verifie que la victime est un joueur

        if (!(event.getEntity() instanceof Player victim)) return;

        // verifie que le tueur est un joueur

        Entity killer = event.getSource().getEntity();
        if (!(killer instanceof Player killerPlayer)) return;

        // verifie que le tueur a le Pink Crimson dans l'inventaire

        boolean hasItem = killerPlayer.getInventory().items.stream()
                .anyMatch(stack -> stack.is(ModItems.PINK_CRIMSON.get()));
        if (!hasItem) return;

        // Recharge KC
        KingCrimsonAbilities.onPlayerKill(killerPlayer.getUUID());
    }
}