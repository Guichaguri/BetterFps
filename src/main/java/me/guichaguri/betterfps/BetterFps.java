package me.guichaguri.betterfps;

import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Vanilla event handling
 * @author Guilherme Chaguri
 */
public class BetterFps {
    private static Minecraft mc;
    public static KeyBinding MENU_KEY = new KeyBinding("BetterFps", Keyboard.KEY_F12, "key.categories.misc");

    // Called in Minecraft.startGame
    public static void start(Minecraft minecraft) {
        mc = minecraft;

        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }
        BetterFpsHelper.init();
    }


    // Called at the end of KeyBinding.onTick
    public static void keyEvent(int key) {
        if(MENU_KEY.getKeyCode() == key) {
            mc.displayGuiScreen(new GuiBetterFpsConfig(mc.currentScreen));
        }
    }

}
