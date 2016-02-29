package me.guichaguri.betterfps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
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

    public static void check() {
        if(!BetterFpsConfig.getConfig().updateChecker) {
            done = true;
            return;
        }
        if(!updateCheck) {
            updateCheck = true;
            Thread thread = new Thread(new UpdateChecker(), "BetterFps Update Checker");
            thread.setDaemon(true);
            thread.start();
        }
    }

    public static void showChat() {
        if(!done) return;
        if(prop == null) return;
        if(BetterFpsHelper.VERSION.equals(prop.getProperty("version"))) {
            prop = null;
            return;
        }
        if(!BetterFps.isClient) return;

        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        if(chat == null) return;

        ChatComponentText title = new ChatComponentText("BetterFps " + prop.getProperty("version") + " is available");
        title.setChatStyle(title.getChatStyle().setColor(EnumChatFormatting.GREEN).setBold(true));

        ChatComponentText desc = new ChatComponentText(prop.getProperty("quick-description"));
        desc.setChatStyle(desc.getChatStyle().setColor(EnumChatFormatting.GRAY));

        ChatComponentText buttons = new ChatComponentText(" ");
        buttons.setChatStyle(buttons.getChatStyle().setColor(EnumChatFormatting.YELLOW));
        buttons.appendSibling(createButton("Download", prop.getProperty("download-url"), "Click here to download the new version"));
        buttons.appendText("  ");
        buttons.appendSibling(createButton("More Information", prop.getProperty("moreinfo-url"), "Click here for more information about the update"));

        chat.printChatMessage(title);
        chat.printChatMessage(desc);
        chat.printChatMessage(buttons);

        prop = null;
    }

    public static void showConsole() {
        if(!done) return;
        if(prop == null) return;
        if(BetterFpsHelper.VERSION.equals(prop.getProperty("version"))) {
            prop = null;
            return;
        }

        BetterFps.log.info("BetterFps " + prop.getProperty("version") + " is available");
        BetterFps.log.info(prop.getProperty("quick-description"));
        BetterFps.log.info("More information at: " + prop.getProperty("moreinfo-url"));

        prop = null;
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

            if(!BetterFps.isClient) {
                showConsole();
            } else {
                if(Minecraft.getMinecraft().theWorld != null) {
                    showChat();
                }
            }
        } catch(IOException ex) {
            BetterFps.log.warn("Could not check for updates: " + ex.getLocalizedMessage());
            done = true;
        } catch(Exception ex) {
            ex.printStackTrace();
            done = true;
        }
    }
}
