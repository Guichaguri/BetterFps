package me.guichaguri.betterfps.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.GuiButton;

/**
 * @author Guilherme Chaguri
 */
public class GuiCycleButton extends GuiButton {
    private String title;
    private int key = 0;
    private List<? extends Object> keys;
    private HashMap<? extends Object, String> values;

    public <T extends Object> GuiCycleButton(int buttonId, String title, HashMap<T, String> values, T defaultValue) {
        super(buttonId, 0, 0, title);
        this.title = title;
        this.keys = new ArrayList<T>(values.keySet());
        for(int i = 0; i < keys.size(); i++) {
            if(defaultValue.equals(keys.get(i))) {
                key = i; break;
            }
        }
        this.values = values;
        updateTitle();
    }

    public void actionPerformed() {
        key++;
        if(key >= keys.size()) key = 0;
        updateTitle();
    }

    private void updateTitle() {
        displayString = title + ": " + values.get(keys.get(key));
    }

    public <T extends Object> T getSelectedValue() {
        return (T)keys.get(key);
    }

    public static class GuiBooleanButton extends GuiCycleButton {
        private static final HashMap<Boolean, String> booleanValues = new HashMap<Boolean, String>();
        static {
            booleanValues.put(true, "On");
            booleanValues.put(false, "Off");
        }

        public GuiBooleanButton(int buttonId, String title, boolean defaultValue) {
            super(buttonId, title, booleanValues, defaultValue);
        }

    }

}
