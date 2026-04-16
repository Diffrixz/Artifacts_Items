package net.Leease.leasemod.entity;

import net.Leease.leasemod.ItemsMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ItemsMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SlashEntity>> SLASH =
            ENTITY_TYPES.register("slash", () ->
                    EntityType.Builder.<SlashEntity>of(SlashEntity::new, MobCategory.MISC)
                            .sized(3.0F, 1.0F)
                            .build("slash"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}