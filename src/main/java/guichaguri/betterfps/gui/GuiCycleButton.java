package guichaguri.betterfps.gui;

import net.minecraft.client.gui.GuiButton;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Guilherme Chaguri
 */
public class GuiCycleButton<T extends Object> extends GuiButton {

    protected final String title;

    protected int index = 0;
    protected Object[] values = new Object[0];
    protected String[] displayNames = new String[0];

    public GuiCycleButton(int id, String title) {
        super(id, 0, 0, title);
        this.title = title;

        updateTitle();
    }

    public void set(T[] values, String[] displayNames) {
        this.values = values;
        this.displayNames = displayNames;
    }

    public void add(T value, String displayName) {
        displayNames = ArrayUtils.add(displayNames, displayName);
        values = ArrayUtils.add(values, value);
    }

    public T getValue() {
        return (T)values[index];
    }

    public void setValue(T elem) {
        for(int i = 0; i < values.length; i++) {
            if(values[i] == elem) {
                this.index = i;
                this.updateTitle();
                break;
            }
        }
    }

    public String toDisplayName(T elem) {
        for(int i = 0; i < values.length; i++) {
            if(values[i] == elem) return displayNames[i];
        }
        return null;
    }

    public void actionPerformed() {
        index++;
        if(index >= values.length) index = 0;
        updateTitle();
    }

    public void setTitle(String title) {
        displayString = title;
    }

    public void updateTitle() {
        if(index >= displayNames.length) return;
        displayString = String.format("%s: %s", title, displayNames[index]);
    }

}
