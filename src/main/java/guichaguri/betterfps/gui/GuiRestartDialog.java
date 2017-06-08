package guichaguri.betterfps.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * @author Guilherme Chaguri
 */
public class GuiRestartDialog extends GuiScreen {
    private final GuiScreen parent;

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
        int dialogTextAmount = 2;

        for(int i = 0; i < dialogTextAmount; i++) {
            String msg = I18n.format("betterfps.options.restart.dialog." + (i + 1));
            drawCenteredString(fontRenderer, msg, centerX, centerY - ((dialogTextAmount - i) * fontRenderer.FONT_HEIGHT), 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id) {
            case 1:
                mc.shutdown();
                break;
            case 2:
                mc.displayGuiScreen(parent);
                break;
        }
    }
}
