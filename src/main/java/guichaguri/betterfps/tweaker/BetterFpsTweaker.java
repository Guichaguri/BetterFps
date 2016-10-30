package guichaguri.betterfps.tweaker;

import guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Guilherme Chaguri
 */
public class BetterFpsTweaker implements ITweaker {

    private static final String[] TRANSFORMERS = new String[]{
            "guichaguri.betterfps.transformers.MathTransformer",
            "guichaguri.betterfps.transformers.EventTransformer",
            "guichaguri.betterfps.transformers.MiscTransformer",
            "guichaguri.betterfps.transformers.cloner.ClonerTransformer",
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

        // Just in case someone needs to know if BetterFps is running
        Launch.blackboard.put("BetterFpsVersion", BetterFpsHelper.VERSION);

        return new String[0];
    }

    private void loadMappings() {
        BetterFpsHelper.LOG.debug("Loading Mappings...");
        try {
            // Normal environment
            Mappings.loadMappings(BetterFpsTweaker.class.getResourceAsStream("betterfps.srg"));
        } catch(Exception ex) {
            try {
                // Dev environment
                Mappings.loadMappings(BetterFpsTweaker.class.getClassLoader().getResourceAsStream("betterfps.srg"));
            } catch(Exception ex2) {
                BetterFpsHelper.LOG.error("Could not load mappings. Things will not work!");
            }
        }
    }
}
