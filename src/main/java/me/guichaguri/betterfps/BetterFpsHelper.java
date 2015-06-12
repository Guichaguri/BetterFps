package me.guichaguri.betterfps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsHelper {

    public static final String MODID = "betterfps";
    public static final String VERSION = "1.1.1";
    public static boolean FORGE = false;

    public static final String[] TRANSFORMERS = new String[]{
            "me.guichaguri.betterfps.MathTransformer"
    };

    // Config Name, Class Name
    public static final LinkedHashMap<String, String> helpers = new LinkedHashMap<String, String>(){{
        helpers.put("vanilla", "VanillaMath");
        helpers.put("rivens", "RivensMath");
        helpers.put("taylors", "TaylorMath");
        helpers.put("libgdx", "LibGDXMath");
        helpers.put("rivens-full", "RivensFullMath");
        helpers.put("rivens-half", "RivensHalfMath");
        helpers.put("java", "JavaMath");
        helpers.put("random", "RandomMath");
    }};

    // Config Name, Display Name
    public static final LinkedHashMap<String, String> displayHelpers = new LinkedHashMap<String, String>(){{
        displayHelpers.put("vanilla", "Vanilla Algorithm");
        displayHelpers.put("rivens", "Riven's Algorithm");
        displayHelpers.put("taylors", "Taylor's Algorithm");
        displayHelpers.put("libgdx", "LibGDX's Algorithm");
        displayHelpers.put("rivens-full", "Riven's \"Full\" Algorithm");
        displayHelpers.put("rivens-half", "Riven's \"Half\" Algorithm");
        displayHelpers.put("java", "Java Math");
        displayHelpers.put("random", "Random Math");
    }};

    public static File LOC;
    public static File MCDIR = null;
    public static Properties CONFIG = null;
    private static File CONFIG_FILE = null;

    public static String ALGORITHM_NAME;
    public static String ALGORITHM_CLASS;

    public static void init() {
        MathTransformer.unloadUselessValues();
    }

    public static void loadConfig() {
        if(MCDIR == null) {
            CONFIG_FILE = new File("betterfps.txt");
        } else {
            CONFIG_FILE = new File(MCDIR, "betterfps.txt");
        }

        CONFIG = new Properties();
        try {
            if(CONFIG_FILE.exists()) {
                CONFIG.load(new FileInputStream(CONFIG_FILE));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        ALGORITHM_NAME = CONFIG.getProperty("algorithm", "rivens-full");
        ALGORITHM_CLASS = helpers.get(ALGORITHM_NAME);
        CONFIG.setProperty("algorithm", ALGORITHM_NAME);

        saveConfig();
    }

    public static void saveConfig() {
        try {
            if(!CONFIG_FILE.exists()) CONFIG_FILE.createNewFile();
            CONFIG.store(new FileOutputStream(CONFIG_FILE), "BetterFps Config");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
