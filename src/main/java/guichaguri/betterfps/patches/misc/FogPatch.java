package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

/**
 * @author Guilherme Chaguri
 */
public abstract class FogPatch extends EntityRenderer {

    public FogPatch(Minecraft mc, IResourceManager manager) {
        super(mc, manager);
    }

    @Copy(Mode.REPLACE)
    @Override
    public void setupFog(int startCoords, float partialTicks) {
        if(startCoords != -1) return;
        super.setupFog(startCoords, partialTicks);
    }

}
