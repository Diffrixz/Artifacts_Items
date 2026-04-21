package net.Leease.leasemod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ElementWheel {

    public enum Element {
        FIRE, WATER, EARTH, AIR
    }

    private static Element selectedElement = Element.FIRE;
    private static boolean isOpen = false;

    private static final ResourceLocation FIRE_TEX =
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "textures/gui/element_fire.png");
    private static final ResourceLocation WATER_TEX =
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "textures/gui/element_water.png");
    private static final ResourceLocation EARTH_TEX =
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "textures/gui/element_earth.png");
    private static final ResourceLocation AIR_TEX =
            ResourceLocation.fromNamespaceAndPath("leasefactorymod", "textures/gui/element_air.png");

    public static void open() { isOpen = true; }
    public static void close() { isOpen = false; }
    public static boolean isOpen() { return isOpen; }
    public static Element getSelected() { return selectedElement; }

    public static void updateSelection(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        double cx = mc.getWindow().getGuiScaledWidth() / 2.0;
        double cy = mc.getWindow().getGuiScaledHeight() / 2.0;

        //taille de l'hud ici et dans render obligé
        int size = 64;
        int gap = 12;

        // Position exacte de chaque icône — identique à render()
        double[] fireCX   = {cx - size - gap + size / 2.0};
        double[] waterCX  = {cx + gap + size / 2.0};
        double[] earthCY  = {cy + gap + size / 2.0};
        double[] airCY    = {cy - size - gap + size / 2.0};

        double fireX  = cx - size - gap;
        double waterX = cx + gap;
        double earthY = cy + gap;
        double airY   = cy - size - gap;

        // On calcule la distance de la souris au centre de chaque icône
        double distFire  = dist(mouseX, mouseY, fireX  + size/2.0, cy - size/2.0 + size/2.0);
        double distWater = dist(mouseX, mouseY, waterX + size/2.0, cy - size/2.0 + size/2.0);
        double distEarth = dist(mouseX, mouseY, cx - size/2.0 + size/2.0, earthY + size/2.0);
        double distAir   = dist(mouseX, mouseY, cx - size/2.0 + size/2.0, airY   + size/2.0);

        // Zone morte — si trop proche du centre on ne change pas
        double dx = mouseX - cx;
        double dy = mouseY - cy;
        if (Math.sqrt(dx * dx + dy * dy) < 20) return;

        // On sélectionne l'icône la plus proche de la souris
        double minDist = Math.min(Math.min(distFire, distWater), Math.min(distEarth, distAir));

        if (minDist == distFire)  selectedElement = Element.FIRE;
        else if (minDist == distWater) selectedElement = Element.WATER;
        else if (minDist == distEarth) selectedElement = Element.EARTH;
        else                           selectedElement = Element.AIR;
    }

    // Helper — distance entre deux points
    private static double dist(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    //taille de l'hud ici et dans updateSelection obligé
    public static void render(GuiGraphics graphics) {
        if (!isOpen) return;
        Minecraft mc = Minecraft.getInstance();
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        int size = 64;
        int gap = 12;
        drawElement(graphics, FIRE_TEX,  Element.FIRE,  cx - size - gap, cy - size/2, size, size);
        drawElement(graphics, WATER_TEX, Element.WATER, cx + gap,        cy - size/2, size, size);
        drawElement(graphics, EARTH_TEX, Element.EARTH, cx - size/2,     cy + gap,    size, size);
        drawElement(graphics, AIR_TEX,   Element.AIR,   cx - size/2,     cy - size - gap, size, size);
    }

    private static void drawElement(GuiGraphics graphics, ResourceLocation tex,
                                    Element element, int x, int y, int w, int h) {
        boolean isSelected = (selectedElement == element);
        int alpha = isSelected ? 255 : 160;
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha / 255f);
        graphics.blit(tex, x, y, 0, 0, w, h, 64, 64);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if (isSelected) {
            graphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    element.name(),
                    x + w / 2,
                    y + h + 2,
                    0xFFFFFF
            );
        }
    }
}