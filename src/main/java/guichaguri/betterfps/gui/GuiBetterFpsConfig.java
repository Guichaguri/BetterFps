package guichaguri.betterfps.gui;

import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.gui.data.OptionManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Mouse;

/**
 * @author Guilherme Chaguri
 */
public class GuiBetterFpsConfig extends GuiScreen {

    private final GuiScreen parent;
    private String title, titleMouseOver, titleRightClick;

    private final List<GuiConfigOption> options = new ArrayList<GuiConfigOption>();
    private float scrollY = 0, lastScrollY = 0;
    private int pageHeight = 0;

    public GuiBetterFpsConfig() {
        this.parent = null;
    }

    public GuiBetterFpsConfig(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("betterfps.options.title");
        this.titleMouseOver = I18n.format("betterfps.options.title.mouseover");
        this.titleRightClick = I18n.format("betterfps.options.title.rightclick");

        int middleX = width / 2;
        int xLeft = middleX - 155;
        int xRight = middleX + 5;
        int y = height - 27;

        buttonList.add(new GuiButton(-1, xLeft, y, 150, 20, I18n.format("gui.done")));
        buttonList.add(new GuiButton(-2, xRight, y, 150, 20, I18n.format("gui.cancel")));

        options.clear();
        OptionManager.addButtons(this.options);

        y = 5;
        boolean left = true;

        for(GuiConfigOption button : options) {

            if(button.isWide()) {
                y += 25;
                button.x = xLeft;
                button.setWidth(310);
            } else {
                if(left) y += 25;
                button.x = left ? xLeft : xRight;
                button.setWidth(150);
                left = !left;
            }

            button.y = y;
        }

        pageHeight = y + 60;
    }

    private void updateScroll(float partialTicks) {
        scrollY += Mouse.getDWheel() / 6F;
        if(Mouse.isButtonDown(0)) {
            // Math.max prevents division by 0 which makes the scrollY and lastScrollY to be set to NaN, making the UI invisible
            scrollY -= (float)Mouse.getDY() / Math.max(mc.gameSettings.guiScale, 1);
            lastScrollY = scrollY;
        }

        int pageHeightDelta = height - pageHeight;
        if(scrollY > 0) {
            scrollY = 0;
            if(lastScrollY > 0) lastScrollY = 0;
        } else if(scrollY < pageHeightDelta) {
            int scroll = pageHeightDelta > 0 ? 0 : pageHeightDelta;
            scrollY = scroll;
            if(lastScrollY < pageHeightDelta) lastScrollY = scroll;
        }

        lastScrollY = lastScrollY + (scrollY - lastScrollY) * partialTicks;
    }

    private int getScrollMouseY(int mouseY) {
        if(mouseY < 30 || height - mouseY < 30) {
            return Integer.MIN_VALUE;
        }
        return mouseY - (int)lastScrollY;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        updateScroll(partialTicks);

        int mouseYScroll = getScrollMouseY(mouseY);

        GlStateManager.translate(0, lastScrollY, 0);
        for(int i = 0; i < options.size(); ++i) {
            options.get(i).drawButton(mc, mouseX, mouseYScroll, partialTicks);
        }
        GlStateManager.translate(0, -lastScrollY, 0);

        drawGradientRect(0, 0, width, 30, 0xC8101010, 0x00000000);
        drawGradientRect(0, height - 30, width, height, 0x00000000, 0xC8101010);

        super.drawScreen(mouseX, mouseY, partialTicks);

        int x = width / 2;
        int y = (30 - fontRenderer.FONT_HEIGHT) / 2;
        if(mouseY < 30) {
            if(Mouse.isButtonDown(1)) {
                drawCenteredString(fontRenderer, titleRightClick, x, y, 0xFF0000);
            } else {
                drawCenteredString(fontRenderer, titleMouseOver, x, y, 0xC0C0C0);
            }
        } else {
            drawCenteredString(fontRenderer, title, x, y, 0xFFFFFF);
        }

        if(Mouse.isButtonDown(1)) {
            for(int i = 0; i < options.size(); ++i) {
                GuiConfigOption button = options.get(i);
                if(!button.isMouseOver()) continue;

                String description = button.getDescription();
                if(description == null) continue;

                List<String> lines = fontRenderer.listFormattedStringToWidth(description, width - 10);

                int tooltipY = mouseY + 10;
                int tooltipHeight = (lines.size() * fontRenderer.FONT_HEIGHT) + 10;
                int tooltipBottom = tooltipY + tooltipHeight;

                if(tooltipBottom > height) {
                    tooltipY = height - tooltipHeight;
                    tooltipBottom = height;
                }

                drawRect(0, tooltipY, width, tooltipBottom, 0xC8101010);

                tooltipY += 5;
                int line = 0;
                for(String l : lines) {
                    fontRenderer.drawString(l, 5, tooltipY + (line * fontRenderer.FONT_HEIGHT), 0xFFFFFF, true);
                    line++;
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton == 0) {
            int mouseYScroll = getScrollMouseY(mouseY);

            for(int i = 0; i < options.size(); ++i) {
                GuiConfigOption button = options.get(i);

                if(button.mousePressed(mc, mouseX, mouseYScroll)) {
                    button.playPressSound(mc.getSoundHandler());
                    button.actionPerformed();
                    actionPerformed(button);
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id) {
            case -1:
                // Done
                boolean restart = OptionManager.store(options);

                BetterFpsHelper.saveConfig();

                mc.displayGuiScreen(restart ? new GuiRestartDialog(parent) : parent);
                break;
            case -2:
                // Cancel
                mc.displayGuiScreen(parent);
                break;
        }
    }
}
