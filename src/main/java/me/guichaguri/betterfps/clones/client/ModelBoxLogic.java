package me.guichaguri.betterfps.clones.client;

import me.guichaguri.betterfps.transformers.cloner.CopyBoolCondition;
import me.guichaguri.betterfps.transformers.cloner.CopyMode;
import me.guichaguri.betterfps.transformers.cloner.CopyMode.Mode;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @author Guilherme Chaguri
 */
public class ModelBoxLogic extends ModelBox {

    @CopyMode(Mode.IGNORE)
    public ModelBoxLogic(ModelRenderer renderer, int i1, int i2, float f1, float f2, float f3, int i3, int i4, int i5, float f4) {
        super(renderer, i1, i2, f1, f2, f3, i3, i4, i5, f4);
    }

    @CopyBoolCondition(key = "fastBoxRender", value = true)
    @CopyMode(Mode.REPLACE)
    @Override
    public void render(WorldRenderer renderer, float scale) {
        boolean b = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        if(b) {
            super.render(renderer, scale);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
            super.render(renderer, scale);
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }
}
