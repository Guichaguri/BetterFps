package me.guichaguri.betterfps;

import java.lang.reflect.Field;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsConfig {
    protected static BetterFpsConfig instance = null;
    public static BetterFpsConfig getConfig() {
        return instance;
    }

    public static Object getValue(String key) {
        if(instance == null) return null;
        try {
            Field f = BetterFpsConfig.class.getDeclaredField(key);
            return f.get(instance);
        } catch(Exception ex) {
            return null;
        }
    }

    public String algorithm = "rivens-half";

    public boolean updateChecker = true;

    public boolean preallocateMemory = false;

    public boolean fastBoxRender = true;

    public boolean fog = true;

}
