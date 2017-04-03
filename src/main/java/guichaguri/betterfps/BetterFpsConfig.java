package guichaguri.betterfps;

import com.google.gson.annotations.SerializedName;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsConfig {

    public AlgorithmType algorithm = AlgorithmType.RIVENS_HALF;

    public boolean updateChecker = true;

    public boolean preallocateMemory = false;

    public boolean fog = true;

    public boolean beaconBeam = true;

    public boolean fastHopper = true;

    public boolean fastBeacon = true;

    public boolean fastSearch = true;

    public boolean asyncSearch = true;

    public enum AlgorithmType {
        @SerializedName("vanilla") VANILLA,
        @SerializedName("java") JAVA,
        @SerializedName("libgdx") LIBGDX,
        @SerializedName("random") RANDOM,
        @SerializedName("rivens-full") RIVENS_FULL,
        @SerializedName("rivens-half") RIVENS_HALF,
        @SerializedName("rivens") RIVENS,
        @SerializedName("taylors") TAYLORS;
    }

}
