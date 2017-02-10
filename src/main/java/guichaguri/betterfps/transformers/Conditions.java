package guichaguri.betterfps.transformers;

import guichaguri.betterfps.BetterFpsConfig;
import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.transformers.annotations.Condition;
import guichaguri.betterfps.tweaker.Mappings;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * @author Guilherme Chaguri
 */
public class Conditions {

    protected static final List<Mappings> patched = new ArrayList<Mappings>();

    public static final String FAST_HOPPER = "fastHopper";
    public static final String FAST_BEACON = "fastBeacon";
    public static final String FAST_SEARCH = "fastSearch";
    public static final String FAST_BEAM_RENDER = "fastBeaconBeamRender";
    public static final String FOG_DISABLED = "fogDisabled";

    /**
     * Checks whether the condition identifier is met
     */
    public static boolean shouldPatch(String condition) {
        BetterFpsConfig config = BetterFpsHelper.getConfig();

        if(condition.equals(FAST_HOPPER)) {
            return config.fastHopper;
        } else if(condition.equals(FAST_BEACON)) {
            return config.fastBeacon;
        } else if(condition.equals(FAST_SEARCH)) {
            return config.fastSearch;
        } else if(condition.equals(FAST_BEAM_RENDER)) {
            return !config.beaconBeam;
        } else if(condition.equals(FOG_DISABLED)) {
            return !config.fog;
        }

        return true;
    }

    /**
     * Checks whether the {@link Condition} annotation has its condition met
     */
    public static boolean shouldPatch(List<AnnotationNode> annotations) {
        AnnotationNode condition = ASMUtils.getAnnotation(annotations, Condition.class);
        if(condition != null) {
            String id = ASMUtils.getAnnotationValue(condition, "value", String.class);
            return id == null || shouldPatch(id);
        }
        return true;
    }

    public static boolean isPatched(Mappings name) {
        return patched.contains(name);
    }

}
