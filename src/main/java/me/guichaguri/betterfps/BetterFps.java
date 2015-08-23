package me.guichaguri.betterfps;

/**
 * Common event handling
 * @author Guilherme Chaguri
 */
public class BetterFps {
    public static boolean isClient = false;

    private static boolean updateNotification = false;

    public static int TNT_TICKS = 0; // Used to control how many explosions can happen in a tick
    public static int MAX_TNT_TICKS = 100;

    public static void worldTick() {
        TNT_TICKS = 0;
        if(!updateNotification) {
            updateNotification = UpdateChecker.tick();
        }
    }
}
