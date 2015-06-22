package me.guichaguri.betterfps.vanilla;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * Only used when is pure vanilla
 * @author Guilherme Chaguri
 */
public class BetterFpsTweaker implements ITweaker {

    private final String[] EXCLUDED = new String[]{
            //"me.guichaguri.betterfps", "me.guichaguri.betterfps.math",
            "me.guichaguri.betterfps.vanilla", "me.guichaguri.betterfps.fml"
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
            System.out.println("FORGE FOUND, ignoring vanilla tweaker"); // TODO: better forge workaround
            BetterFpsHelper.FORGE = true;
        } catch(Exception ex) {
            BetterFpsHelper.FORGE = false;
            //BetterFpsVanilla.preInit();
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader cl) {
        if(BetterFpsHelper.FORGE) return;
        cl.registerTransformer("me.guichaguri.betterfps.vanilla.EventTransformer");
        for(String transformer : BetterFpsHelper.TRANSFORMERS) {
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
