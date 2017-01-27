package guichaguri.betterfps.transformers.patcher;

import guichaguri.betterfps.ASMUtils;
import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.tweaker.BetterFpsTweaker;
import guichaguri.betterfps.tweaker.Mappings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Guilherme Chaguri
 */
public class PatcherTransformer implements IClassTransformer {

    private static final Map<Mappings, String> patches = new HashMap<Mappings, String>();

    static {
        patches.put(Mappings.C_TileEntityHopper, "guichaguri/betterfps/patches/block/FastHopper");
        patches.put(Mappings.C_BlockHopper, "guichaguri/betterfps/patches/block/FastHopperBlock");

        patches.put(Mappings.C_TileEntityBeacon, "guichaguri/betterfps/patches/block/FastBeacon");
        patches.put(Mappings.C_TileEntityBeaconRenderer, "guichaguri/betterfps/patches/block/FastBeaconRender");

        patches.put(Mappings.C_EntityRenderer, "guichaguri/betterfps/patches/misc/FogPatch");
        patches.put(Mappings.C_GuiOptions, "guichaguri/betterfps/patches/misc/OptionsButton");

        patches.put(Mappings.C_Minecraft, "guichaguri/betterfps/patches/misc/MinecraftPatch");
        patches.put(Mappings.C_DedicatedServer, "guichaguri/betterfps/patches/misc/ServerPatch");
        patches.put(Mappings.C_EntityPlayerSP, "guichaguri/betterfps/patches/misc/ClientPlayerPatch"); //TODO EntityPlayerSP
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        ClassNode patchClass = findPatch(name);
        if(patchClass == null) return bytes; // There is no patch for this class

        ClassNode classNode = ASMUtils.readClass(bytes, 0);
        Patch patch = new Patch(patchClass, classNode);

        BetterFpsHelper.LOG.info("Patching {}... ({})", transformedName, name);

        // Copy fields and methods with the @Copy annotation
        patch.copy();

        // Patch the class with a custom patcher
        patch.patch();

        bytes = ASMUtils.writeClass(classNode, 0);

        try {
            File f = new File("debugPatch", name + ".class");
            f.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(f, bytes);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return bytes;
    }

    private ClassNode findPatch(String name) {
        for(Mappings m : patches.keySet()) {
            if(m.is(name)) {
                return loadPatch(patches.get(m));
            }
        }
        return null;
    }

    private ClassNode loadPatch(String className) {
        String path = className.replaceAll("\\.", "/") + ".class";
        InputStream in = null;
        try {
            in = BetterFpsTweaker.getResourceStream(path);
            return ASMUtils.readClass(IOUtils.toByteArray(in), 0);
        } catch(IOException ex) {
            BetterFpsHelper.LOG.error("Couldn't load patch from {}", className);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return null;
    }

}
