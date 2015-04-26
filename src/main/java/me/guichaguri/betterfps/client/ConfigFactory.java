package me.guichaguri.betterfps.client;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import me.guichaguri.betterfps.BetterFps;
import me.guichaguri.betterfps.BetterMathHelper;
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
        elements.add(new ConfigElementCycle(BetterMathHelper.CONFIG_ALGORITHM));
        return elements;
    }

    public static class ConfigScreen extends GuiConfig {
        public ConfigScreen(GuiScreen parent) {//new ConfigElement(BetterMathHelper.CONFIG.getCategory("betterfps")).getChildElements()
            super(parent, getElements(), BetterFps.MODID, BetterFps.MODID, false, false, "BetterFps Algorithm");
        }
    }

    public static class ConfigElementCycle extends ConfigElement<String> {
        String[] validValues;
        public ConfigElementCycle(Property prop) {
            super(prop);
            List<String> l = new ArrayList<String>();
            for(Entry<String, String> s : BetterMathHelper.displayHelpers.entrySet()) {
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
            return BetterMathHelper.displayHelpers.get(super.getDefault());
        }
        @Override
        public Object get() {
            return BetterMathHelper.displayHelpers.get(super.get());
        }
        @Override
        public String getName() {
            return "Algorithm";
        }
        @Override
        public void set(String value) {
            String v = "rivens";
            for(Entry<String, String> e : BetterMathHelper.displayHelpers.entrySet()) {
                if(e.getValue().equals(value)) {
                    v = e.getKey();
                    break;
                }
            }
            super.set(v);
        }
    }

}
