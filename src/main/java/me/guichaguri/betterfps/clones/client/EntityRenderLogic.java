package me.guichaguri.betterfps.clones.client;

import me.guichaguri.betterfps.transformers.cloner.CopyBoolCondition;
import me.guichaguri.betterfps.transformers.cloner.CopyMode;
import me.guichaguri.betterfps.transformers.cloner.CopyMode.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

/**
 * @author Guilherme Chaguri
 */
public class EntityRenderLogic extends EntityRenderer {

    @CopyMode(Mode.IGNORE)
    public EntityRenderLogic(Minecraft mcIn, IResourceManager manager) {
        super(mcIn, manager);
    }

    @CopyBoolCondition(key = "fog")
    @CopyMode(Mode.REPLACE)
    public void setupFog(int p1, float partialTicks) {
        if(p1 != -1) return;
        super.setupFog(p1, partialTicks);
    }
/*
    @CopyMode(Mode.APPEND)
    public void setupFog(int p_78468_1_, float partialTicks) {
        if(p_78468_1_ != -1) return;
        Entity entity = this.mc.getRenderViewEntity();
        boolean flag = false;

        if (entity instanceof EntityPlayer)
        {
            flag = ((EntityPlayer)entity).capabilities.isCreativeMode;
        }

        GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
        float f1;

        float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(this, entity, block, partialTicks, 0.1F);
        if (hook >= 0)
            GlStateManager.setFogDensity(hook);
        else
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness))
        {
            f1 = 5.0F;
            int j = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();

            if (j < 20)
            {
                f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)j / 20.0F);
            }

            GlStateManager.setFog(9729);

            if (p_78468_1_ == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1 * 0.8F);
            }
            else
            {
                GlStateManager.setFogStart(f1 * 0.25F);
                GlStateManager.setFogEnd(f1);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }
        }
        else if (this.cloudFog)
        {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(0.1F);
        }
        else if (block.getMaterial() == Material.water)
        {
            GlStateManager.setFog(2048);

            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing))
            {
                GlStateManager.setFogDensity(0.01F);
            }
            else
            {
                GlStateManager.setFogDensity(0.1F - (float) EnchantmentHelper.getRespiration(entity) * 0.03F);
            }
        }
        else if (block.getMaterial() == Material.lava)
        {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(2.0F);
        }
        else
        {
            f1 = this.farPlaneDistance;
            GlStateManager.setFog(9729);

            if (p_78468_1_ == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1);
            }
            else
            {
                GlStateManager.setFogStart(f1 * 0.75F);
                GlStateManager.setFogEnd(f1);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }

            if (this.mc.theWorld.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ))
            {
                GlStateManager.setFogStart(f1 * 0.05F);
                GlStateManager.setFogEnd(Math.min(f1, 192.0F) * 0.5F);
            }
            net.minecraftforge.client.ForgeHooksClient.onFogRender(this, entity, block, partialTicks, p_78468_1_, f1);
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }*/ /*@CopyMode(Mode.IGNORE)
    public EntityRenderLogic(Minecraft mcIn, IResourceManager manager) {
        super(mcIn, manager);
    }

    @CopyMode(Mode.APPEND)
    public void setupFog(int p_78468_1_, float partialTicks) {
        if(p_78468_1_ != -1) return;
        Entity entity = this.mc.getRenderViewEntity();
        boolean flag = false;

        if (entity instanceof EntityPlayer)
        {
            flag = ((EntityPlayer)entity).capabilities.isCreativeMode;
        }

        GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
        float f1;

        float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(this, entity, block, partialTicks, 0.1F);
        if (hook >= 0)
            GlStateManager.setFogDensity(hook);
        else
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness))
        {
            f1 = 5.0F;
            int j = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();

            if (j < 20)
            {
                f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)j / 20.0F);
            }

            GlStateManager.setFog(9729);

            if (p_78468_1_ == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1 * 0.8F);
            }
            else
            {
                GlStateManager.setFogStart(f1 * 0.25F);
                GlStateManager.setFogEnd(f1);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }
        }
        else if (this.cloudFog)
        {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(0.1F);
        }
        else if (block.getMaterial() == Material.water)
        {
            GlStateManager.setFog(2048);

            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing))
            {
                GlStateManager.setFogDensity(0.01F);
            }
            else
            {
                GlStateManager.setFogDensity(0.1F - (float) EnchantmentHelper.getRespiration(entity) * 0.03F);
            }
        }
        else if (block.getMaterial() == Material.lava)
        {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(2.0F);
        }
        else
        {
            f1 = this.farPlaneDistance;
            GlStateManager.setFog(9729);

            if (p_78468_1_ == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1);
            }
            else
            {
                GlStateManager.setFogStart(f1 * 0.75F);
                GlStateManager.setFogEnd(f1);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }

            if (this.mc.theWorld.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ))
            {
                GlStateManager.setFogStart(f1 * 0.05F);
                GlStateManager.setFogEnd(Math.min(f1, 192.0F) * 0.5F);
            }
            net.minecraftforge.client.ForgeHooksClient.onFogRender(this, entity, block, partialTicks, p_78468_1_, f1);
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }*/

}
