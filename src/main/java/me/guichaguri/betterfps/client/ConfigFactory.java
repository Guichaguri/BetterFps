package me.guichaguri.betterfps.client;

import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import me.guichaguri.betterfps.fml.BetterFpsForge;
import me.guichaguri.betterfps.BetterHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;

/**
 * @author Guilherme Chaguri
 */
public class ConfigFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {

    }
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigScreen.class;
    }
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }


    public static List<IConfigElement> getElements() {
        List<IConfigElement> elements = new ArrayList<IConfigElement>();
        elements.add(new ConfigElementCycle(BetterHelper.CONFIG_ALGORITHM));
        return elements;
    }

    public static class ConfigScreen extends GuiConfig {
        public ConfigScreen(GuiScreen parent) {//new ConfigElement(BetterMathHelper.CONFIG.getCategory("betterfps")).getChildElements()
            super(parent, getElements(), BetterFpsForge.MODID, BetterFpsForge.MODID, false, false, "BetterFps Algorithm");
        }
    }

    public static class ConfigElementCycle extends ConfigElement {
        String[] validValues;
        public ConfigElementCycle(Property prop) {
            super(prop);
            List<String> l = new ArrayList<String>();
            for(Entry<String, String> s : BetterHelper.displayHelpers.entrySet()) {
                if(s.getKey().equals("random")) continue;
                l.add(s.getValue());
            }
            validValues = l.toArray(new String[l.size()]);
        }

        @Override
        public String[] getValidValues() {
            return validValues;
        }
        @Override
        public Object getDefault() {
            return BetterHelper.displayHelpers.get(super.getDefault());
        }
        @Override
        public Object get() {
            return BetterHelper.displayHelpers.get(super.get());
        }
        @Override
        public String getName() {
            return "Algorithm";
        }
        @Override
        public void set(Object value) {
            String v = "rivens";
            for(Entry<String, String> e : BetterHelper.displayHelpers.entrySet()) {
                if(e.getValue().equals(value)) {
                    v = e.getKey();
                    break;
                }
            }
            super.set(v);
        }
    }

}
