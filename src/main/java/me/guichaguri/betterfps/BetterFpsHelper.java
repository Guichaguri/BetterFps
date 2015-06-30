package me.guichaguri.betterfps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Properties;
import net.minecraft.util.MathHelper;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsHelper {

    public static final String MC_VERSION = "1.8";
    public static final String VERSION = "1.1.1";

    public static final String UPDATE_URL = "https://raw.githubusercontent.com/Guichaguri/BetterFps/1.8/lastest-version.properties";

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

    public static File LOC;
    public static File MCDIR = null;
    public static Properties CONFIG = null;
    private static File CONFIG_FILE = null;

    public static String ALGORITHM_NAME;
    public static String ALGORITHM_CLASS;

    public static boolean CHECK_UPDATES = true;

    public static void init() {
        try {
            // UNLOAD CACHED UNNECESSARY VALUES
            for(Field f : MathHelper.class.getDeclaredFields()) {
                String name = f.getName();
                if((name.equals("SIN_TABLE")) || (name.equals("a"))) { // field_76144_a
                    f.setAccessible(true);
                    f.set(null, null);
                }
            }
        } catch(Exception ex) {
            // An error ocurred while unloading vanilla sin table? Its not a big problem.
        }
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

        ALGORITHM_NAME = CONFIG.getProperty("algorithm", "rivens-half");
        ALGORITHM_CLASS = helpers.get(ALGORITHM_NAME);

        CHECK_UPDATES = parseBoolean(CONFIG.getProperty("update-checker"), true);

        CONFIG.setProperty("algorithm", ALGORITHM_NAME);
        CONFIG.setProperty("update-checker", CHECK_UPDATES + "");

        saveConfig();
    }

    private static boolean parseBoolean(String str, boolean def) {
        try {
            return Boolean.parseBoolean(str);
        } catch(Exception ex) {
            return def;
        }
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
