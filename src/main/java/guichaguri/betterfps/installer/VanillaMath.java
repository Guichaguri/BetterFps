package guichaguri.betterfps.installer;

/**
 * This is a copy of the vanilla algorithm. Only used for benchmarks
 */
public class VanillaMath {

    private static final float[] SIN_TABLE = new float[65536];

    static {
        for(int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
        }
    }

    public static float sin(float value) {
        return SIN_TABLE[(int)(value * 10430.378F) & 65535];
    }

    public static float cos(float value) {
        return SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
    }

}
