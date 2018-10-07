package guichaguri.betterfps.patches.block;

import guichaguri.betterfps.api.IBlock;
import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import java.util.Arrays;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * @author Guilherme Chaguri
 */
@Condition(Conditions.FAST_BEACON)
public abstract class FastBeacon extends TileEntityBeacon {

    @Copy
    private int tickCount = 0;

    @Copy(Mode.REPLACE)
    @Override
    public void update() {
        tickCount--;
        if(tickCount == 100) {
            addEffectsToPlayers();
        } else if(tickCount <= 0) {
            updateBeacon();
            tickCount = 200;
        }
    }

    /**
     * Updates the beacon structure
     */
    @Copy(Mode.REPLACE)
    @Override
    public void updateSegmentColors() {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if(world.isRemote) {
            updateGlassLayers(x, y, z);
        } else {
            updateActivation(x, y, z);
        }
        updateLevels(x, y, z);
    }

    /**
     * Updates player effects
     */
    @Copy(Mode.REPLACE)
    @Override
    public void addEffectsToPlayers() {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if(isComplete && levels > 0 && !world.isRemote && primaryEffect != null) {

            int effectTicks = (9 + levels * 2) * 20;
            int radius = (levels + 1) * 10;
            byte effectLevel = 0;
            boolean hasSecondaryEffect = false;

            if(levels >= 4) {
                if(primaryEffect == secondaryEffect) {
                    effectLevel = 1;
                } else {
                    hasSecondaryEffect = secondaryEffect != null;
                }
            }

            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
            box = box.grow(radius, radius, radius).expand(0.0D, world.getHeight(), 0.0D);

            for(EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, box)) {
                player.addPotionEffect(new PotionEffect(primaryEffect, effectTicks, effectLevel, true, true));

                if(hasSecondaryEffect) {
                    player.addPotionEffect(new PotionEffect(secondaryEffect, effectTicks, 0, true, true));
                }
            }
        }
    }

    /**
     * Checks if the beacon should be active and searches for stained glass to color the beam
     * Should be only called client-side. {@link #updateActivation} should be used on the server
     */
    @Copy
    private void updateGlassLayers(int x, int y, int z) {
        BeamSegment beam = new BeamSegment(EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
        float[] oldColor = null;

        beamSegments.clear();
        beamSegments.add(beam);

        isComplete = true;

        for(int blockY = y + 1; blockY < world.getActualHeight(); blockY++) {

            BlockPos blockPos = new BlockPos(x, blockY, z);
            IBlockState state = world.getBlockState(blockPos);
            Block b = state.getBlock();
            float[] color = null;

            if(b == Blocks.STAINED_GLASS) {
                color = EntitySheep.getDyeRgb(state.getValue(BlockStainedGlass.COLOR));
            } else if(b == Blocks.STAINED_GLASS_PANE) {
                color = EntitySheep.getDyeRgb(state.getValue(BlockStainedGlassPane.COLOR));
            } else {
                if(b != Blocks.BEDROCK && state.getLightOpacity() >= 15) {
                    isComplete = false;
                    beamSegments.clear();
                    break;
                }

                // Forge Compatibility
                float[] customColor = ((IBlock)b).getBeaconColorMultiplier(state, world, blockPos, pos);
                if(customColor != null) color = customColor;
            }

            if(color == null) {
                beam.incrementHeight();
                continue;
            }

            if(oldColor != null) {
                color = new float[]{
                        (oldColor[0] + color[0]) / 2,
                        (oldColor[1] + color[1]) / 2,
                        (oldColor[2] + color[2]) / 2
                };
            }

            if(Arrays.equals(color, oldColor)) {
                beam.incrementHeight();
            } else {
                beam = new BeamSegment(color);
                beamSegments.add(beam);
                oldColor = color;
            }
        }
    }

    /**
     * Checks if the beacon should be active
     * Should be called only server-side. {@link #updateGlassLayers} should be used on the client
     */
    @Copy
    private void updateActivation(int x, int y, int z) {
        isComplete = true;

        for(int blockY = y + 1; blockY < world.getActualHeight(); blockY++) {

            BlockPos pos = new BlockPos(x, blockY, z);
            IBlockState state = world.getBlockState(pos);
            Block b = state.getBlock();

            if(b != Blocks.BEDROCK && state.getLightOpacity() >= 15) {
                isComplete = false;
                break;
            }
        }
    }

    /**
     * Checks if the beacon should be active and how many levels it should have
     */
    @Copy
    private void updateLevels(int x, int y, int z) {
        boolean isClient = world.isRemote;
        int levelsOld = levels;

        lvlLoop: for(int lvl = 1; lvl <= 4; lvl++) {
            levels = lvl;
            int blockY = y - lvl;
            if(blockY < 0) break;

            for(int blockX = x - lvl; blockX <= x + lvl; blockX++) {
                for(int blockZ = z - lvl; blockZ <= z + lvl; blockZ++) {

                    BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
                    Block block = world.getBlockState(blockPos).getBlock();

                    // Forge Compatibility
                    if(!((IBlock)block).isBeaconBase(world, blockPos, pos)) {
                        levels--;
                        break lvlLoop;
                    }

                }
            }

            // If it's a client, let's ignore all other layers
            if(isClient) break;
        }
        if(levels == 0) {
            isComplete = false;
        }

        if(!isClient && levelsOld < levels) {
            // Give the full beacon advancement
            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x, y - 4, z).expand(10.0, 5.0, 10.0);
            for(EntityPlayerMP player : world.getEntitiesWithinAABB(EntityPlayerMP.class, box)) {
                CriteriaTriggers.CONSTRUCT_BEACON.trigger(player, this);
            }
        }
    }

}
