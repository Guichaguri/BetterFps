package me.guichaguri.betterfps.fml;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import me.guichaguri.betterfps.BetterHelper;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsContainer extends DummyModContainer {

    private static ModMetadata createMetadata() {
        ModMetadata meta = new ModMetadata();
        meta.modId = BetterHelper.MODID;
        meta.name = "BetterFps";
        meta.version = BetterHelper.VERSION;
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
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);

        if(!BetterHelper.ALGORITHM_NAME.equals("vanilla")) {

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
        if(event.modID.equals(BetterHelper.MODID)) {
            BetterHelper.CONFIG.save();
        }
    }

    @Override
    public String getGuiClassName() {
        return "me.guichaguri.betterfps.client.ConfigFactory";
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
