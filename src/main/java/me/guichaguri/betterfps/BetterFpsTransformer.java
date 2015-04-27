package me.guichaguri.betterfps;

import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsTransformer implements IClassTransformer {
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

        BetterMathHelper.loadConfig();
        if(BetterMathHelper.ALGORITHM_NAME.equals("vanilla")) {
            LogManager.getLogger("BetterFps").info("Letting Minecraft use " + BetterMathHelper.displayHelpers.get(BetterMathHelper.ALGORITHM_NAME));
            return bytes;
        } else {
            LogManager.getLogger("BetterFps").info("Patching Minecraft using " + BetterMathHelper.displayHelpers.get(BetterMathHelper.ALGORITHM_NAME));
        }

        ClassReader reader;
        if(BetterFps.LOC == null) {
            reader = new ClassReader("me.guichaguri.betterfps.math." + BetterMathHelper.ALGORITHM_CLASS);
        } else {
            JarFile jar = new JarFile(BetterFps.LOC);
            ZipEntry e = jar.getEntry("me/guichaguri/betterfps/math/" + BetterMathHelper.ALGORITHM_CLASS + ".class");
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
        for(MethodNode m : math.methods) {
            if(m.name.equals("<clinit>")) {
                MethodNode method = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "bfInit", "()V", null, null);
                method.instructions.add(m.instructions);
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node instanceof FieldInsnNode) {
                        FieldInsnNode field = (FieldInsnNode)node;
                        if(field.owner.equals(oldName)) field.owner = name;
                    }
                }
                classNode.methods.add(method);
            }
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

        //method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 0));
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/guichaguri/betterfps/BetterMathHelper", "sin", "(F)F", false));
        //method.instructions.add(new InsnNode(Opcodes.FRETURN));
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

        //method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 0));
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/guichaguri/betterfps/BetterMathHelper", "cos", "(F)F", false));
        //method.instructions.add(new InsnNode(Opcodes.FRETURN));
    }
}
