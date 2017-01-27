package guichaguri.betterfps.tweaker;

import guichaguri.betterfps.BetterFpsHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 *
 * @author Guilherme Chaguri
 */
public class BetterFpsTweaker implements ITweaker {

    public static InputStream getResourceStream(String path) {
        InputStream stream;

        // Normal environment
        stream = BetterFpsTweaker.class.getResourceAsStream(path);
        if(stream != null) return stream;

        // Dev environment
        stream = BetterFpsTweaker.class.getClassLoader().getResourceAsStream(path);
        return stream;
    }

    public static URL getResource(String path) {
        URL url;

        // Normal environment
        url = BetterFpsTweaker.class.getResource(path);
        if(url != null) return url;

        // Dev environment
        url = BetterFpsTweaker.class.getClassLoader().getResource(path);
        return url;
    }

    private static final String[] TRANSFORMERS = new String[]{
            "guichaguri.betterfps.transformers.MathTransformer",
            //"guichaguri.betterfps.transformers.EventTransformer",
            //"guichaguri.betterfps.transformers.MiscTransformer",
            //"guichaguri.betterfps.transformers.cloner.ClonerTransformer",
            "guichaguri.betterfps.transformers.patcher.PatcherTransformer",
            //"guichaguri.betterfps.transformers.VisualChunkTransformer"
            //"guichaguri.betterfps.transformers.CapTransformer"
    };

    private final String[] EXCLUDED = new String[]{
            "guichaguri.betterfps.transformers",
            "guichaguri.betterfps.tweaker"
    };

    private List<String> args;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = new ArrayList<String>(args);
        this.args.add("--version");
        this.args.add(profile);
        if(assetsDir != null) {
            this.args.add("--assetsDir");
            this.args.add(assetsDir.getAbsolutePath());
        }
        if(gameDir != null) {
            this.args.add("--gameDir");
            this.args.add(gameDir.getAbsolutePath());
        }

        BetterFpsHelper.MCDIR = gameDir;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader cl) {
        loadMappings();

        for(String transformer : TRANSFORMERS) {
            cl.registerTransformer(transformer);
        }

        for(String excluded : EXCLUDED) {
            cl.addTransformerExclusion(excluded);
        }

        cl.addClassLoaderExclusion("guichaguri.betterfps.clones");
        cl.addClassLoaderExclusion("guichaguri.betterfps.installer");
        cl.addClassLoaderExclusion("guichaguri.betterfps.math");
        cl.addClassLoaderExclusion("guichaguri.betterfps.patches");
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {

        ArrayList args = (ArrayList)Launch.blackboard.get("ArgumentList");
        if(args.isEmpty()) args.addAll(this.args);

        this.args = null;

        // Just in case someone needs to know whether BetterFps is running
        Launch.blackboard.put("BetterFpsVersion", BetterFpsHelper.VERSION);

        return new String[0];
    }

    private void loadMappings() {
        BetterFpsHelper.LOG.debug("Loading Mappings...");
        try {
            Mappings.loadMappings(getResourceStream("betterfps.srg"));
        } catch(IOException ex) {
            BetterFpsHelper.LOG.error("Could not load mappings. Things will not work!");
        }
    }
}
