package me.guichaguri.betterfps.clones.tileentity;

import java.lang.ref.WeakReference;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author Guilherme Chaguri
 */
public class HopperLogic extends TileEntityHopper { // TODO
    public WeakReference<IInventory> topInventory = new WeakReference<IInventory>(null);
    public boolean canPickupDrops = true;

    public static boolean func_145891_a(IHopper hopper) {
        HopperLogic hopperTE = hopper instanceof HopperLogic ? (HopperLogic)hopper : null;

        IInventory iinventory;
        if(hopperTE != null) {
            iinventory = hopperTE.topInventory.get();
            if(iinventory == null) {
                iinventory = func_145884_b(hopper);
                hopperTE.topInventory = new WeakReference<IInventory>(iinventory);
            }
        } else {
            iinventory = func_145884_b(hopper);
        }

        if(iinventory != null) {
            EnumFacing enumfacing = EnumFacing.DOWN;

            if(func_174917_b(iinventory, enumfacing)) return false;

            if(iinventory instanceof ISidedInventory) {
                ISidedInventory isidedinventory = (ISidedInventory)iinventory;
                int[] aint = isidedinventory.getSlotsForFace(enumfacing);

                for(int i = 0; i < aint.length; ++i) {
                    if(func_174915_a(hopper, iinventory, aint[i], enumfacing)) return true;
                }
            } else {
                int j = iinventory.getSizeInventory();

                for(int k = 0; k < j; ++k) {
                    if(func_174915_a(hopper, iinventory, k, enumfacing)) return true;
                }
            }
        } else if(hopperTE == null || hopperTE.canPickupDrops) {
            EntityItem entityitem = func_145897_a(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());

            if(entityitem != null) {
                return func_145898_a(hopper, entityitem);
            }
        }

        return false;
    }

    @Override
    public void setTransferCooldown(int ticks) {
        if(ticks == 0) {
            ticks = 4; // Let the server breathe
            checkBlockOnTop();
        }
        this.transferCooldown = ticks;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return super.decrStackSize(index, count);
    }
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
    }

    public void checkBlockOnTop() { // TODO Should be called with neighbour update
        canPickupDrops = worldObj.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock().isSolidFullCube();
    }
}
