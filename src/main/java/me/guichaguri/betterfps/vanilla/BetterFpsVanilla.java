package me.guichaguri.betterfps.vanilla;

import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsVanilla {

    private static Minecraft MC;
    private static KeyBinding MENU_KEY;

    public static void start() {
        System.out.println("STAAAAAART");
        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }

        BetterFpsHelper.init();

        MENU_KEY = new KeyBinding("BetterFps", Keyboard.KEY_F12, "key.categories.misc");
        registerKeyBinding(MENU_KEY);
    }

    public static void keyEvent() {
        System.out.println("KEY EVENTTTTTTTTT");
        if(MENU_KEY.isPressed()) {
            GuiBetterFpsConfig.openGui();
        }
    }

    private static void registerKeyBinding(KeyBinding key) {
        MC.gameSettings.keyBindings = ArrayUtils.add(MC.gameSettings.keyBindings, key);
    }

    public static void setMc(Minecraft minecraft) {
        System.out.println("SET MCCCCCCCCCCCC");
        MC = minecraft;
    }

}
