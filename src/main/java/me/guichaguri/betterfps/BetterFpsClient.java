package me.guichaguri.betterfps;

import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
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

        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }
        BetterFpsHelper.init();

        mc.gameSettings.keyBindings = ArrayUtils.add(mc.gameSettings.keyBindings, MENU_KEY);
        mc.gameSettings.loadOptions();

        UpdateChecker.check();
    }

    // Called in WorldClient.<init>
    public static void worldLoad() {
        System.out.println("WORLD LOAD");
        UpdateChecker.showChat();
    }

    // Called at the end of KeyBinding.onTick
    public static void keyEvent(int key) {
        if(MENU_KEY.getKeyCode() == key) {
            mc.displayGuiScreen(new GuiBetterFpsConfig(mc.currentScreen));
        }
    }

}
