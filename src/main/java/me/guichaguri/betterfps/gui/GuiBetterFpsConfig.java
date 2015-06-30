package me.guichaguri.betterfps.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.gui.GuiCycleButton.GuiBooleanButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * @author Guilherme Chaguri
 */
public class GuiBetterFpsConfig extends GuiScreen {

    private GuiScreen parent = null;
    public GuiBetterFpsConfig() {

    }
    public GuiBetterFpsConfig(GuiScreen parent) {
        this.parent = parent;
    }


    private List<GuiButton> initButtons() {
        List<GuiButton> buttons = new ArrayList<GuiButton>();
        buttons.add(new GuiCycleButton(2, "Algorithm",
                BetterFpsHelper.displayHelpers, BetterFpsHelper.ALGORITHM_NAME));
        buttons.add(new GuiBooleanButton(4, "Update Checker", true));
        return buttons;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));

        List<GuiButton> buttons = initButtons();

        int x1 = width / 2 - 155;
        int x2 = width / 2 + 5;
        int y = 25;
        int lastId = 0;

        for(GuiButton button : buttons) {
            boolean first = button.id % 2 != 0;
            boolean large = button.id - 1 != lastId;
            button.xPosition = (first || large) ? x1 : x2;
            button.yPosition = y;
            button.width = large ? 310 : 150;
            button.height = 20;
            buttonList.add(button);
            if((!first) || (large)) y += 25;
            lastId = button.id;
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "BetterFps Options", this.width / 2, 7, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button instanceof GuiCycleButton) {
            ((GuiCycleButton)button).actionPerformed();
        } else if(button.id == -1) {
            // Save
            boolean restart = false;

            GuiCycleButton algorithmButton = getCycleButton(2);
            String algorithm = algorithmButton.getSelectedValue();
            if(!algorithm.equals(BetterFpsHelper.ALGORITHM_NAME)) restart = true;
            BetterFpsHelper.CONFIG.setProperty("algorithm", algorithm);

            GuiBooleanButton updateButton = (GuiBooleanButton)getCycleButton(4);
            BetterFpsHelper.CONFIG.setProperty("update-checker", updateButton.getSelectedValue() + "");

            BetterFpsHelper.saveConfig();
            BetterFpsHelper.loadConfig();

            mc.displayGuiScreen(restart ? new GuiRestartDialog(parent) : parent);
        }
    }

    private GuiCycleButton getCycleButton(int id) {
        for(GuiButton button : (List<GuiButton>)buttonList) {
            if(button.id == id) {
                return (GuiCycleButton)button;
            }
        }
        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
