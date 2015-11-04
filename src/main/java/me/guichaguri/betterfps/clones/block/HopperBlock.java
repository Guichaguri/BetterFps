package me.guichaguri.betterfps.clones.block;

import me.guichaguri.betterfps.clones.tileentity.HopperLogic;
import me.guichaguri.betterfps.transformers.ClonerTransformer.CopyMode;
import me.guichaguri.betterfps.transformers.ClonerTransformer.CopyMode.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
public class HopperBlock extends BlockHopper {

    @CopyMode(Mode.IGNORE) // Ignore the constructor to prevent an infinite loop
    public HopperBlock() {

    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        updateState(worldIn, pos, state);
        TileEntity te = worldIn.getTileEntity(pos);
        if(te != null) ((HopperLogic)te).checkBlockOnTop();
    }

    public void updateState(World worldIn, BlockPos pos, IBlockState state) {
        boolean flag = !worldIn.isBlockPowered(pos);

        if(flag != ((Boolean)state.getValue(ENABLED)).booleanValue()) {
            worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 4);
        }
    }

}
