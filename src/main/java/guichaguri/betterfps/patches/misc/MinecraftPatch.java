package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.BetterFps;
import guichaguri.betterfps.UpdateChecker;
import guichaguri.betterfps.gui.BetterFpsResourcePack;
import guichaguri.betterfps.transformers.patcher.annotations.Copy;
import guichaguri.betterfps.transformers.patcher.annotations.Copy.Mode;
import guichaguri.betterfps.transformers.patcher.annotations.Patcher;
import guichaguri.betterfps.patchers.MinecraftPatcher;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import org.lwjgl.LWJGLException;

/**
 * @author Guilherme Chaguri
 */
@Patcher(MinecraftPatcher.class)
public abstract class MinecraftPatch extends Minecraft {
    public MinecraftPatch(GameConfiguration config) {
        super(config);
    }

    @Copy(Mode.PREPEND)
    @Override
    public void startGame() throws LWJGLException, IOException {
        BetterFps.isClient = true;

        defaultResourcePacks.add(new BetterFpsResourcePack());

        UpdateChecker.check();
    }
}
