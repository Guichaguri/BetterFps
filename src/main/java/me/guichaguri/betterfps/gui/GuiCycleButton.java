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
    private List<String> keys;
    private HashMap<String, String> values;

    public GuiCycleButton(int buttonId, int x, int y, int width, int height,
                          String title, HashMap<String, String> values, String defaultValue) {
        super(buttonId, x, y, width, height, title);
        this.title = title;
        this.keys = new ArrayList<String>(values.keySet());
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

    public String getSelectedValue() {
        return keys.get(key);
    }

}
