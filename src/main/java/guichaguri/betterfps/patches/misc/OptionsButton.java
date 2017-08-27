package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.gui.GuiBetterFpsConfig;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * @author Guilherme Chaguri
 */
public abstract class OptionsButton extends GuiScreen {

    @Copy(Mode.REPLACE)
    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);

        // Adds the BetterFps button, trying to find a free spot for it
        // Called after setWorldAndResolution instead of initGui
        // Other mods should have already added their buttons at this point

        int x_BF = this.width / 2 + 5;
        int y_BF = this.height / 6 + 24 - 8;

        int width_BF = 150;

        if(hasButtonInPosition(x_BF, y_BF, width_BF)) {
            // There is a another button on the right side, we will change to the left side
            x_BF = this.width / 2 - 155;

            if(hasButtonInPosition(x_BF, y_BF, width_BF)) {
                // There is also a button on the left side, we will find another spot
                // 102 - Language Button
                GuiButton language = null;
                for(GuiButton b : buttonList) {
                    if(b.id == 102) {
                        language = b;
                        break;
                    }
                }

                if(language == null) {
                    // Did not find the language button. A mod must have removed it, we'll add the button in the corner
                    x_BF = 0;
                    y_BF = 0;
                    width_BF = 100;
                } else {
                    // Replace the language button to the icon version, using the rest of the space for our button
                    buttonList.remove(language);
                    language = new GuiButtonLanguage(language.id, language.x, language.y);
                    buttonList.add(language);
                    x_BF = language.x + language.getButtonWidth();
                    y_BF = language.y;
                    width_BF -= language.getButtonWidth();
                }

            }

        }

        buttonList.add(new GuiButton(72109, x_BF, y_BF, width_BF, 20, I18n.format("betterfps.options.title")));
    }

    @Copy
    private boolean hasButtonInPosition(int x, int y, int width) {
        int x2 = x + width;
        int y2 = y + 20;

        for(GuiButton b : buttonList) {
            int bX = b.x;
            int bY = b.y;
            if(x2 > bX && y2 > bY && x < bX + b.getButtonWidth() && y < bY + 20) return true;
        }
        return false;
    }

    @Copy(Mode.APPEND)
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 72109) {
            mc.gameSettings.saveOptions();
            mc.displayGuiScreen(new GuiBetterFpsConfig(this));
        }
    }

}
