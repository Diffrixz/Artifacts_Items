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
public class BookKeyHandler {

    private static boolean wasHeld = false;

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        // Si un écran est ouvert on ne fait rien
        if (mc.screen != null) return;

        boolean bookInHand = player.getMainHandItem().is(ModItems.ELEMENTAL_BOOK.get())
                || player.getOffhandItem().is(ModItems.ELEMENTAL_BOOK.get());

        if (!bookInHand) {
            if (ElementWheel.isOpen()) {
                ElementWheel.close();
                mc.mouseHandler.grabMouse();
            }
            wasHeld = false;
            return;
        }

        boolean isHeld = ClientEvents.BOOK_KEY.isDown();

        if (isHeld && !wasHeld) {
            // Touche vient d'être enfoncée — on ouvre la roue
            ElementWheel.open();
            mc.mouseHandler.releaseMouse();
        }

        if (!isHeld && wasHeld) {
            // Touche vient d'être relâchée — on ferme la roue
            ElementWheel.close();
            mc.mouseHandler.grabMouse();
        }

        wasHeld = isHeld;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!ElementWheel.isOpen()) return;
        if (mc.player == null) return;

        double mouseX = mc.mouseHandler.xpos()
                * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos()
                * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        ElementWheel.updateSelection(mouseX, mouseY);
        ElementWheel.render(event.getGuiGraphics());
    }
}