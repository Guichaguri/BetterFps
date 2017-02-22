package guichaguri.betterfps;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author Guilherme Chaguri
 */
public class UpdateChecker implements Runnable {

    private static boolean updateCheck = false;
    private static boolean done = false;
    private static String updateVersion = null;
    private static String updateDownload = null;

    public static void check() {
        if(!BetterFpsHelper.getConfig().updateChecker) {
            done = true;
            return;
        }
        if(!updateCheck) {
            updateCheck = true;
            checkForced();
        }
    }

    public static void checkForced() {
        done = false;
        Thread thread = new Thread(new UpdateChecker(), "BetterFps Update Checker");
        thread.setDaemon(true);
        thread.start();
    }

    public static void showChat(EntityPlayerSP player) {
        if(!done) return;
        if(updateVersion == null && updateDownload == null) return;
        if(!BetterFps.CLIENT) return;

        TextComponentTranslation title = new TextComponentTranslation("betterfps.update.available", updateVersion);
        title.setStyle(title.getStyle().setColor(TextFormatting.GREEN).setBold(true));

        TextComponentString buttons = new TextComponentString("  ");
        buttons.setStyle(buttons.getStyle().setColor(TextFormatting.YELLOW));
        buttons.appendSibling(createButton(updateDownload, "betterfps.update.button.download"));
        buttons.appendText("  ");
        buttons.appendSibling(createButton(BetterFps.URL, "betterfps.update.button.more"));

        int phrase = (int)(Math.random() * 12) + 1;
        TextComponentTranslation desc = new TextComponentTranslation("betterfps.update.phrase." + phrase);
        desc.setStyle(desc.getStyle().setColor(TextFormatting.GRAY));

        if(updateVersion.length() < 8) {
            title.appendSibling(buttons);
            player.sendStatusMessage(title, false);
            player.sendStatusMessage(desc, false);
        } else {
            player.sendStatusMessage(title, false);
            player.sendStatusMessage(buttons, false);
            player.sendStatusMessage(desc, false);
        }

        updateVersion = null;
        updateDownload = null;
    }

    private static void showConsole() {
        if(!done) return;
        if(updateVersion == null && updateDownload == null) return;

        BetterFpsHelper.LOG.info("BetterFps " + updateVersion + " is available");
        BetterFpsHelper.LOG.info("Download: " + updateDownload);
        BetterFpsHelper.LOG.info("More: " + BetterFps.URL);

        updateVersion = null;
        updateDownload = null;
    }

    private static TextComponentTranslation createButton(String link, String key) {
        TextComponentTranslation sib = new TextComponentTranslation(key);
        Style style = sib.getStyle();
        style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        TextComponentTranslation h = new TextComponentTranslation(key + ".info");
        h.setStyle(h.getStyle().setColor(TextFormatting.RED));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, h));
        sib.setStyle(style);
        return sib;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(BetterFps.UPDATE_URL);
            InputStream in = url.openStream();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(new InputStreamReader(in)).getAsJsonObject();
            JsonObject versions = obj.getAsJsonObject("versions");

            if(!versions.has(BetterFps.MC_VERSION)) return;
            JsonArray array = versions.getAsJsonArray(BetterFps.MC_VERSION);
            if(array.size() == 0) return;

            JsonObject latest = array.get(0).getAsJsonObject();
            String version = latest.get("name").getAsString();

            if(!version.contains(BetterFps.VERSION)) {
                updateVersion = version;
                updateDownload = latest.get("url").getAsString();
            }

            done = true;

            if(!BetterFps.CLIENT) {
                showConsole();
            } else {
                if(Minecraft.getMinecraft().player != null) {
                    showChat(Minecraft.getMinecraft().player);
                }
            }
        } catch(Exception ex) {
            BetterFpsHelper.LOG.warn("Could not check for updates: " + ex.getMessage());
        } finally {
            done = true;
        }
    }
}
