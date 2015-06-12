package me.guichaguri.betterfps.installer;

import java.io.File;

/**
 * @author Guilherme Chaguri
 */
public class InstanceInstaller {

    public static void install(File folder) {

    }

    public static File getSuggestedMinecraftFolder() {
        // Adapted from Forge installer
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase();
        if((osType.contains("win")) && (System.getenv("APPDATA") != null)) {
            return new File(System.getenv("APPDATA"), ".minecraft");
        } else if(osType.contains("mac")) {
            return new File(userHomeDir, "Library/Application Support/minecraft");
        } else {
            return new File(userHomeDir, ".minecraft");
        }
    }

    public InstanceInstaller() {

    }

}
