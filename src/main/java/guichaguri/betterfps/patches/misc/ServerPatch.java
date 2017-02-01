package guichaguri.betterfps.patches.misc;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import guichaguri.betterfps.UpdateChecker;
import guichaguri.betterfps.transformers.annotations.Copy;
import guichaguri.betterfps.transformers.annotations.Copy.Mode;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;

/**
 * @author Guilherme Chaguri
 */
public abstract class ServerPatch extends MinecraftServer {
    public ServerPatch(File anvil, Proxy proxy, DataFixer fixer, YggdrasilAuthenticationService auth, MinecraftSessionService session, GameProfileRepository profiles, PlayerProfileCache offlineProfiles) {
        super(anvil, proxy, fixer, auth, session, profiles, offlineProfiles);
    }

    @Copy(Mode.APPEND)
    @Override
    public boolean init() throws IOException {
        UpdateChecker.check();
        return true;
    }
}
