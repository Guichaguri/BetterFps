package me.guichaguri.betterfps;

import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Client event handling
 * @author Guilherme Chaguri
 */
public class BetterFpsClient {
    protected static Minecraft mc;
    private static KeyBinding MENU_KEY = new KeyBinding("BetterFps", Keyboard.KEY_F12, "key.categories.misc");

    // Called in Minecraft.startGame
    public static void start(Minecraft minecraft) {
        mc = minecraft;
        BetterFps.isClient = true;

        if(BetterFpsConfig.instance == null) {
            BetterFpsHelper.loadConfig();
        }
        BetterFpsHelper.init();

        // TODO unused. remove it
        //mc.gameSettings.keyBindings = ArrayUtils.add(mc.gameSettings.keyBindings, MENU_KEY);
        //mc.gameSettings.loadOptions();

        UpdateChecker.check();
    }

    // Called in WorldClient.<init>
    public static void worldLoad() {
        UpdateChecker.showChat();
    }

    // Called at the end of KeyBinding.onTick
    public static void keyEvent(int key) {//TODO unused. remove it
        if(MENU_KEY.getKeyCode() == key) {
            mc.displayGuiScreen(new GuiBetterFpsConfig(mc.currentScreen));
        }
    }

}
