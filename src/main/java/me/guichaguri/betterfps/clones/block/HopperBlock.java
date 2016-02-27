package me.guichaguri.betterfps.clones.block;

import me.guichaguri.betterfps.clones.tileentity.HopperLogic;
import me.guichaguri.betterfps.transformers.cloner.CopyMode;
import me.guichaguri.betterfps.transformers.cloner.CopyMode.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
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
    @CopyMode(Mode.APPEND)
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        TileEntity te = worldIn.getTileEntity(pos);
        if(te != null) {
            TileEntityHopper hopper = (TileEntityHopper)te;
            ((HopperLogic)hopper).checkBlockOnTop();
            // This is casted to HopperLogic just to make this class compilable. It will be removed by the ASM
        }
    }

}
