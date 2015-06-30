package me.guichaguri.betterfps;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Guilherme Chaguri
 */
public class UpdateChecker implements Runnable {

    private static boolean updateCheck = false;
    private static boolean done = false;
    private static Properties prop = null;

    // Returns true when everything is done
    public static boolean tick() {
        if(!BetterFpsHelper.CHECK_UPDATES) {
            return true;
        }
        if(done) {
            if((prop != null) && (!BetterFpsHelper.VERSION.equals(prop.getProperty("version")))) {
                showUpdate();
            }
            return true;
        }
        if(!updateCheck) {
            updateCheck = true;
            Thread thread = new Thread(new UpdateChecker(), "BetterFps UpdateChecker");
            thread.start();
        }
        return false;
    }

    private static void showUpdate() {
        EntityPlayer p = BetterFps.mc.thePlayer;
        ChatComponentText txt;

        txt = new ChatComponentText("BetterFps " + prop.getProperty("version") + " is available");
        txt.setChatStyle(txt.getChatStyle().setColor(EnumChatFormatting.GREEN).setBold(true));
        p.addChatComponentMessage(txt);

        txt = new ChatComponentText(prop.getProperty("quick-description"));
        txt.setChatStyle(txt.getChatStyle().setColor(EnumChatFormatting.GRAY));
        p.addChatComponentMessage(txt);

        txt = new ChatComponentText(" ");
        txt.setChatStyle(txt.getChatStyle().setColor(EnumChatFormatting.YELLOW));
        txt.appendSibling(createButton("Download", prop.getProperty("download-url"), "Click here to download the new version"));
        txt.appendText("  ");
        txt.appendSibling(createButton("More Information", prop.getProperty("moreinfo-url"), "Click here for more information about the update"));
        p.addChatComponentMessage(txt);
    }

    private static ChatComponentText createButton(String label, String link, String hover) {
        ChatComponentText sib = new ChatComponentText("[" + label + "]");
        ChatStyle style = sib.getChatStyle();
        style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        ChatComponentText h = new ChatComponentText(hover);
        h.setChatStyle(h.getChatStyle().setColor(EnumChatFormatting.RED));
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, h));
        sib.setChatStyle(style);
        return sib;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(BetterFpsHelper.UPDATE_URL);
            InputStream in = url.openStream();
            Properties p = new Properties();
            p.load(in);

            prop = p;
            done = true;
        } catch(Exception ex) {
            ex.printStackTrace();
            done = true;
        }
    }
}
