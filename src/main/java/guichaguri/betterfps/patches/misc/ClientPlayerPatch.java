package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.UpdateChecker;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
public abstract class ClientPlayerPatch extends EntityPlayerSP {

    public ClientPlayerPatch(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
        super(p_i47378_1_, p_i47378_2_, p_i47378_3_, p_i47378_4_, p_i47378_5_);
    }

    @Copy(Mode.REPLACE)
    @Override
    public void preparePlayerToSpawn() {
        UpdateChecker.showChat(this);
        super.preparePlayerToSpawn();
    }
}
