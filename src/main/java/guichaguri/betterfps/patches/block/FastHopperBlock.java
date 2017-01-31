package guichaguri.betterfps.patches.block;

import guichaguri.betterfps.api.IFastHopper;
import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
@Condition(Conditions.FAST_HOPPER)
public abstract class FastHopperBlock extends Block {

    protected FastHopperBlock(Material materialIn) {
        super(materialIn);
    }

    @Copy(Mode.APPEND)
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos pos2) {
        TileEntity te = worldIn.getTileEntity(pos);
        if(te != null && te instanceof IFastHopper) {
            ((IFastHopper)te).updateFastHopper();
        }
    }

}
