package net.Leease.leasemod.client;

import net.Leease.leasemod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "leasefactorymod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BookCooldownHud {

    // On mémorise l'élément qui a posé le cooldown — ne change pas quand on tourne la roue
    private static ElementWheel.Element lastUsedElement = ElementWheel.Element.FIRE;
    // On mémorise le pourcentage au moment où le cooldown a été posé
    private static float lastCooldownPercent = 0f;

    public static void setLastUsed(ElementWheel.Element element) {
        lastUsedElement = element;
        lastCooldownPercent = 1.0f;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        boolean hasBook = player.getInventory().items.stream()
                .anyMatch(stack -> stack.is(ModItems.ELEMENTAL_BOOK.get()));
        if (!hasBook) return;

        float cooldownPercent = player.getCooldowns()
                .getCooldownPercent(ModItems.ELEMENTAL_BOOK.get(), mc.getTimer().getRealtimeDeltaTicks());
        if (cooldownPercent <= 0) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int barWidth = 182;
        int barHeight = 8;
        int x = screenWidth / 2 - barWidth / 2;
        int y = screenHeight - 72;

        // Fond gris
        graphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        int filledWidth = (int)(barWidth * (1.0f - cooldownPercent));

        // Couleur fixe selon l'élément qui a posé le cooldown — pas celui de la roue
        int color = switch (lastUsedElement) {
            case FIRE  -> 0xFFFF4500;
            case WATER -> 0xFF1E90FF;
            case EARTH -> 0xFF8B4513;
            case AIR   -> 0xFFE0E0E0;
        };

        graphics.fill(x, y, x + filledWidth, y + barHeight, color);

        // Texte du temps restant
        long totalTicks = switch (lastUsedElement) {
            case FIRE  -> 24000L;
            case WATER -> 6000L;
            case EARTH -> 48000L;
            case AIR   -> 12000L;
        };

        long remainingTicks = (long)(cooldownPercent * totalTicks);
        long remainingSeconds = remainingTicks / 20;
        if (remainingSeconds > 0) {
            graphics.drawCenteredString(mc.font, remainingSeconds + "s",
                    screenWidth / 2, y - 10, 0xFFFFFF);
        }
    }
}