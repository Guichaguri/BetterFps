package me.guichaguri.betterfps.fml;

import java.io.File;
import java.util.Map;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

/**
 * @author Guilherme Chaguri
 */

@TransformerExclusions({
        "me.guichaguri.betterfps", "me.guichaguri.betterfps.math",
        "me.guichaguri.betterfps.vanilla", "me.guichaguri.betterfps.fml"
})
@MCVersion("1.8")
public class BetterFpsForge implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    public String[] getASMTransformerClass() {
        return BetterFpsHelper.TRANSFORMERS;
    }
    @Override
    public String getModContainerClass() {
        return "me.guichaguri.betterfps.fml.BetterFpsContainer";
    }
    @Override
    public String getSetupClass() {
        return "me.guichaguri.betterfps.fml.BetterFpsForge";
    }
    @Override
    public void injectData(Map<String, Object> data) {
        BetterFpsHelper.LOC = (File)data.get("coremodLocation");
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
