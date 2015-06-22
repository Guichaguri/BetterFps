package me.guichaguri.betterfps.tweaker;

/**
 * @author Guilherme Chaguri
 */
public class Naming {

    public static final ObfuscationName C_Minecraft = new ObfuscationName("net.minecraft.client.Minecraft", "bsu");
    public static final ObfuscationName C_KeyBinding = new ObfuscationName("net.minecraft.client.settings.KeyBinding", "bsr");
    public static final ObfuscationName C_GameSettings = new ObfuscationName("net.minecraft.client.settings.GameSettings", "bto");

    public static final ObfuscationName M_startGame = new ObfuscationName("startGame", "aj", "()V");
    public static final ObfuscationName M_onTick = new ObfuscationName("onTick", "a", "(I)V");

    public static class ObfuscationName {
        private String deob;
        private String deobRepl;
        private String ob;
        private String obRepl;

        // Only used for methods and fields
        private String desc;

        public ObfuscationName(String deob, String ob) {
            this.deob = deob;
            this.deobRepl = deob.replaceAll("\\.", "/");
            this.ob = ob;
            this.obRepl = ob.replaceAll("\\.", "/");
        }

        public ObfuscationName(String deob, String ob, String desc) {
            this(deob, ob);
            this.desc = desc;
        }

        // Used to check names
        public boolean is(String name) {
            if(name.equals(deob)) return true;
            if(name.equals(ob)) return true;
            return false;
        }

        // Used to check class names inside ASM
        public boolean isASM(String name) {
            if(name.equals(deobRepl)) return true;
            if(name.equals(obRepl)) return true;
            return false;
        }

        // Used to check names and desc
        public boolean is(String name, String desc) {
            if((this.desc.equals(desc)) && (is(name))) {
                return true;
            }
            return false;
        }

    }

}
