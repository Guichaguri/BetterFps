package guichaguri.betterfps;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class file that should include Minecraft-dependent methods
 * @author Guilherme Chaguri
 */
public class BetterFpsHelper {

    public static final Logger LOG = LogManager.getLogger("BetterFps");

    private static BetterFpsConfig INSTANCE = null;
    private static File CONFIG_FILE = null;

    public static BetterFpsConfig getConfig() {
        if(INSTANCE == null) loadConfig();
        return INSTANCE;
    }

    public static void loadConfig() {
        File configPath;
        if(BetterFps.GAME_DIR == null) {
            configPath = new File("config");
        } else {
            configPath = new File(BetterFps.GAME_DIR, "config");
        }

        CONFIG_FILE = new File(configPath, "betterfps.json");

        // Temporary code - Import old config file to the new one
        File oldConfig;
        if(BetterFps.GAME_DIR == null) {
            oldConfig = new File("config" + File.pathSeparator + "betterfps.json");
        } else {
            oldConfig = new File(BetterFps.GAME_DIR, "config" + File.pathSeparator + "betterfps.json");
        }
        if(oldConfig.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(oldConfig);
                INSTANCE = new Gson().fromJson(reader, BetterFpsConfig.class);
                saveConfig();
                return;
            } catch(Exception ex) {
                LOG.error("Could not load the old config file. It will be deleted.");
            } finally {
                IOUtils.closeQuietly(reader);
                oldConfig.deleteOnExit();
            }
        }
        // -------

        FileReader reader = null;
        try {
            if(CONFIG_FILE.exists()) {
                reader = new FileReader(CONFIG_FILE);
                INSTANCE = new Gson().fromJson(reader, BetterFpsConfig.class);
            }
        } catch(Exception ex) {
            LOG.error("Could not load the config file", ex);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        if(INSTANCE == null) INSTANCE = new BetterFpsConfig();

        saveConfig();
    }

    public static void saveConfig() {
        FileWriter writer = null;
        try {
            if(!CONFIG_FILE.exists()) CONFIG_FILE.getParentFile().mkdirs();
            writer = new FileWriter(CONFIG_FILE);
            new Gson().toJson(INSTANCE, writer);
        } catch(Exception ex) {
            LOG.error("Could not save the config file", ex);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
