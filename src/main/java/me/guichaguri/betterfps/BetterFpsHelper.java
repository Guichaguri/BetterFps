package me.guichaguri.betterfps;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsHelper {

    public static final String MC_VERSION = "1.8.9";
    public static final String VERSION = "1.2.1";

    public static final String URL = "http://guichaguri.github.io/BetterFps/";

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
    private static File CONFIG_FILE = null;

    public static void init() {

    }

    public static BetterFpsConfig loadConfig() {

        if(MCDIR == null) {
            CONFIG_FILE = new File("config" + File.pathSeparator + "betterfps.json");
        } else {
            CONFIG_FILE = new File(MCDIR, "config" + File.pathSeparator + "betterfps.json");
        }

        try {
            if(CONFIG_FILE.exists()) {
                Gson gson = new Gson();
                BetterFpsConfig.instance = gson.fromJson(new FileReader(CONFIG_FILE), BetterFpsConfig.class);
            } else {
                BetterFpsConfig.instance = new BetterFpsConfig();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        // Temporary code - Import config from the old format to the new one
        try {
            Properties prop = new Properties();
            File oldConfigFile;
            if(MCDIR == null) {
                oldConfigFile = new File("betterfps.txt");
            } else {
                oldConfigFile = new File(MCDIR, "betterfps.txt");
            }
            if((oldConfigFile.exists()) && (!CONFIG_FILE.exists())) {
                prop.load(new FileInputStream(oldConfigFile));
                BetterFpsConfig.instance.algorithm = prop.getProperty("algorithm", "rivens-half");
            }
        } catch(Exception ex) {
            System.err.println("Could not import the old config format");
        }
        // ---

        saveConfig();

        return BetterFpsConfig.instance;
    }

    public static void saveConfig() {
        try {
            if(!CONFIG_FILE.exists()) CONFIG_FILE.createNewFile();
            Gson gson = new Gson();
            FileUtils.writeStringToFile(CONFIG_FILE, gson.toJson(BetterFpsConfig.instance));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
