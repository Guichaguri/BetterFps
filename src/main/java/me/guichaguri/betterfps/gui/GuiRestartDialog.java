package me.guichaguri.betterfps.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * @author Guilherme Chaguri
 */
public class GuiRestartDialog extends GuiScreen {
    private GuiScreen parent = null;

    private final String[] message = new String[]{
            "You need to restart your game to apply some changes",
            "Do you want restart now?"
    };

    public GuiRestartDialog(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(1, this.width / 2 - 205, this.height - 27, I18n.format("gui.yes")));
        buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 27, I18n.format("gui.no")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int centerX = width / 2;
        int centerY = height / 2;
        int i = 0;
        for(String msg : message) {
            drawCenteredString(fontRendererObj, msg, centerX, centerY -
                    ((message.length - i) * fontRendererObj.FONT_HEIGHT), 0xFFFFFF);
            i++;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button.id == 1) {
            mc.shutdown();
        } else if(button.id == 2) {
            mc.displayGuiScreen(parent);
        }
    }
}
