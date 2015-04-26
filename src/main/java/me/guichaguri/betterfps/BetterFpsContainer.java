package me.guichaguri.betterfps;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraft.util.MathHelper;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsContainer extends DummyModContainer {

    private static ModMetadata createMetadata() {
        ModMetadata meta = new ModMetadata();
        meta.modId = BetterFps.MODID;
        meta.name = "BetterFps";
        meta.version = BetterFps.VERSION;
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
        FMLCommonHandler.instance().bus().register(this);

        if(!BetterMathHelper.ALGORITHM_NAME.equals("vanilla")) {

            try {
                Method m = MathHelper.class.getMethod("bfInit");
                m.setAccessible(true);
                m.invoke(null);
            } catch(Exception ex) {
                // Maybe bfInit does not exist? Can be possible if the algorithm does not have a static block
            }

            try {
                // UNLOAD CACHED UNNECESSARY VALUES
                for(Field f : MathHelper.class.getDeclaredFields()) {
                    String name = f.getName();
                    if((name.equals("SIN_TABLE")) || (name.equals("a"))) { // field_76144_a
                        f.setAccessible(true);
                        f.set(null, null);
                    }
                }
            } catch(Exception ex) {
                // An error ocurred while unloading vanilla sin table? Its not a big problem.
            }

        }
    }

    @SubscribeEvent
    public void OnConfigChangedEvent(OnConfigChangedEvent event) {
        if(event.modID.equals(BetterFps.MODID)) {
            BetterMathHelper.CONFIG.save();
        }
    }

    @Override
    public String getGuiClassName() {
        return "me.guichaguri.betterfps.client.ConfigFactory";
    }

}
