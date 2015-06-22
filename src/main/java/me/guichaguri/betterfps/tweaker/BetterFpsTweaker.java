package me.guichaguri.betterfps.tweaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * Only used when is pure tweaker
 * @author Guilherme Chaguri
 */
public class BetterFpsTweaker implements ITweaker {

    private static final String[] TRANSFORMERS = new String[]{
            "me.guichaguri.betterfps.transformers.MathTransformer",
            "me.guichaguri.betterfps.transformers.EventTransformer"
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
        this.args.add("--assetsDir");
        this.args.add(assetsDir.getAbsolutePath());
        this.args.add("--gameDir");
        this.args.add(gameDir.getAbsolutePath());
        BetterFpsHelper.MCDIR = gameDir;
        try {
            Class.forName("net.minecraftforge.fml.common.launcher.FMLTweaker");
            System.out.println("FORGE FOUND, ignoring tweaker tweaker"); // TODO: better forge workaround
            BetterFpsHelper.FORGE = true;
        } catch(Exception ex) {
            BetterFpsHelper.FORGE = false;
            //BetterFpsVanilla.preInit();
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader cl) {
        if(BetterFpsHelper.FORGE) return;

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
        return BetterFpsHelper.FORGE ? new String[0] : args.toArray(new String[args.size()]);
    }
}
