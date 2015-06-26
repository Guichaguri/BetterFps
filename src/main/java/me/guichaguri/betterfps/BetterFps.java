package me.guichaguri.betterfps;

import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.DummyModContainer;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

/**
 * Vanilla event handling
 * @author Guilherme Chaguri
 */
public class BetterFps {
    protected static Minecraft mc;
    private static KeyBinding MENU_KEY = new KeyBinding("BetterFps", Keyboard.KEY_F12, "key.categories.misc");
    private static boolean updateNotification = false;

    // Called in Minecraft.startGame
    public static void start(Minecraft minecraft) {
        mc = minecraft;

        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }
        BetterFpsHelper.init();

        mc.gameSettings.keyBindings = ArrayUtils.add(mc.gameSettings.keyBindings, MENU_KEY);
        mc.gameSettings.loadOptions();

    }

    // Called at the end of KeyBinding.onTick
    public static void keyEvent(int key) {

        DummyModContainer

        if(MENU_KEY.getKeyCode() == key) {
            mc.displayGuiScreen(new GuiBetterFpsConfig(mc.currentScreen));
        }
    }

    // Called at the end of EntityPlayer.onUpdate
    public static void playerTick() {
        if(!updateNotification) {
            updateNotification = UpdateChecker.tick();
        }
    }

}
