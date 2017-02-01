package guichaguri.betterfps.gui.data;

import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author Guilherme Chaguri
 */
public class UpdateCheckAction implements Runnable {

    @Override
    public void run() {
        BetterFpsHelper.LOG.info("Checking for updates...");
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player != null) {
            player.sendStatusMessage(new TextComponentTranslation("betterfps.installer.update.check"), true);
        }

        UpdateChecker.checkForced();
    }

}
