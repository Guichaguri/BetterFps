package me.guichaguri.betterfps.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Guilherme Chaguri
 */
public class GuiBetterFpsConfig extends GuiScreen {

    public static void openGui() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiBetterFpsConfig());
    }

    private GuiButton algorithmType = null;


    public GuiBetterFpsConfig() {

    }
    public GuiBetterFpsConfig(GuiScreen parent) {

    }


    @Override
    public void initGui() {
        algorithmType = new GuiButton(0, 0, 0, "Algorithm");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int wHalf = width / 2;
        int hHalf = height / 2;
        drawCenteredString(fontRendererObj, "BetterFps Config", wHalf, hHalf - 50, 0xFFFFFF);
        algorithmType.xPosition = wHalf;
        algorithmType.yPosition = hHalf;
        algorithmType.drawButton(mc, mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        // SAVE
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
