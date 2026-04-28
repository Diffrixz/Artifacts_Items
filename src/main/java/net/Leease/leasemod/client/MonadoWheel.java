package net.Leease.leasemod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class MonadoWheel {

    public enum Art {
        BUSTER, ENCHANT, SHIELD, SPEED, EATER, ARMOUR, CYCLONE, PURGE
    }

    private static Art selectedArt = Art.BUSTER;
    private static boolean isOpen = false;

    public static void open() { isOpen = true; }
    public static void close() { isOpen = false; }
    public static boolean isOpen() { return isOpen; }
    public static Art getSelected() { return selectedArt; }

    public static void updateSelection(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        double cx = mc.getWindow().getGuiScaledWidth() / 2.0;
        double cy = mc.getWindow().getGuiScaledHeight() / 2.0;

        double dx = mouseX - cx;
        double dy = mouseY - cy;

        // Zone morte au centre
        if (Math.sqrt(dx * dx + dy * dy) < 20) return;

        // Calcule l'angle de la souris
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;

        // 8 arts = 45 degrés chacun
        int index = (int)((angle + 22.5) / 45) % 8;
        selectedArt = Art.values()[index];
    }

    public static void render(GuiGraphics graphics) {
        if (!isOpen) return;
        Minecraft mc = Minecraft.getInstance();
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        int radius = 80;

        for (int i = 0; i < 8; i++) {
            Art art = Art.values()[i];
            double angle = Math.toRadians(i * 45 - 90);
            int x = cx + (int)(Math.cos(angle) * radius) - 16;
            int y = cy + (int)(Math.sin(angle) * radius) - 16;

            boolean isSelected = (selectedArt == art);
            int color = isSelected ? 0xFFFFFFFF : 0x99FFFFFF;

            graphics.drawCenteredString(
                    mc.font,
                    art.name(),
                    x + 16,
                    y + 16,
                    color
            );
        }
    }
}