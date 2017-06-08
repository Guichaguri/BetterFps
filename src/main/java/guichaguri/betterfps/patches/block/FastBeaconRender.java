package guichaguri.betterfps.patches.block;

import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * @author Guilherme Chaguri
 */
@Condition(Conditions.FAST_BEAM_RENDER)
public abstract class FastBeaconRender extends TileEntityBeaconRenderer {

    @Copy(Mode.REPLACE)
    public static void renderBeamSegment(double x, double y, double z, double partialTicks, double scale, double worldTime, int beamY, int beamHeight, float[] colors, double p_188205_15_, double p_188205_17_) {
        int maxY = beamY + beamHeight;
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        double time = worldTime + partialTicks;
        double d1 = beamHeight < 0 ? time : -time;
        double d2 = MathHelper.frac(d1 * 0.2D - (double)MathHelper.floor(d1 * 0.1D));
        float r = colors[0];
        float g = colors[1];
        float b = colors[2];
        double d3 = time * 0.025D * -1.5D;
        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * p_188205_15_;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * p_188205_15_;
        double d6 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * p_188205_15_;
        double d7 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * p_188205_15_;
        double d8 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * p_188205_15_;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * p_188205_15_;
        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * p_188205_15_;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * p_188205_15_;
        double d14 = -1.0D + d2;
        double d15 = (double)beamHeight * scale * (0.5D / p_188205_15_) + d14;
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexbuffer.pos(x + d4, y + (double)maxY, z + d5).tex(1.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d4, y + (double)beamY, z + d5).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d6, y + (double)beamY, z + d7).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d6, y + (double)maxY, z + d7).tex(0.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d10, y + (double)maxY, z + d11).tex(1.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d10, y + (double)beamY, z + d11).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d8, y + (double)beamY, z + d9).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d8, y + (double)maxY, z + d9).tex(0.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d6, y + (double)maxY, z + d7).tex(1.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d6, y + (double)beamY, z + d7).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d10, y + (double)beamY, z + d11).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d10, y + (double)maxY, z + d11).tex(0.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d8, y + (double)maxY, z + d9).tex(1.0D, d15).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d8, y + (double)beamY, z + d9).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d4, y + (double)beamY, z + d5).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        vertexbuffer.pos(x + d4, y + (double)maxY, z + d5).tex(0.0D, d15).color(r, g, b, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

}
