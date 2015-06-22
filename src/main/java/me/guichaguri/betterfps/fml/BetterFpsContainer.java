package me.guichaguri.betterfps.fml;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.util.Arrays;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.gui.GuiBetterFpsConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsContainer extends DummyModContainer {



    private static ModMetadata createMetadata() {
        ModMetadata meta = new ModMetadata();
        meta.modId = BetterFpsHelper.MODID;
        meta.name = "BetterFps";
        meta.version = BetterFpsHelper.VERSION;
        meta.authorList = Arrays.asList("Guichaguri");
        meta.description = "Performance Improvements";
        meta.url = "http://minecraft.curseforge.com/mc-mods/229876-betterfps";
        return meta;
    }

    public BetterFpsContainer() {
        super(createMetadata());
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {

        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }

        // IMPORTING OLD CONFIG - WILL BE REMOVED IN THE FUTURE
        File oldConfig = event.getSuggestedConfigurationFile();
        if(oldConfig.exists()) {
            Configuration config = new Configuration(oldConfig);
            Property configAlgorithm = config.get("betterfps", "algorithm", "rivens-full");
            BetterFpsHelper.CONFIG.setProperty("algorithm", configAlgorithm.getString());
            BetterFpsHelper.saveConfig(); // Save the new algorithm
            BetterFpsHelper.loadConfig(); // Load the new algorithm
            oldConfig.deleteOnExit();
        }

    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        BetterFpsHelper.FORGE = true;

        FMLCommonHandler.instance().bus().register(this);

        BetterFpsHelper.init();

        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientRegistry.registerKeyBinding(BetterFpsHelper.MENU_KEY);
        }
    }


    @SubscribeEvent
    public void KeyInputEvent(KeyInputEvent event) {
        if(BetterFpsHelper.MENU_KEY.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiBetterFpsConfig());
        }
    }

    @Override
    public String getGuiClassName() {
        return "me.guichaguri.betterfps.fml.ConfigFactory";
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        try {
            return VersionRange.createFromVersionSpec("*");
        } catch(Exception ex) {
            return super.acceptableMinecraftVersionRange();
        }
    }

}
