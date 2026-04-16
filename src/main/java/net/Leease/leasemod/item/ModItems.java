package net.Leease.leasemod.item;

import net.Leease.leasemod.ItemsMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ItemsMod.MOD_ID);

    //scythe de neuille staill
    public static final DeferredItem<ScytheItem> SCYTHE = ITEMS.register("scythe",
            ScytheItem::new);

    // bottes de neuille lorenzo
    public static final DeferredItem<ZephyrBootsItem> ZEPHYR = ITEMS.register("zephyr",
            ZephyrBootsItem::new);

    // sayak epee rat
    public static final DeferredItem<SayakaItem> SAYAKA = ITEMS.register("sayaka_katana",
            SayakaItem::new);

    // Technique des Dix Ombres de skylorent
    public static final DeferredItem<CrownItem> CROWN = ITEMS.register("crown",
            CrownItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
