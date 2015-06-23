package me.guichaguri.betterfps.gui;

import java.io.IOException;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsHelper;
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


    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));

        int id = 2;
        int x1 = width / 2 - 155;
        int x2 = width / 2 + 5;
        int y = 25;

        buttonList.add(new GuiCycleButton(id, x1, y, 310, 20, "Algorithm",
                BetterFpsHelper.displayHelpers, BetterFpsHelper.ALGORITHM_NAME));

        /*for(String option : options) {
            boolean first = id % 2 == 0;
            buttonList.add(new GuiCycleButton(id, first ? x1 : x2, y, 150, 20, option));
            if(!first) height += 25;
            id++;
        }*/
        // TODO: Finish this

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
