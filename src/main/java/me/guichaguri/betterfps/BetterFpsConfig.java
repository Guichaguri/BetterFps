package me.guichaguri.betterfps;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsConfig {
    protected static BetterFpsConfig instance = null;
    public static BetterFpsConfig getConfig() {
        return instance;
    }

    public String algorithm = "rivens-half";

    public boolean updateChecker = true;

    public boolean preallocateMemory = false;

}
