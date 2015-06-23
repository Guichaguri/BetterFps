package me.guichaguri.betterfps.installer;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.installer.json.JSONArray;
import me.guichaguri.betterfps.installer.json.JSONObject;

/**
 * @author Guilherme Chaguri
 */
public class InstanceInstaller {
    private static final String LIBRARY_IDENTIFIER = "betterfps";
    private static final String LIBRARY_NAME = "BetterFps";
    private static final String VERSION_NAME = BetterFpsHelper.VERSION;

    private static final String TWEAKER = "me.guichaguri.betterfps.tweaker.BetterFpsTweaker";
    private static final String[] LIBRARIES_NAMES = new String[]{
            "org.ow2.asm:asm-all:5.0.3", "net.minecraft:launchwrapper:1.11"
    };

    public static List<String> getVersions(File mcFolder) {
        File versionFolder = new File(mcFolder, "versions");
        if((!versionFolder.exists()) || (!versionFolder.isDirectory())) {
            return null;
        }
        List<String> versions = new ArrayList<String>();
        for(File f : versionFolder.listFiles()) {
            if(!f.isDirectory()) continue;
            if(!f.getName().startsWith(BetterFpsHelper.MC_VERSION)) continue;
            versions.add(f.getName());
        }
        return versions;
    }

    public static void install(File mcFolder, String version) throws Exception {
        InstanceInstaller installer = new InstanceInstaller(mcFolder, version);
        installer.setupJson();
        installer.copyLibrary();
        installer.saveNewVersion();
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

    private File mcFolder;
    private File versionsFolder;
    private String version;
    private File oldVersionFolder;
    private File versionFolder = null;
    private JSONObject versionJson;
    private InstanceInstaller(File mcFolder, String version) throws Exception {
        this.mcFolder = mcFolder;
        this.version = version;
        versionsFolder = new File(mcFolder, "versions");
        oldVersionFolder = new File(versionsFolder, version);

        File versionJsonFile = new File(oldVersionFolder, version + ".json");
        if(!versionJsonFile.exists()) {
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(versionJsonFile));
        String line, json = "";
        while((line = br.readLine()) != null) {
            json += line;
        }
        br.close();
        versionJson = new JSONObject(json);
    }

    public void setupJson() {

        JSONArray libraries = versionJson.getJSONArray("libraries");
        JSONArray newArray = new JSONArray();

        JSONObject betterfpsLib = new JSONObject();
        betterfpsLib.put("name", LIBRARY_IDENTIFIER + ":" + LIBRARY_NAME + ":" + VERSION_NAME);
        newArray.put(betterfpsLib);

        String[] libNames = new String[LIBRARIES_NAMES.length];
        int i = 0;
        for(String name : LIBRARIES_NAMES) {
            JSONObject lib = new JSONObject();
            lib.put("name", name);
            newArray.put(lib);
            libNames[i] = name.split(":")[1];
            i++;
        }

        libLoop: for(i = 0; i < libraries.length(); i++) {
            JSONObject o = libraries.getJSONObject(i);
            String name = o.getString("name").split(":")[1];
            for(String ln : libNames) {
                if(name.equals(ln)) continue libLoop;
            }
            newArray.put(o);
        }

        versionJson.put("libraries", newArray);

        versionJson.put("mainClass", "net.minecraft.launchwrapper.Launch");
        String jar = versionJson.has("jar") ? versionJson.getString("jar") : version;
        versionJson.put("jar", jar);
        String arguments = versionJson.getString("minecraftArguments");
        arguments += " --tweakClass " + TWEAKER;
        versionJson.put("minecraftArguments", arguments);
    }

    public void copyLibrary() throws Exception {
        URL modFile = BetterFpsInstaller.class.getProtectionDomain().getCodeSource().getLocation();
        File libraries = new File(mcFolder, "libraries");
        File libraryDir = new File(libraries, LIBRARY_IDENTIFIER + "/" + LIBRARY_NAME + "/" + VERSION_NAME);
        libraryDir.mkdirs();
        File library = new File(libraryDir, LIBRARY_NAME + "-" + VERSION_NAME + ".jar");
        InputStream is = modFile.openStream();
        OutputStream os = new FileOutputStream(library);
        byte[] buffer = new byte[1024];
        int length;
        while((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
    }

    public void saveNewVersion() throws Exception {
        String versionName = version + "-BetterFps-" + VERSION_NAME;
        versionJson.put("id", versionName);
        versionFolder = new File(versionsFolder, versionName);
        versionFolder.mkdirs();
        File json = new File(versionFolder, versionName + ".json");
        BufferedWriter bw = new BufferedWriter(new FileWriter(json));
        bw.write(versionJson.toString());
        bw.close();
    }

}
