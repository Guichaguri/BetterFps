package me.guichaguri.betterfps.transformers;

import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.guichaguri.betterfps.BetterFps;
import me.guichaguri.betterfps.BetterFpsConfig;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
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

    @Override
    public byte[] transform(String name, String name2, byte[] bytes) {
        if(bytes == null) return new byte[0];

        if(Naming.C_MathHelper.is(name)) {
            try {
                return patchMath(bytes);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return bytes;
    }

    private byte[] patchMath(byte[] bytes) throws Exception {

        BetterFpsConfig config = BetterFpsConfig.getConfig();
        if(config == null) {
            BetterFpsHelper.loadConfig();
            config = BetterFpsConfig.getConfig();
        }

        String algorithmClass = BetterFpsHelper.helpers.get(config.algorithm);
        if(algorithmClass == null) {
            BetterFps.log.error("The algorithm is invalid. We're going to use Vanilla Algorithm instead.");
            config.algorithm = "vanilla";
        }

        if(config.algorithm.equals("vanilla")) {
            BetterFps.log.info("Letting Minecraft use " + BetterFpsHelper.displayHelpers.get(config.algorithm));
            return bytes;
        } else {
            BetterFps.log.info("Patching Minecraft using " + BetterFpsHelper.displayHelpers.get(config.algorithm));
        }

        ClassReader reader;
        if(BetterFpsHelper.LOC == null) { // Development or vanilla environment?
            reader = new ClassReader("me.guichaguri.betterfps.math." + algorithmClass);
        } else { // Forge environment
            JarFile jar = new JarFile(BetterFpsHelper.LOC);
            ZipEntry e = jar.getEntry("me/guichaguri/betterfps/math/" + algorithmClass + ".class");
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

            if(Naming.M_sin.is(method.name, method.desc)) {
                // SIN
                patchSin(method, mathnode, className, mathClass);
                patched = true;
            } else if(Naming.M_cos.is(method.name, method.desc)) {
                // COS
                patchCos(method, mathnode, className, mathClass);
                patched = true;
            } /*else if(Naming.M_StaticBlock.is(method.name, method.desc)) {

                InsnList list = new InsnList();
                for(int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode node = list.get(i);
                    if(node instanceof FieldInsnNode) {
                        FieldInsnNode field = (FieldInsnNode)node;
                        if(field.owner.equals(className) && Naming.F_SIN_TABLE.is(field.name, field.desc)) {

                        }
                    }
                }

            }*/ //TODO finish

        }

        if(patched) {

            Iterator<FieldNode> fields = classNode.fields.iterator();
            while(fields.hasNext()) {
                FieldNode field = fields.next();
                if(Naming.F_SIN_TABLE.is(field.name, field.desc)) { // Remove this unused array to get less ram usage
                    //fields.remove();
                    // TODO finish
                    break;
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();

        }

        return bytes;
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
