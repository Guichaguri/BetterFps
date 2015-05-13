package me.guichaguri.betterfps.vanilla;

import java.io.File;
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
            "me.guichaguri.betterfps", "me.guichaguri.betterfps.math",
            "me.guichaguri.betterfps.vanilla", "me.guichaguri.betterfps.fml"
    };

    private List<String> args;
    private boolean forge = false;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = args;
        try {
            Class.forName("net.minecraftforge.fml.common.launcher.FMLTweaker");
            System.out.println("FORGE FOUND, ignoring vanilla tweaker");
            forge = true;
            /*List<String> tweakers = (List<String>)Launch.blackboard.get("TweakClasses");
            for(String tweaker : tweakers) {
                System.out.println(tweaker);
                if(tweaker.startsWith("net.minecraftforge")) {
                    forge = true;
                }
            }*/
        } catch(Exception ex) {
            forge = false;
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader cl) {
        if(forge) return;
        for(String transformer : BetterFpsHelper.TRANSFORMERS) {
            cl.registerTransformer(transformer);
        }
        for(String excluded : EXCLUDED) {
            cl.addClassLoaderExclusion(excluded);
        }
    }

    @Override
    public String getLaunchTarget() {
        return forge ? "net.minecraft.client.main.Main" : "me.guichaguri.betterfps.vanilla.BetterFpsLauncher";
    }

    @Override
    public String[] getLaunchArguments() {
        return forge ? new String[0] : args.toArray(new String[args.size()]);
    }
}
