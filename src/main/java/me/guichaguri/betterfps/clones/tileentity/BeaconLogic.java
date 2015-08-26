package me.guichaguri.betterfps.clones.tileentity;

import java.util.Arrays;
import java.util.Iterator;
import me.guichaguri.betterfps.transformers.ClonerTransformer.CopyMode;
import me.guichaguri.betterfps.transformers.ClonerTransformer.CopyMode.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

/**
 * @author Guilherme Chaguri
 */
public class BeaconLogic extends TileEntityBeacon {

    @CopyMode(Mode.IGNORE) // Ignore the constructor prevent an infinite loop
    public BeaconLogic() {

    }

    private int tickCount = 0;
    @Override
    public void update() {
        tickCount--;
        if(tickCount == 100) {
            updateEffects(pos.getX(), pos.getY(), pos.getZ());
        } else if(tickCount <= 0) {
            func_174908_m();
        }
    }

    @Override
    public void func_174908_m() {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(worldObj.isRemote) {
            updateGlassLayers(x, y, z);
        } else {
            updateActivation(x, y, z);
        }
        updateLevels(x, y, z);
        updateEffects(x, y, z);
        tickCount = 200;
    }

    private void updateEffects(int x, int y, int z) {
        if((isComplete) && (levels > 0) && (!worldObj.isRemote) && (primaryEffect > 0)) {
            int radius = (levels + 1) * 10;
            byte effectLevel = 0;
            if((levels >= 4) && (primaryEffect == secondaryEffect)) {
                effectLevel = 1;
            }
            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
            box = box.expand(radius, radius, radius).addCoord(0.0D, worldObj.getHeight(), 0.0D);
            Iterator iterator = worldObj.getEntitiesWithinAABB(EntityPlayer.class, box).iterator();

            boolean hasSecondaryEffect = (levels >= 4) && (primaryEffect != secondaryEffect) && (secondaryEffect > 0);

            while(iterator.hasNext()) {
                EntityPlayer player = (EntityPlayer)iterator.next();
                player.addPotionEffect(new PotionEffect(primaryEffect, 180, effectLevel, true, true));
                if(hasSecondaryEffect) {
                    player.addPotionEffect(new PotionEffect(secondaryEffect, 180, 0, true, true));
                }
            }
        }
    }


    private void updateGlassLayers(int x, int y, int z) {
        // Checks if the beacon should be active and searches for stained glass to color the beam.
        // Should be only called client-side
        isComplete = true;
        field_174909_f.clear();
        BeamSegment beam = new BeamSegment(EntitySheep.func_175513_a(EnumDyeColor.WHITE));
        float[] oldColor = null;
        field_174909_f.add(beam);
        int height = worldObj.getActualHeight();
        for(int blockY = y + 1; blockY < height; blockY++) {
            BlockPos pos = new BlockPos(x, blockY, z);
            IBlockState state = this.worldObj.getBlockState(pos);
            Block b = state.getBlock();
            float[] color;
            if(b == Blocks.stained_glass) {
                color = EntitySheep.func_175513_a((EnumDyeColor)state.getValue(BlockStainedGlass.COLOR));
            } else if(b == Blocks.stained_glass_pane) {
                color = EntitySheep.func_175513_a((EnumDyeColor)state.getValue(BlockStainedGlassPane.COLOR));
            } else {
                if(b.getLightOpacity() >= 15) {
                    isComplete = false;
                    field_174909_f.clear();
                    break;
                }
                beam.func_177262_a();
                continue;
            }

            if(oldColor != null) {
                color = new float[]{(oldColor[0] + color[0]) / 2.0F, (oldColor[1] + color[1]) / 2.0F, (oldColor[2] + color[2]) / 2.0F};
            }
            if(Arrays.equals(color, oldColor)) {
                beam.func_177262_a();
            } else {
                beam = new BeamSegment(color);
                field_174909_f.add(beam);
                oldColor = color;
            }
        }
    }

    private void updateActivation(int x, int y, int z) {
        // Checks if the beacon should be activate
        // Should be called only server-side. updateGlassLayers do the trick on the client
        isComplete = true;
        int height = worldObj.getActualHeight();
        for(int blockY = y + 1; blockY < height; blockY++) {
            BlockPos pos = new BlockPos(x, blockY, z);
            IBlockState state = this.worldObj.getBlockState(pos);
            Block b = state.getBlock();
            if(b.getLightOpacity() >= 15) {
                isComplete = false;
                break;
            }
        }
    }

    private void updateLevels(int x, int y, int z) {
        // Checks if the beacon should be active and how many levels it should have.
        boolean isClient = worldObj.isRemote;
        int levelsOld = levels;
        lvlLoop: for(int lvl = 1; lvl <= 4; lvl++) {
            levels = lvl;
            int blockY = y - lvl;
            if(blockY < 0) break;

            for(int blockX = x - lvl; blockX <= x + lvl; blockX++) {
                for(int blockZ = z - lvl; blockZ <= z + lvl; blockZ++) {
                    BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
                    Block block = worldObj.getBlockState(blockPos).getBlock();
                    if(!block.isBeaconBase(worldObj, blockPos, pos)) {
                        levels--;
                        break lvlLoop;
                    }
                }
            }

            if(isClient) break;
        }
        if(levels == 0) {
            this.isComplete = false;
        }

        if((!isClient) && (levels == 4) && (levelsOld < levels)) {
            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x, y - 4, z).expand(10.0, 5.0, 10.0);
            Iterator iterator = worldObj.getEntitiesWithinAABB(EntityPlayer.class, box).iterator();
            while(iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();
                entityplayer.triggerAchievement(AchievementList.fullBeacon);
            }
        }
    }
}
