package guichaguri.betterfps.api;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
public interface IBlock {

    /**
     * Method added by Forge. Reimplemented for compatibility with it.
     */
    boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon);

    /**
     * Method added by Forge. Reimplemented for compatibility with it.
     */
    @Nullable
    float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos);

}
