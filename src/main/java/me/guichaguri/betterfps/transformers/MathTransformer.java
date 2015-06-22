package me.guichaguri.betterfps.transformers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Guilherme Chaguri
 */
public class MathTransformer implements IClassTransformer {

    public static void unloadUselessValues() {
        if(!BetterFpsHelper.ALGORITHM_NAME.equals("tweaker")) {
            try {
                Method m = MathHelper.class.getMethod("bfInit");
                m.setAccessible(true);
                m.invoke(null);
            } catch(Exception ex) {
                // Maybe bfInit does not exist? Can be possible if the algorithm does not have a static block
            }
            try {
                // UNLOAD CACHED UNNECESSARY VALUES
                for(Field f : MathHelper.class.getDeclaredFields()) {
                    String name = f.getName();
                    if((name.equals("SIN_TABLE")) || (name.equals("a"))) { // field_76144_a
                        f.setAccessible(true);
                        f.set(null, null);
                    }
                }
            } catch(Exception ex) {
                // An error ocurred while unloading tweaker sin table? Its not a big problem.
            }
        }
    }

    @Override
    public byte[] transform(String name, String name2, byte[] bytes) {
        if(bytes == null) return null;

        try {
            if((name.equals("net.minecraft.util.MathHelper")) || (name.equals("uv"))) {
                return patchMath(bytes);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return bytes;
    }

    private byte[] patchMath(byte[] bytes) throws Exception {
        String sinOb = "a"; //func_76126_a
        String sinDeob = "sin";
        String cosOb = "b"; //func_76134_b
        String cosDeob = "cos";

        if(BetterFpsHelper.CONFIG == null) {
            BetterFpsHelper.loadConfig();
        }

        if(BetterFpsHelper.ALGORITHM_NAME.equals("tweaker")) {
            LogManager.getLogger("BetterFps").info("Letting Minecraft use " + BetterFpsHelper.displayHelpers.get(BetterFpsHelper.ALGORITHM_NAME));
            return bytes;
        } else {
            LogManager.getLogger("BetterFps").info("Patching Minecraft using " + BetterFpsHelper.displayHelpers.get(BetterFpsHelper.ALGORITHM_NAME));
        }

        ClassReader reader;
        if(BetterFpsHelper.LOC == null) { // Development or tweaker environment?
            reader = new ClassReader("me.guichaguri.betterfps.math." + BetterFpsHelper.ALGORITHM_CLASS);
        } else { // Forge environment
            JarFile jar = new JarFile(BetterFpsHelper.LOC);
            ZipEntry e = jar.getEntry("me/guichaguri/betterfps/math/" + BetterFpsHelper.ALGORITHM_CLASS + ".class");
            reader = new ClassReader(jar.getInputStream(e));
            jar.close();
        }

        ClassNode mathnode = new ClassNode();
        reader.accept(mathnode, 0);


        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        String className = classNode.name;
        String mathClass = mathnode.name;

        patchInit(classNode, mathnode, className, mathClass);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patched = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(method.desc.equals("(F)F")) {

                if((method.name.equals(sinOb)) || (method.name.equals(sinDeob))) {
                    // SIN
                    patchSin(method, mathnode, className, mathClass);
                    patched = true;
                } else if((method.name.equals(cosOb)) || (method.name.equals(cosDeob))) {
                    // COS
                    patchCos(method, mathnode, className, mathClass);
                    patched = true;
                }

            }

        }

        if(!patched) return bytes;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void patchInit(ClassNode classNode, ClassNode math, String name, String oldName) {
        // COPY ALL FIELDS
        for(FieldNode field : math.fields) {
            classNode.fields.add(field);
        }

        // COPY STATIC BLOCK
        MethodNode mathClinit = null;
        for(MethodNode m : math.methods) {
            if(m.name.equals("<clinit>")) {
                mathClinit = m;
                break;
            }
        }
        if(mathClinit != null) {
            MethodNode clinit = null;
            for(MethodNode m : classNode.methods) {
                if(m.name.equals("<clinit>")) {
                    clinit = m;
                    break;
                }
            }
            if(clinit == null) { // Why MathHelper does not have a static block? Well, we'll create one
                clinit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            }
            InsnList list = new InsnList();
            for(AbstractInsnNode node : mathClinit.instructions.toArray()) {
                if(node instanceof FieldInsnNode) {
                    FieldInsnNode field = (FieldInsnNode)node;
                    if(field.owner.equals(oldName)) field.owner = name;
                } else if(node.getOpcode() == Opcodes.RETURN) {
                    continue;
                }
                list.add(node);
            }
            list.add(clinit.instructions);
            clinit.instructions.clear();
            clinit.instructions.add(list);

            classNode.methods.remove(clinit);
            classNode.methods.add(clinit);
        }
    }

    private void patchSin(MethodNode method, ClassNode math, String name, String oldName) {
        method.instructions.clear();

        for(MethodNode original : math.methods) {
            if(original.name.equals("sin")) {
                method.instructions.add(original.instructions);
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node instanceof FieldInsnNode) {
                        FieldInsnNode field = (FieldInsnNode)node;
                        if(field.owner.equals(oldName)) field.owner = name;
                    }
                }
            }
        }
    }

    private void patchCos(MethodNode method, ClassNode math, String name, String oldName) {
        method.instructions.clear();

        for(MethodNode original : math.methods) {
            if(original.name.equals("cos")) {
                method.instructions.add(original.instructions);
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node instanceof FieldInsnNode) {
                        FieldInsnNode field = (FieldInsnNode)node;
                        if(field.owner.equals(oldName)) field.owner = name;
                    }
                }
            }
        }
    }
}