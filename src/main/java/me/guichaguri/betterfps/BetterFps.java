package me.guichaguri.betterfps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Common event handling
 * @author Guilherme Chaguri
 */
public class BetterFps {
    public static final Logger log = LogManager.getLogger("BetterFps");

    public static boolean isClient = false;

    // Used to control how many TNTs can update per tick
    public static int TNT_TICKS = 0;
    public static int MAX_TNT_TICKS = 100;

    public static void serverStart() {
        System.out.println("SERVER START");
        UpdateChecker.check();
    }

    public static void worldTick() {
        TNT_TICKS = 0;
    }
}
