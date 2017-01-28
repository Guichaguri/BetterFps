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

        TextComponentString title = new TextComponentString("BetterFps " + updateVersion + " is available");
        title.setStyle(title.getStyle().setColor(TextFormatting.GREEN).setBold(true));

        TextComponentString buttons = new TextComponentString("  ");
        buttons.setStyle(buttons.getStyle().setColor(TextFormatting.YELLOW));
        buttons.appendSibling(createButton("Download", updateDownload, "Click here to download the new version"));
        buttons.appendText("  ");
        buttons.appendSibling(createButton("More", BetterFps.URL, "Click here for more versions"));

        TextComponentString desc = new TextComponentString(getRandomPhrase());
        desc.setStyle(desc.getStyle().setColor(TextFormatting.GRAY));

        if(updateVersion.length() < 8) {
            title.appendSibling(buttons);
            player.addChatComponentMessage(title, false);
            player.addChatComponentMessage(desc, false);
        } else {
            player.addChatComponentMessage(title, false);
            player.addChatComponentMessage(buttons, false);
            player.addChatComponentMessage(desc, false);
        }

        updateVersion = null;
        updateDownload = null;
    }

    public static void showConsole() {
        if(!done) return;
        if(updateVersion == null && updateDownload == null) return;

        BetterFpsHelper.LOG.info("BetterFps " + updateVersion + " is available");
        BetterFpsHelper.LOG.info(getRandomPhrase());
        BetterFpsHelper.LOG.info("Download: " + updateDownload);
        BetterFpsHelper.LOG.info("More: " + BetterFps.URL);

        updateVersion = null;
        updateDownload = null;
    }

    private static TextComponentString createButton(String label, String link, String hover) {
        TextComponentString sib = new TextComponentString("[" + label + "]");
        Style style = sib.getStyle();
        style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        TextComponentString h = new TextComponentString(hover);
        h.setStyle(h.getStyle().setColor(TextFormatting.RED));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, h));
        sib.setStyle(style);
        return sib;
    }

    private static String getRandomPhrase() {
        // Thanks to Kevin8082 for some of this phrases
        String[] phrases = new String[]{
                "Just another annoying update.",
                "Babe are you a new update? Because not now.",
                "Seeing that a new update came out, it fills you with determination.",
                "When Stanley came into an update reminder, he pressed the \"download\" button.",
                "Not again D:",
                "<3",
                "You will probably update just to get rid of this reminder",
                "HARDWARE FAILURE! HARDWARE FAILURE! No wait, I derped out.",
                "Less annoying than Windows Updater... I think",
                "Rope is cut, update is out, and the entire server is after me now, great...",
                "It's free. Go get it ;)",
                "Some bugs are now fixed, but new ones have been introduced",
                "It will not update automagically, you know...",
                "Do you want something new? Here, have an update."
        };

        return phrases[(int)(Math.random() * phrases.length)];
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
                if(Minecraft.getMinecraft().thePlayer != null) {
                    showChat(Minecraft.getMinecraft().thePlayer);
                }
            }
        } catch(Exception ex) {
            BetterFpsHelper.LOG.warn("Could not check for updates: " + ex.getMessage());
        } finally {
            done = true;
        }
    }
}
