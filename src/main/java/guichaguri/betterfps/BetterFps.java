package guichaguri.betterfps;

/**
 * Common event handling
 * @author Guilherme Chaguri
 */
public class BetterFps {

    public static boolean isClient = false;

    // Used to control how many TNTs can update per tick
    public static int TNT_TICKS = 0;
    public static int MAX_TNT_TICKS = 100;

    public static int TICKABLE_RADIUS_POS = 1;
    public static int TICKABLE_RADIUS_NEG = -1;

    public static void serverStart() {
        UpdateChecker.check();
    }

    public static void worldTick() {
        TNT_TICKS = 0;
    }

    public static boolean isTickable(int dX, int dY) {
        // Checks if a chunk should tick
        return dX == 0 && dY == 0;
        //return dX < TICKABLE_RADIUS_POS && dX > TICKABLE_RADIUS_NEG && dY < TICKABLE_RADIUS_POS && dY > TICKABLE_RADIUS_NEG;
    }

}
