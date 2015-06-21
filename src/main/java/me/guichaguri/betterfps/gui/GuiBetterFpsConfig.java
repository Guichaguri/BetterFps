package me.guichaguri.betterfps.gui;

import java.io.IOException;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * @author Guilherme Chaguri
 */
public class GuiBetterFpsConfig extends GuiScreen {

    public static void openGui() {
        GuiBetterFpsConfig gui = new GuiBetterFpsConfig();
        System.out.println(gui.getClass().getClassLoader());
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    private String screenTitle = "BetterFps Options";


    public GuiBetterFpsConfig() {

    }
    public GuiBetterFpsConfig(GuiScreen parent) {
    }


    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));

        int id = 2;
        int width1 = width / 2 - 155;
        int width2 = width / 2 + 5;
        int height = 25;

        buttonList.add(new GuiCycleButton(id, width1, height, 310, 20,
                        "Algorithm", BetterFpsHelper.displayHelpers, BetterFpsHelper.ALGORITHM_NAME));

        for(String option : new String[]{"Teste", "Teste2", "Teste3", "Teste4"}) {
            boolean first = id % 2 == 0;
            //buttonList.add(new GuiCycleButton(id, first ? width1 : width2, height, 150, 20, option));
            if(!first) height += 25;
            id++;
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, screenTitle, this.width / 2, 5, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button instanceof GuiCycleButton) {
            ((GuiCycleButton)button).actionPerformed();
        }
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
