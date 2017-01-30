package guichaguri.betterfps.transformers;

import guichaguri.betterfps.BetterFpsConfig;
import guichaguri.betterfps.BetterFpsConfig.AlgorithmType;
import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.tweaker.BetterFpsTweaker;
import guichaguri.betterfps.tweaker.Mappings;
import java.io.InputStream;
import java.util.LinkedHashMap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Guilherme Chaguri
 */
public class MathTransformer implements IClassTransformer {

    // Config Name, Class Name
    private static final LinkedHashMap<String, String> algorithmClasses = new LinkedHashMap<String, String>();

    static {
        algorithmClasses.put("vanilla", "guichaguri/betterfps/math/VanillaMath");
        algorithmClasses.put("rivens", "guichaguri/betterfps/math/RivensMath");
        algorithmClasses.put("taylors", "guichaguri/betterfps/math/TaylorMath");
        algorithmClasses.put("libgdx", "guichaguri/betterfps/math/LibGDXMath");
        algorithmClasses.put("rivens-full", "guichaguri/betterfps/math/RivensFullMath");
        algorithmClasses.put("rivens-half", "guichaguri/betterfps/math/RivensHalfMath");
        algorithmClasses.put("java", "guichaguri/betterfps/math/JavaMath");
        algorithmClasses.put("random", "guichaguri/betterfps/math/RandomMath");
    }

    private final String METHOD_SIN = "sin";
    private final String METHOD_COS = "cos";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        if(Mappings.C_MathHelper.is(name)) {
            try {
                return patchMath(bytes);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return bytes;
    }

    private byte[] patchMath(byte[] bytes) throws Exception {

        BetterFpsConfig config = BetterFpsHelper.getConfig();

        String algorithmClass = algorithmClasses.get(config.algorithm);
        if(algorithmClass == null) {
            BetterFpsHelper.LOG.error("The algorithm is invalid. We're going to use Vanilla Algorithm instead.");
            config.algorithm = AlgorithmType.VANILLA;
            return bytes;
        }

        if(config.algorithm == AlgorithmType.VANILLA) {
            BetterFpsHelper.LOG.info("No algorithm for patching, Vanilla Algorithm will be used");
            return bytes;
        } else {
            BetterFpsHelper.LOG.info("Patching math utils with \"{}\" algorithm", config.algorithm);
        }

        InputStream in = BetterFpsTweaker.getResourceStream(algorithmClass + ".class");
        if(in == null) return bytes;

        ClassNode mathClass = ASMUtils.readClass(IOUtils.toByteArray(in), 0);

        ClassNode classNode = ASMUtils.readClass(bytes, 0);

        MethodNode init = ASMUtils.findMethod(classNode, "<clinit>", "()V"); // Static Constructor
        MethodNode sin = ASMUtils.findMethod(classNode, Mappings.M_sin);
        MethodNode cos = ASMUtils.findMethod(classNode, Mappings.M_cos);

        boolean patchedSin = false;
        boolean patchedCos = false;

        for(FieldNode f : mathClass.fields) {
            ASMUtils.copyField(mathClass, classNode, f, true);
        }

        for(MethodNode m : mathClass.methods) {
            if(m.name.equals(METHOD_SIN)) {
                ASMUtils.copyMethod(mathClass, classNode, m, sin, true);
                patchedSin = true;
            } else if(m.name.equals(METHOD_COS)) {
                ASMUtils.copyMethod(mathClass, classNode, m, cos, true);
                patchedCos = true;
            } else if(m.name.equals("<clinit>")) {
                m.name = "init";
                ASMUtils.appendMethod(mathClass, classNode, m, init);
            } else {
                ASMUtils.copyMethod(mathClass, classNode, m, true);
            }
        }

        FieldNode sinTable = ASMUtils.findField(classNode, Mappings.F_SIN_TABLE);

        if(patchedSin && patchedCos && sinTable != null) {
            classNode.fields.remove(sinTable);

            InsnList inst = init.instructions;
            LabelNode currentLabel = null;
            for(int i = 0; i < inst.size(); i++) {
                AbstractInsnNode node = inst.get(i);
                if(node instanceof LabelNode) {
                    currentLabel = (LabelNode)node;
                } else if(node instanceof FieldInsnNode && currentLabel != null) {
                    FieldInsnNode f = (FieldInsnNode)node;
                    if(classNode.name.equals(f.owner) && sinTable.name.equals(f.name) && sinTable.desc.equals(f.desc)) {
                        i = inst.indexOf(currentLabel);
                        ASMUtils.removeLabelSection(inst, currentLabel);
                    }
                }
            }
        }

        return ASMUtils.writeClass(classNode, 0);
    }

}
