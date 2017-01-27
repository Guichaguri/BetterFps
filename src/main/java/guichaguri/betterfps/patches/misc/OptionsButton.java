package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.gui.GuiBetterFpsConfig;
import guichaguri.betterfps.transformers.patcher.annotations.Copy;
import guichaguri.betterfps.transformers.patcher.annotations.Copy.Mode;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Guilherme Chaguri
 */
public abstract class OptionsButton extends GuiScreen {

    @Copy(Mode.APPEND)
    @Override
    public void initGui() {
        // Adds the BetterFps button, trying to find a free spot for it

        int x_BF = this.width / 2 + 5;
        int y_BF = this.height / 6 + 24 - 8;

        int width_BF = 150;

        if(hasButtonInPosition(x_BF, y_BF, 0, 2)) {

            x_BF = this.width / 2 - 155;

            if(hasButtonInPosition(x_BF, y_BF, 0, 2)) {
                // 102 - Language Button
                GuiButton language = null;
                for(GuiButton b : buttonList) {
                    if(b.id == 102) {
                        language = b;
                        break;
                    }
                }
                if(language == null) {
                    x_BF = 0;
                    y_BF = 0;
                    width_BF = 100;
                } else {
                    buttonList.remove(language);
                    language = new GuiButtonLanguage(language.id, language.xPosition, language.yPosition);
                    buttonList.add(language);
                    x_BF = language.xPosition + language.getButtonWidth();
                    y_BF = language.yPosition;
                    width_BF -= language.getButtonWidth();
                }

            }

        }

        this.buttonList.add(new GuiButton(72109, x_BF, y_BF, width_BF, 20, "BetterFps Options"));
    }

    @Copy
    private boolean hasButtonInPosition(int x, int y, int xRadius, int yRadius) {
        for(GuiButton b : buttonList) {
            if(b.xPosition <= x + xRadius && b.yPosition <= y + yRadius &&
                    b.xPosition >= x - xRadius && b.yPosition >= y - yRadius) {
                return true;
            }
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
