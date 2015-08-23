package me.guichaguri.betterfps.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

/**
 * @author Guilherme Chaguri
 */
public class GuiCycleButton extends GuiButton {
    private String title;
    protected int key = 0;
    protected List<? extends Object> keys;
    protected HashMap<? extends Object, String> values;
    private String[] helpLines;

    public <T extends Object> GuiCycleButton(int buttonId, String title, HashMap<T, String> values, T defaultValue, String[] helpLines) {
        super(buttonId, 0, 0, title);
        this.title = title;
        this.keys = new ArrayList<T>(values.keySet());
        this.helpLines = helpLines;
        for(int i = 0; i < keys.size(); i++) {
            if(defaultValue.equals(keys.get(i))) {
                key = i; break;
            }
        }
        this.values = values;
        updateTitle();
    }



    public void actionPerformed() {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && shiftClick()) return;
        key++;
        if(key >= keys.size()) key = 0;
        updateTitle();
    }

    public boolean shiftClick() {
        return false;
    }

    protected void updateTitle() {
        displayString = title + ": " + values.get(keys.get(key));
    }

    public <T extends Object> T getSelectedValue() {
        return (T)keys.get(key);
    }

    public String[] getHelpText() {
        return helpLines;
    }

    public static class GuiBooleanButton extends GuiCycleButton {
        private static final HashMap<Boolean, String> booleanValues = new HashMap<Boolean, String>();
        static {
            booleanValues.put(true, "On");
            booleanValues.put(false, "Off");
        }

        public GuiBooleanButton(int buttonId, String title, boolean defaultValue, String[] helpLines) {
            super(buttonId, title, booleanValues, defaultValue, helpLines);
        }

    }

}
