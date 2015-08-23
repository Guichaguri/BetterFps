package me.guichaguri.betterfps.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * @author Guilherme Chaguri
 */
public class ClassTransformer implements IClassTransformer {

    public final IClassTransformer BEACON;

    public ClassTransformer() {
        BEACON = null;//new ClonerTransformer("TileEntityBeacon");
    }

    @Override
    public byte[] transform(String s, String s1, byte[] bytes) {
        return new byte[0];
    }
}
