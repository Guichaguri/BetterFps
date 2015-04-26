package me.guichaguri.betterfps;

import java.io.File;
import java.util.LinkedHashMap;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Guilherme Chaguri
 */
public abstract class BetterMathHelper {

    // Config Name, Class Name
    public static final LinkedHashMap<String, String> helpers = new LinkedHashMap<String, String>();

    // Config Name, Display Name
    public static final LinkedHashMap<String, String> displayHelpers = new LinkedHashMap<String, String>();

    static {
        helpers.put("vanilla", "VanillaMath");
        helpers.put("rivens", "RivensMath");
        helpers.put("taylors", "TaylorMath");
        helpers.put("libgdx", "LibGDXMath");
        helpers.put("rivens-full", "RivensFullMath");
        helpers.put("rivens-half", "RivensHalfMath");
        helpers.put("java", "JavaMath");
        helpers.put("random", "RandomMath");

        displayHelpers.put("vanilla", "Vanilla Algorithm");
        displayHelpers.put("rivens", "Riven's Algorithm");
        displayHelpers.put("taylors", "Taylor's Algorithm");
        displayHelpers.put("libgdx", "LibGDX's Algorithm");
        displayHelpers.put("rivens-full", "Riven's \"Full\" Algorithm");
        displayHelpers.put("rivens-half", "Riven's \"Half\" Algorithm");
        displayHelpers.put("java", "Java Math");
        displayHelpers.put("random", "Random Math");
    }

    public static Configuration CONFIG;
    public static Property CONFIG_ALGORITHM;
    public static String ALGORITHM_NAME;
    public static String ALGORITHM_CLASS;

    public static void loadConfig() {
        File configFile = new File("config", "betterfps.cfg");
        CONFIG = new Configuration(configFile);
        CONFIG_ALGORITHM = CONFIG.get("betterfps", "algorithm", "rivens-full");
        CONFIG_ALGORITHM.setRequiresMcRestart(true);
        String v = "";
        for(String s : helpers.keySet()) v += ", " + s;
        CONFIG_ALGORITHM.comment = "The algorithm to be used.\nValues: " + v.substring(2);
        String[] validValues = new String[BetterMathHelper.displayHelpers.size()];
        int i = 0;
        for(String s : BetterMathHelper.displayHelpers.values()) {
            validValues[i] = s; i++;
        }
        CONFIG_ALGORITHM.setValidValues(validValues);
        ALGORITHM_NAME = CONFIG_ALGORITHM.getString();
        ALGORITHM_CLASS = helpers.get(ALGORITHM_NAME);
        CONFIG.save();
    }
}
