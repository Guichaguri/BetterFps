package me.guichaguri.betterfps.tweaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 *
 * @author Guilherme Chaguri
 */
public class BetterFpsTweaker implements ITweaker {

    private static final String[] TRANSFORMERS = new String[]{
            "me.guichaguri.betterfps.transformers.MathTransformer",
            "me.guichaguri.betterfps.transformers.EventTransformer",
            "me.guichaguri.betterfps.transformers.MiscTransformer",
            "me.guichaguri.betterfps.transformers.ClonerTransformer"
            //"me.guichaguri.betterfps.transformers.CapTransformer"
    };

    private final String[] EXCLUDED = new String[]{
            "me.guichaguri.betterfps.transformers",
            "me.guichaguri.betterfps.tweaker"
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
        for(String transformer : TRANSFORMERS) {
            cl.registerTransformer(transformer);
        }

        for(String excluded : EXCLUDED) {
            cl.addTransformerExclusion(excluded);
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {

        ArrayList args = (ArrayList)Launch.blackboard.get("ArgumentList");
        if(args.isEmpty()) args.addAll(this.args);

        this.args = null; // Unload the list from the ram

        return new String[0];
    }
}
