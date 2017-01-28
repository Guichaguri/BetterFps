package guichaguri.betterfps.patches.block;

import guichaguri.betterfps.api.IFastHopper;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
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

    @Copy(Mode.PREPEND)
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if(worldIn.isRemote) return false;

        TileEntity tileentity = worldIn.getTileEntity(pos);

        if(tileentity instanceof IFastHopper) {
            IFastHopper hopper = (IFastHopper)tileentity;
            player.addChatMessage(new TextComponentString(hopper.canPickupItems() ? "YEP" : "NOPE"));
        }
        return false;
    }
}
