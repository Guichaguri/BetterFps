package me.guichaguri.betterfps.installer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import me.guichaguri.betterfps.math.JavaMath;
import me.guichaguri.betterfps.math.LibGDXMath;
import me.guichaguri.betterfps.math.RivensFullMath;
import me.guichaguri.betterfps.math.RivensHalfMath;
import me.guichaguri.betterfps.math.RivensMath;
import me.guichaguri.betterfps.math.TaylorMath;

/**
 * @author Guilherme Chaguri
 */
public class InstanceInstaller {

    private static final Class[] algorithms = new Class[]{
            JavaMath.class, TaylorMath.class, LibGDXMath.class,
            RivensMath.class, RivensFullMath.class, RivensHalfMath.class
    };

    public static HashMap<String, Float> testAlgorithms() {
        HashMap<String, Float> results = new HashMap<String, Float>();
        for(Class algorithm : algorithms) {
            try {
                Method sin = algorithm.getDeclaredMethod("sin", float.class);
                Method cos = algorithm.getDeclaredMethod("cos", float.class);

                long startTime = System.nanoTime();
                for(int i = 0; i < 360 * 10; i++) {
                    float angle = (float)i / 10F;
                    sin.invoke(null, angle);
                    cos.invoke(null, angle);
                }
                long endTime = System.nanoTime();
                float result = (float)(endTime - startTime) / 1000000;
                results.put(algorithm.getSimpleName(), result);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return results;
    }

    public static void install(File folder) {

    }

    public static File getSuggestedMinecraftFolder() {
        // Adapted from Forge installer
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase();
        if((osType.contains("win")) && (System.getenv("APPDATA") != null)) {
            return new File(System.getenv("APPDATA"), ".minecraft");
        } else if(osType.contains("mac")) {
            return new File(userHomeDir, "Library/Application Support/minecraft");
        } else {
            return new File(userHomeDir, ".minecraft");
        }
    }

    public InstanceInstaller() {

    }

}
