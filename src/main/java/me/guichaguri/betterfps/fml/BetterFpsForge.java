package me.guichaguri.betterfps.fml;

import java.io.File;
import java.util.Map;
import me.guichaguri.betterfps.BetterHelper;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

/**
 * @author Guilherme Chaguri
 */

@TransformerExclusions({"me.guichaguri.betterfps", "me.guichaguri.betterfps.math"})
public class BetterFpsForge implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"me.guichaguri.betterfps.BetterFpsTransformer"};
    }
    @Override
    public String getModContainerClass() {
        return "me.guichaguri.betterfps.BetterFpsContainer";
    }
    @Override
    public String getSetupClass() {
        return "me.guichaguri.betterfps.BetterFps";
    }
    @Override
    public void injectData(Map<String, Object> data) {
        BetterHelper.LOC = (File)data.get("coremodLocation");
    }
    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public Void call() throws Exception {
        return null;
    }
}
