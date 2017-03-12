package guichaguri.betterfps.patches.block;

import guichaguri.betterfps.api.IBlock;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
public abstract class BlockPatch extends Block implements IBlock {
    public BlockPatch(Material m) {
        super(m);
    }

    @Copy(Mode.COPY)
    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return this == Blocks.EMERALD_BLOCK || this == Blocks.GOLD_BLOCK || this == Blocks.DIAMOND_BLOCK || this == Blocks.IRON_BLOCK;
    }

    @Copy(Mode.COPY)
    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
        return null;
    }
}
