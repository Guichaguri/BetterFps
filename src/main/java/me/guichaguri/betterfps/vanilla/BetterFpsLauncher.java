package me.guichaguri.betterfps.vanilla;

import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.client.main.Main;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsLauncher {

    public static void main(String[] args) {
        BetterFpsHelper.init();
        Main.main(args);
    }

}
