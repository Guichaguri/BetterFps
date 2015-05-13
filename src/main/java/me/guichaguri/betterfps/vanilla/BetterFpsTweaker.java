package me.guichaguri.betterfps;

import java.io.File;
import java.util.List;
import me.guichaguri.betterfps.BetterHelper;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * Only used when is pure vanilla
 * @author Guilherme Chaguri
 */
public class BetterFpsTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> list, File file, File file1, String s) {

    }
    @Override
    public void injectIntoClassLoader(LaunchClassLoader cl) {
        for(String transformer : BetterHelper.TRANSFORMERS) {
            cl.registerTransformer(transformer);
        }
    }
    @Override
    public String getLaunchTarget() {
        return null;
    }
    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
