package net.Leease.leasemod.client;

import net.Leease.leasemod.entity.ModEntities;
import net.Leease.leasemod.particle.ModParticles;
import net.Leease.leasemod.particle.SlashParticle;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SLASH.get(), NoopRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SLASH.get(), SlashParticle.Provider::new);
    }

    //creation de la touche dans les parametre pour summon

    public static final KeyMapping SUMMON_KEY = new KeyMapping(
            "key.leasefactorymod.summon",
            org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.leasefactorymod"
    );

    public static final KeyMapping BOOK_KEY = new KeyMapping(
            "key.leasefactorymod.book",
            org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.leasefactorymod"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SUMMON_KEY);
        event.register(BOOK_KEY);
        event.register(ZEPHYR_KEY);
    }
    public static final KeyMapping ZEPHYR_KEY = new KeyMapping(
            "key.leasefactorymod.zephyr",
            org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.leasefactorymod"
    );
}