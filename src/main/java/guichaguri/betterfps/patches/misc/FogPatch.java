package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;

/**
 * @author Guilherme Chaguri
 */
public abstract class FogPatch extends EntityRenderer {

    public FogPatch(Minecraft mc, IResourceManager manager) {
        super(mc, manager);
    }

    @Copy(Mode.REPLACE)
    @Condition(Conditions.FOG_DISABLED)
    @Override
    public void setupFog(int startCoords, float partialTicks) {
        if(startCoords != -1) {
            GlStateManager.disableFog();
            return;
        }
        super.setupFog(startCoords, partialTicks);
    }

}
