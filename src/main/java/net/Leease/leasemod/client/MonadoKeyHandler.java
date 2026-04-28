package net.Leease.leasemod.client;

import net.Leease.leasemod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MonadoKeyHandler {

    private static boolean wasHeld = false;

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;
        if (mc.screen != null) return;

        // Vérifie que le joueur tient la Monado en main
        boolean monadoInHand = player.getMainHandItem().is(ModItems.MONADO.get())
                || player.getOffhandItem().is(ModItems.MONADO.get());

        if (!monadoInHand) {
            if (MonadoWheel.isOpen()) {
                MonadoWheel.close();
                mc.mouseHandler.grabMouse();
            }
            wasHeld = false;
            return;
        }

        boolean isHeld = ClientEvents.MONADO_KEY.isDown();

        if (!isHeld && wasHeld) {
            //ça envoie l'art sélectionné au serveur
            int artIndex = MonadoWheel.getSelected().ordinal();
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new net.Leease.leasemod.network.MonadoUsePacket(artIndex));
            MonadoWheel.close();
            mc.mouseHandler.grabMouse();
        }

        wasHeld = isHeld;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!MonadoWheel.isOpen()) return;
        if (mc.player == null) return;

        double mouseX = mc.mouseHandler.xpos()
                * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos()
                * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        MonadoWheel.updateSelection(mouseX, mouseY);
        MonadoWheel.render(event.getGuiGraphics());
    }
}