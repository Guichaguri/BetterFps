package guichaguri.betterfps.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

/**
 * @author Guilherme Chaguri
 */
public class GuiConfigOption<T> extends GuiCycleButton<T> {

    private String description;
    private boolean wide = false;
    private boolean restart = false;
    private int originalValue = -1;
    private T defaultVanilla, defaultBetterFps;
    private Runnable shiftClick;

    public GuiConfigOption(int id, String title) {
        super(id, I18n.format(title));
    }

    public boolean isWide() {
        return wide;
    }

    public void setWide(boolean wide) {
        this.wide = wide;
    }

    public boolean shouldRestart() {
        return restart && originalValue != index;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public void setShiftClick(Runnable shiftClick) {
        this.shiftClick = shiftClick;
    }

    public void setDefaults(T defaultVanilla, T defaultBetterFps, T originalValue) {
        this.defaultVanilla = defaultVanilla;
        this.defaultBetterFps = defaultBetterFps;

        setValue(originalValue);
        this.originalValue = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String ... lines) {
        StringBuilder builder = new StringBuilder();

        for(String line : lines) {
            builder.append(line);
            builder.append('\n');
        }

        builder.append('\n');
        builder.append(ChatFormatting.GRAY);
        if(defaultVanilla == defaultBetterFps) {
            builder.append(I18n.format("betterfps.options.default", toDisplayName(defaultBetterFps)));
        } else {
            builder.append(I18n.format("betterfps.options.default.vanilla", toDisplayName(defaultVanilla)));
            builder.append('\n');
            builder.append(I18n.format("betterfps.options.default.betterfps", toDisplayName(defaultBetterFps)));
        }
        if(restart) {
            builder.append('\n');
            builder.append(ChatFormatting.RED);
            builder.append(I18n.format("betterfps.options.restart"));
        }

        description = builder.toString();
    }

    @Override
    public void add(T value, String displayName) {
        super.add(value, I18n.format(displayName));
    }

    @Override
    public void actionPerformed() {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            if(shiftClick != null) {
                shiftClick.run();
                return;
            }
        }

        super.actionPerformed();
    }

}
