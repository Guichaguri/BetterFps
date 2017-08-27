package guichaguri.betterfps.installer;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import guichaguri.betterfps.BetterFps;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsInstaller {

    private static final String TWEAK_CLASS = "guichaguri.betterfps.tweaker.BetterFpsTweaker";
    private static final String LIBRARY_IDENTIFIER = "betterfps";
    private static final String LIBRARY_NAME = "BetterFps";
    private static final String VERSION_NAME = BetterFps.VERSION;
    private static final String[] LIBRARIES = new String[] {
            "org.ow2.asm:asm-all:5.0.3",
            "net.minecraft:launchwrapper:1.11"
    };

    private static final Properties i18n = new Properties();

    protected static File GAME_DIR = null;
    protected static String ALGORITHM = null;

    public static void main(String[] args) {
        String lang = String.format("%s_%s", System.getProperty("user.language"), System.getProperty("user.country"));
        loadLanguage(lang);

        info("Waiting for Swing to load...");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadUI();
            }
        });
    }

    private static void loadUI() {
        info("Initializing Look and Feel...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            info("OS Look and Feel couldn't be loaded. Using the default instead.");
        }

        info("Initializing the UI...");
        GuiInstaller installer = new GuiInstaller();
        installer.setVisible(true);

        info("Checking for updates...");
        try {
            checkUpdates(installer);
        } catch(Exception ex) {
            info("Couldn't check for updates");
        }
    }

    private static void loadLanguage(String lang) {
        final String langPattern = "assets/betterfps/lang/%s.lang";
        lang = lang.toLowerCase();

        try {
            Charset utf8 = Charset.forName("UTF-8");

            info("Loading en_us...");
            InputStream in = getResource(String.format(langPattern, "en_us"));
            if(in != null) i18n.load(new InputStreamReader(in, utf8));

            if(!lang.equals("en_us")) {
                info("Loading %s...", lang);
                in = getResource(String.format(langPattern, lang));
                if(in != null) i18n.load(new InputStreamReader(in, utf8));
            }
        } catch(IOException ex) {
            error("Couldn't load the i18n data (%s)", lang);
            ex.printStackTrace();
        }
    }

    public static void info(String log, Object ... data) {
        System.out.println(String.format(log, data));
    }

    public static void error(String log, Object ... data) {
        System.err.println(String.format(log, data));
    }

    public static InputStream getResource(String path) {
        return BetterFpsInstaller.class.getClassLoader().getResourceAsStream(path);
    }

    public static String i18n(String id, Object ... data) {
        String msg = i18n.getProperty(id, id);
        if(data.length > 0) {
            return String.format(msg, data);
        } else {
            return msg;
        }
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

    public static void checkUpdates(GuiInstaller installer) throws IOException, URISyntaxException {
        URL url = new URL(BetterFps.UPDATE_URL);
        InputStream in = url.openStream();
        JsonObject json = Json.parse(new InputStreamReader(in)).asObject();
        JsonObject versions = json.get("versions").asObject();

        JsonValue elem = versions.get(BetterFps.MC_VERSION);
        if(elem == null) return;
        JsonArray array = elem.asArray();
        if(array.isEmpty()) return;

        JsonObject latest = array.get(0).asObject();
        String version = latest.getString("name", BetterFps.VERSION);

        if(!version.contains(BetterFps.VERSION)) {
            String title = i18n("betterfps.update.available", version);
            String msg = title + "\n" + i18n("betterfps.update.prompt");
            int r = JOptionPane.showConfirmDialog(installer, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if(r == JOptionPane.YES_OPTION) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(latest.getString("url", BetterFps.URL)));
                installer.setVisible(false);
                System.exit(0);
            }
        }
    }

    public static void saveAlgorithm() throws IOException {
        if(ALGORITHM == null) return;
        if(GAME_DIR == null) GAME_DIR = getSuggestedMinecraftFolder();
        info("Changing the algorithm to %s...", ALGORITHM);

        File config = new File(GAME_DIR, "config/betterfps.json");
        JsonObject json;

        if(config.exists()) {
            FileReader reader = new FileReader(config);
            json = Json.parse(reader).asObject();
            reader.close();
        } else {
            json = Json.object();
            config.getParentFile().mkdirs();
            config.createNewFile();
        }

        json.set("algorithm", ALGORITHM);

        FileWriter writer = new FileWriter(config);
        json.writeTo(writer);
        writer.close();
    }

    public static void copyLibrary(File gameDir) throws IOException {
        String path = String.format("libraries/%s/%s/%s", LIBRARY_IDENTIFIER, LIBRARY_NAME, VERSION_NAME);
        File libraryDir = new File(gameDir, path);
        libraryDir.mkdirs();

        File library = new File(libraryDir, String.format("%s-%s.jar", LIBRARY_NAME, VERSION_NAME));
        library.createNewFile();

        copyMod(library);
    }

    public static void copyMod(File output) throws IOException {
        info("Copying the mod file...");

        URL modFile = BetterFpsInstaller.class.getProtectionDomain().getCodeSource().getLocation();
        InputStream is = modFile.openStream();
        OutputStream os = new FileOutputStream(output);
        byte[] buffer = new byte[1024];
        int length;
        while((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
    }

    public static JsonObject loadVersion(File gameDir, String version) throws IOException {
        info("Loading version json from %s...", version);
        File versionFile = new File(gameDir, String.format("versions/%s/%s.json", version, version));
        FileReader reader = new FileReader(versionFile);
        JsonObject obj = Json.parse(reader).asObject();
        reader.close();
        return obj;
    }

    public static void saveVersion(File gameDir, String version, JsonObject data) throws IOException {
        info("Saving version json to %s...", version);
        File versionFile = new File(gameDir, String.format("versions/%s/%s.json", version, version));
        versionFile.getParentFile().mkdirs();
        versionFile.createNewFile();
        FileWriter writer = new FileWriter(versionFile);
        data.writeTo(writer, WriterConfig.PRETTY_PRINT);
        writer.close();
    }

    public static JsonObject generateVersion(JsonObject original, String versionName) {
        JsonObject version = new JsonObject();
        String oldId = original.getString("id", BetterFps.MC_VERSION);
        String date = getDateISO();

        version.set("id", versionName);
        version.set("inheritsFrom", oldId);
        version.set("jar", original.getString("jar", oldId));
        version.set("mainClass", "net.minecraft.launchwrapper.Launch");
        version.set("time", date);
        version.set("releaseTime", date);
        version.set("type", original.getString("type", "release"));

        String arguments = original.getString("minecraftArguments", "");
        version.set("minecraftArguments", String.format("%s --tweakClass %s", arguments, TWEAK_CLASS));

        JsonArray libraries = new JsonArray();

        String ver = String.format("%s:%s:%s", LIBRARY_IDENTIFIER, LIBRARY_NAME, VERSION_NAME);
        libraries.add(new JsonObject().add("name", ver));

        for(String lib : LIBRARIES) {
            libraries.add(new JsonObject().add("name", lib));
        }

        version.set("libraries", libraries);

        return version;
    }

    public static JsonObject loadProfiles(File gameDir) throws IOException {
        info("Loading profiles json for game dir: %s", gameDir.getAbsolutePath());
        File profilesFile = new File(gameDir, "launcher_profiles.json");
        FileReader reader = new FileReader(profilesFile);
        JsonObject obj = Json.parse(reader).asObject();
        reader.close();
        return obj;
    }

    public static void saveProfiles(File gameDir, JsonObject launcherProfiles) throws IOException {
        info("Saving profiles json...");
        File profilesFile = new File(gameDir, "launcher_profiles.json");
        FileWriter writer = new FileWriter(profilesFile);
        launcherProfiles.writeTo(writer, WriterConfig.PRETTY_PRINT);
        writer.close();
    }

    public static List<String> getProfileNames(JsonObject launcherProfiles) {
        List<String> profileNames = new ArrayList<String>();
        JsonObject profs = launcherProfiles.get("profiles").asObject();

        for(String prof : profs.names()) {
            JsonObject obj = profs.get(prof).asObject();
            if(!obj.getString("type", "custom").equalsIgnoreCase("custom")) continue;
            profileNames.add(obj.getString("name", prof));
        }

        return profileNames;
    }

    public static void addProfile(JsonObject launcherProfiles, String selectedProfileName, String version) {
        JsonObject profs = launcherProfiles.get("profiles").asObject();
        JsonObject selectedProfile = null;

        for(String prof : profs.names()) {
            JsonObject obj = profs.get(prof).asObject();
            String name = obj.getString("name", prof);
            if(name.equalsIgnoreCase(selectedProfileName)) {
                selectedProfile = obj;
                break;
            }
        }

        String date = getDateISO();

        if(selectedProfile == null) {
            selectedProfile = new JsonObject();
            selectedProfile.add("name", selectedProfileName);
            selectedProfile.add("type", "custom");
            selectedProfile.add("created", date);
            selectedProfile.add("icon", "Leaves_Oak");
            profs.add(selectedProfileName, selectedProfile);
        }

        selectedProfile.set("lastUsed", date);
        selectedProfile.set("lastVersionId", version);
    }

    private static String getDateISO() {
        // ISO 8601
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        return format.format(new Date());
    }

}
