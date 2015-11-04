package me.guichaguri.betterfps.clones.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

/**
 * @author Guilherme Chaguri
 */
public class WorldLogic extends WorldServer {
    private static int radius = 5;

    private WorldLogic(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn) {
        super(server, saveHandlerIn, info, dimensionId, profilerIn);
    }

    public void test() {
        TestClass pair = null;
        if(pair.happyBoolean) {
            return;
        }
        test();
    }

    public class TestClass {
        public boolean happyBoolean;
    }

}
