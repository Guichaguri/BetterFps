package guichaguri.betterfps.patches.misc;

import guichaguri.betterfps.UpdateChecker;
import guichaguri.betterfps.transformers.patcher.annotations.Copy;
import guichaguri.betterfps.transformers.patcher.annotations.Copy.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

/**
 * @author Guilherme Chaguri
 */
public abstract class ClientPlayerPatch extends EntityPlayerSP {

    public ClientPlayerPatch(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatisticsManager statFile) {
        super(mcIn, worldIn, netHandler, statFile);
    }

    @Copy(Mode.PREPEND)
    @Override
    public void preparePlayerToSpawn() {
        UpdateChecker.showChat(this);
    }
}
