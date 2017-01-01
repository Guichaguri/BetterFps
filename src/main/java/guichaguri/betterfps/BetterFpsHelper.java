package guichaguri.betterfps;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsHelper {

    public static final String MC_VERSION = "1.11";
    public static final String VERSION = "1.3.4";

    public static final String URL = "http://guichaguri.github.io/BetterFps/";

    public static final String UPDATE_URL = "http://widget.mcf.li/mc-mods/minecraft/229876-betterfps.json";

    public static final Logger LOG = LogManager.getLogger("BetterFps");

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
        // Temporary code - Import old config file to the new one
        File oldConfig;
        if(MCDIR == null) {
            oldConfig = new File("config" + File.pathSeparator + "betterfps.json");
        } else {
            oldConfig = new File(MCDIR, "config" + File.pathSeparator + "betterfps.json");
        }
        if(oldConfig.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(oldConfig);
                BetterFpsConfig.instance = new Gson().fromJson(reader, BetterFpsConfig.class);
                saveConfig();

                return BetterFpsConfig.instance;
            } catch(Exception ex) {
                LOG.error("Could not load the old config file. It will be deleted.");
            } finally {
                IOUtils.closeQuietly(reader);
                oldConfig.deleteOnExit();
            }
        }
        // -------


        File configPath;
        if(MCDIR == null) {
            configPath = new File("config");
        } else {
            configPath = new File(MCDIR, "config");
        }

        CONFIG_FILE = new File(configPath, "betterfps.json");

        FileReader reader = null;
        try {
            if(CONFIG_FILE.exists()) {
                reader = new FileReader(CONFIG_FILE);
                BetterFpsConfig.instance = new Gson().fromJson(reader, BetterFpsConfig.class);
            } else {
                BetterFpsConfig.instance = new BetterFpsConfig();
            }
        } catch(Exception ex) {
            LOG.error("Could not load the config file", ex);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        saveConfig();

        return BetterFpsConfig.instance;
    }

    public static void saveConfig() {
        try {
            if(!CONFIG_FILE.exists()) CONFIG_FILE.createNewFile();
            FileUtils.writeStringToFile(CONFIG_FILE, new Gson().toJson(BetterFpsConfig.instance));
        } catch(Exception ex) {
            LOG.error("Could not save the config file", ex);
        }
    }

}
