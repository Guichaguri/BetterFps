package me.guichaguri.betterfps.clones.tileentity;

import me.guichaguri.betterfps.transformers.cloner.CopyMode;
import me.guichaguri.betterfps.transformers.cloner.CopyMode.Mode;
import me.guichaguri.betterfps.transformers.cloner.Named;
import me.guichaguri.betterfps.tweaker.Naming;
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
public class HopperLogic extends TileEntityHopper {
    public IInventory topInventory = null;
    public int topBlockUpdate = 1;
    public boolean canPickupDrops = true;
    public boolean isOnTransferCooldown = false;

    @Named(Naming.M_captureDroppedItems)
    public static boolean captureDroppedItems(IHopper hopper) {
        // This is to keep the same functionality in the Minecart with Hopper
        HopperLogic hopperTE = hopper.getClass() == TileEntityHopper.class ? (HopperLogic)hopper : null;

        IInventory iinventory = hopperTE == null ? getHopperInventory(hopper) : hopperTE.topInventory;

        if(iinventory != null) {
            EnumFacing enumfacing = EnumFacing.DOWN;

            if(isInventoryEmpty(iinventory, enumfacing)) return false;

            if(iinventory instanceof ISidedInventory) {
                ISidedInventory isidedinventory = (ISidedInventory)iinventory;
                int[] aint = isidedinventory.getSlotsForFace(enumfacing);

                for(int i = 0; i < aint.length; ++i) {
                    if(pullItemFromSlot(hopper, iinventory, aint[i], enumfacing)) return true;
                }
            } else {
                int j = iinventory.getSizeInventory();

                for(int k = 0; k < j; ++k) {
                    if(pullItemFromSlot(hopper, iinventory, k, enumfacing)) return true;
                }
            }
        } else if(hopperTE == null || hopperTE.canPickupDrops) {

            for(EntityItem entityitem : func_181556_a(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos())) {
                if(putDropInInventoryAllSlots(hopper, entityitem)) {
                    return true;
                }
            }

        }

        return false;
    }

    @CopyMode(Mode.IGNORE) // Ignore the constructor to prevent an infinite loop
    public HopperLogic() {

    }

    @Override
    public void update() {
        if(this.worldObj != null && !this.worldObj.isRemote) {
            --transferCooldown;
            isOnTransferCooldown = transferCooldown > 0;

            if(!this.isOnTransferCooldown()) {
                this.setTransferCooldown(2); // Let the server breathe
                this.updateHopper();

                if(topBlockUpdate-- <= 0) {
                    checkBlockOnTop();
                    topBlockUpdate = 120;
                }
            }
        }
    }

    @Override
    public boolean isOnTransferCooldown() {
        return isOnTransferCooldown;
    }

    @Override
    @CopyMode(Mode.IGNORE) // TODO
    public ItemStack decrStackSize(int index, int count) {
        return super.decrStackSize(index, count);
    }
    @Override
    @CopyMode(Mode.IGNORE) // TODO
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory[index] = stack;
        if(stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        checkBlockOnTop();
    }

    public void checkBlockOnTop() { // TODO Should be called with neighbour update
        BlockPos topPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        canPickupDrops = !worldObj.getBlockState(topPos).getBlock().isFullCube();
        topInventory = getHopperInventory(this);
    }
}
