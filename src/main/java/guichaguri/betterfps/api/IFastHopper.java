package guichaguri.betterfps.api;

import net.minecraft.tileentity.IHopper;

/**
 * @author Guilherme Chaguri
 */
public interface IFastHopper extends IHopper {

    /**
     * Whether this hopper is picking up dropped items
     */
    boolean canPickupItems();

    /**
     * Updates {@link #canPickupItems}
     */
    void updateFastHopper();

}
