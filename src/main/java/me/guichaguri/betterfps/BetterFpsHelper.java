package me.guichaguri.betterfps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Properties;
import net.minecraft.util.MathHelper;

/**
 * @author Guilherme Chaguri
 */
public abstract class BetterFpsHelper {

    public static final String MODID = "betterfps";
    public static final String VERSION = "1.1.0";

    public static final String[] TRANSFORMERS = new String[]{
            "me.guichaguri.betterfps.MathTransformer"
    };

    public static File LOC = null;

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

    public static File MCDIR = null;
    public static Properties CONFIG = null;
    private static File CONFIG_FILE = null;
    public static String ALGORITHM_NAME;
    public static String ALGORITHM_CLASS;

    public static void init() {
        if(!BetterFpsHelper.ALGORITHM_NAME.equals("vanilla")) {
            try {
                Method m = MathHelper.class.getMethod("bfInit");
                m.setAccessible(true);
                m.invoke(null);
            } catch(Exception ex) {
                // Maybe bfInit does not exist? Can be possible if the algorithm does not have a static block
            }
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
