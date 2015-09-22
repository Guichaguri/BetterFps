package me.guichaguri.betterfps.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsConfig;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.gui.GuiCycleButton.GuiBooleanButton;
import me.guichaguri.betterfps.tweaker.BetterFpsTweaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.lwjgl.input.Mouse;

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
        BetterFpsConfig config = BetterFpsConfig.getConfig();
        buttons.add(new AlgorithmButton(2, "Algorithm", BetterFpsHelper.displayHelpers,
                config.algorithm, new String[] {
                        "The algorithm of sine & cosine methods",
                        "§cRequires restarting to take effect",
                        "", "§eShift-click me to test algorithms §7(This will take a few seconds)",
                        "", "§aMore information soon"}));
        buttons.add(new GuiBooleanButton(3, "Update Checker", config.updateChecker, new String[] {
                        "Whether will check for updates on startup",
                        "It's highly recommended enabling this option"}));
        buttons.add(new GuiBooleanButton(4, "Preallocate Memory", config.preallocateMemory, new String[] {
                        "Whether will preallocate 10MB on startup.",
                        "§cRequires restarting to take effect", "",
                        "Default in Vanilla: On",
                        "Default in BetterFps: Off",
                        "",
                        "Note: This allocation will only be cleaned once the memory is almost full"}));
        return buttons;
    }

    @Override
    public void initGui() {
        int x1 = width / 2 - 155;
        int x2 = width / 2 + 5;

        buttonList.clear();
        buttonList.add(new GuiButton(-1, x1, height - 27, 150, 20, I18n.format("gui.done")));
        buttonList.add(new GuiButton(-2, x2, height - 27, 150, 20, I18n.format("gui.cancel")));

        List<GuiButton> buttons = initButtons();

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
        if(mouseY < fontRendererObj.FONT_HEIGHT + 14) {
            if(Mouse.isButtonDown(1)) {
                drawCenteredString(fontRendererObj, "This is not a button", this.width / 2, 7, 0xC0C0C0);
            } else {
                drawCenteredString(fontRendererObj, "Hold right-click on a button for information", this.width / 2, 7, 0xC0C0C0);
            }
        } else {
            drawCenteredString(fontRendererObj, "BetterFps Options", this.width / 2, 7, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(Mouse.isButtonDown(1)) { // Right Click
            for(GuiButton button : (List<GuiButton>)buttonList) {
                if((button instanceof GuiCycleButton) && (button.isMouseOver())) {
                    int y = mouseY + 5;

                    String[] help = ((GuiCycleButton)button).getHelpText();
                    int fontHeight = fontRendererObj.FONT_HEIGHT, i = 0;
                    drawGradientRect(0, y, mc.displayWidth, y + (fontHeight * help.length) + 10, -1072689136, -804253680);
                    for(String h : help) {
                        if(!h.isEmpty()) fontRendererObj.drawString(h, 5, y + (i * fontHeight) + 5, 0xFFFFFF);
                        i++;
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button instanceof GuiCycleButton) {
            ((GuiCycleButton)button).actionPerformed();
        } else if(button.id == -1) {
            // Save
            boolean restart = false;
            BetterFpsConfig config = BetterFpsConfig.getConfig();

            GuiCycleButton algorithmButton = getCycleButton(2);
            String algorithm = algorithmButton.getSelectedValue();
            if(!algorithm.equals(config.algorithm)) restart = true;

            config.algorithm = algorithm;

            GuiCycleButton updateButton = getCycleButton(3);
            config.updateChecker = updateButton.getSelectedValue();

            GuiCycleButton preallocateButton = getCycleButton(4);
            boolean preallocate = preallocateButton.getSelectedValue();
            if(preallocate != config.preallocateMemory) restart = true;
            config.preallocateMemory = preallocate;

            BetterFpsHelper.saveConfig();

            mc.displayGuiScreen(restart ? new GuiRestartDialog(parent) : parent);
        } else if(button.id == -2) {
            mc.displayGuiScreen(parent);
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

    private static class AlgorithmButton extends GuiCycleButton {
        Process process = null;
        public <T> AlgorithmButton(int buttonId, String title, HashMap<T, String> values, T defaultValue, String[] helpLines) {
            super(buttonId, title, values, defaultValue, helpLines);
        }

        private String getJavaDir() {
            String separator = System.getProperty("file.separator");
            String path = System.getProperty("java.home") + separator + "bin" + separator;
            if((Util.getOSType() == EnumOS.WINDOWS) && (new File(path + "javaw.exe").isFile())) {
                return path + "javaw.exe";
            }
            return path + "java";
        }

        private boolean isRunning() {
            try {
                process.exitValue();
                return false;
            } catch(Exception ex) {
                return true;
            }
        }

        private void updateAlgorithm() {
            if((process != null) && (!isRunning())) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while((line = in.readLine()) != null) {
                        if(BetterFpsHelper.helpers.containsKey(line)) {
                            for(int i = 0; i < keys.size(); i++) {
                                if(keys.get(i).equals(line)) {
                                    key = i;
                                    updateTitle();
                                    break;
                                }
                            }
                        }
                    }
                } catch(Exception ex) {}
                process = null;
            }
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            updateAlgorithm();
            super.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public boolean shiftClick() {
            if((process != null) && (isRunning())) {
                return true;
            }
            List<String> args = new ArrayList<String>();
            args.add(getJavaDir());
            args.add("-Dtester=" + Minecraft.getMinecraft().mcDataDir.getAbsolutePath());
            args.add("-cp");
            args.add(BetterFpsTweaker.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            args.add("me.guichaguri.betterfps.installer.BetterFpsInstaller");
            try {
                process = new ProcessBuilder(args).start();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }
    }
}
