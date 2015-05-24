package me.guichaguri.betterfps.fml;

import java.util.Set;
import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

/**
 * @author Guilherme Chaguri
 */
public class ConfigFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {

    }
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GuiBetterFpsConfig.class;
    }
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

}
