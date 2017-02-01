package guichaguri.betterfps.patches.block;

import guichaguri.betterfps.api.IFastHopper;
import guichaguri.betterfps.transformers.Conditions;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import guichaguri.betterfps.transformers.annotations.Patcher;
import guichaguri.betterfps.transformers.annotations.Reference;
import guichaguri.betterfps.patchers.FastHopperPatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
@Patcher(FastHopperPatcher.class)
@Condition(Conditions.FAST_HOPPER)
public abstract class FastHopper extends TileEntityHopper implements IFastHopper {

    // TODO Check if any further improvements can be done in the hopper
    // I used to cache the inventory on top and the inventory connected to,
    // but thats kinda useless with Forge's capabilities
    // We can leave as it is, cache only the inventory on top (only affects vanilla)
    // or cache and patch Forge's hooks and vanilla inventories

    /**
     * Checks whether the hopper can pickup dropped items
     */
    @Reference(FastHopperPatcher.PICKUP_ITEMS)
    public static boolean canPickupItems(IHopper hopper) {
        return !(hopper instanceof IFastHopper) || ((IFastHopper)hopper).canPickupItems();
    }

    @Copy
    private boolean isPickingUpItems;
    @Copy
    private int fastHopperUpdate;

    @Override
    @Copy
    public boolean canPickupItems() {
        return isPickingUpItems;
    }

    @Override
    @Copy
    public void updateFastHopper() {
        World world = getWorld();
        if(world.isRemote) return;
        IBlockState state = world.getBlockState(new BlockPos(getXPos(), getYPos() + 1, getZPos()));
        isPickingUpItems = !state.isOpaqueCube() && !state.isFullCube();
    }

    @Override
    @Copy(Mode.PREPEND)
    public boolean updateHopper() {
        if(fastHopperUpdate-- <= 0) {
            updateFastHopper();
            fastHopperUpdate = 600; // 30 seconds
        }
        return false;
    }

    @Override
    @Copy(Mode.REPLACE)
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        updateFastHopper();
    }
}
