package net.Leease.leasemod;

import net.Leease.leasemod.effect.ModEffects;
import net.Leease.leasemod.entity.ModEntities;
import net.Leease.leasemod.item.ModItems;
import net.Leease.leasemod.particle.ModParticles;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ItemsMod.MOD_ID)
public class ItemsMod {
    public static final String MOD_ID = "leasefactorymod";
    public static final Logger LOGGER = LogUtils.getLogger();
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ItemsMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModEffects.register(modEventBus);
        ModParticles.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public class ModSounds {
        public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
                DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ItemsMod.MOD_ID);

        // c le son du slash de la scythe
        public static final DeferredHolder<SoundEvent, SoundEvent> BLOODWARD_SLASH =
                SOUND_EVENTS.register("item.bloodward_slash",
                        () -> SoundEvent.createVariableRangeEvent(
                                ResourceLocation.fromNamespaceAndPath(ItemsMod.MOD_ID, "item.bloodward_slash")
                        ));

        public static final DeferredHolder<SoundEvent, SoundEvent> KC_TELEPORT =
                SOUND_EVENTS.register("item.kc_teleport",
                        () -> SoundEvent.createVariableRangeEvent(
                                ResourceLocation.fromNamespaceAndPath(ItemsMod.MOD_ID, "item.king_crimson")
                        ));

        public static final DeferredHolder<SoundEvent, SoundEvent> TODO_CLAP =
                SOUND_EVENTS.register("item.todo_clap",
                        () -> SoundEvent.createVariableRangeEvent(
                                ResourceLocation.fromNamespaceAndPath(ItemsMod.MOD_ID, "item.clap")
                        ));

        public static void register(IEventBus eventBus) {
            SOUND_EVENTS.register(eventBus);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.SCYTHE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = ItemsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
