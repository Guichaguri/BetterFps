package me.guichaguri.betterfps.vanilla;

import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsVanilla {

    public static void start() {
        System.out.println("STAAAAAART");
        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }

        BetterFpsHelper.init();

        BetterFpsHelper.MENU_KEY = new KeyBinding("Settings", Keyboard.KEY_F12, "BetterFps");
        registerKeyBinding(BetterFpsHelper.MENU_KEY);
    }

    public static void keyEvent() {
        System.out.println("KEY EVENTTTTTTTTT");
        if(BetterFpsHelper.MENU_KEY.isPressed()) {
            GuiBetterFpsConfig.openGui();
        }
    }

    private static void registerKeyBinding(KeyBinding key) {
        KeyBinding[] bindings = Minecraft.getMinecraft().gameSettings.keyBindings;
        KeyBinding[] newBindings = new KeyBinding[bindings.length + 1];
        int i;
        for(i = 0; i < bindings.length; i++) {
            newBindings[i] = bindings[i];
        }
        newBindings[i + 1] = key;
        Minecraft.getMinecraft().gameSettings.keyBindings = newBindings;
    }

}
