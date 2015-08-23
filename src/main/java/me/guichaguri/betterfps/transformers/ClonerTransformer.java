package me.guichaguri.betterfps.transformers;

import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.tweaker.Naming.ObfuscationName;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

/**
 * This class will clone .class methods/fields to the real class
 * @author Guilherme Chaguri
 */
public class ClonerTransformer implements IClassTransformer {
    private final String fileName;
    private final ObfuscationName className;
    private final String[] methodsToClone;
    private final String[] fieldsToClone;

    public ClonerTransformer(String fileName, ObfuscationName className, String[] methodsToClone, String[] fieldsToClone) {
        this.fileName = fileName;
        this.className = className;
        this.methodsToClone = methodsToClone;
        this.fieldsToClone = fieldsToClone;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(!className.is(name)) return bytes;

        try {

            ClassReader reader;
            if(BetterFpsHelper.LOC == null) { // Development or vanilla environment?
                reader = new ClassReader(fileName);
            } else { // Forge environment
                JarFile jar = new JarFile(BetterFpsHelper.LOC);
                ZipEntry e = jar.getEntry(fileName + ".class");
                reader = new ClassReader(jar.getInputStream(e));
                jar.close();
            }

            ClassNode cloneNode = new ClassNode();
            reader.accept(cloneNode, 0);

            ClassNode node = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(node, 0);

            boolean patched = false;

            for(FieldNode field : cloneNode.fields) {
                for(String fieldName : fieldsToClone) {
                    if(fieldName.equals(field.name)) {
                        addOrReplace(field, node);
                        patched = true;
                        break;
                    }
                }
            }

            for(MethodNode method : cloneNode.methods) {
                for(String methodName : methodsToClone) {
                    if(methodName.equals(method.name)) {
                        addOrReplace(method, cloneNode, node);
                        patched = true;
                        break;
                    }
                }
            }

            if(!patched) return bytes;

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return bytes;
    }

    private void addOrReplace(FieldNode e, ClassNode node) {
        for(int i = 0; i < node.fields.size(); i++) {
            FieldNode field = node.fields.get(i);
            if((field.name.equals(e.name)) && (field.desc.equals(e.desc))) {
                node.fields.remove(field);
                break;
            }
        }
        node.fields.add(e);
    }

    private void addOrReplace(MethodNode e, ClassNode old, ClassNode node) {
        String oldName = old.name;
        String name = node.name;

        InsnList list = new InsnList();
        for(AbstractInsnNode ins : e.instructions.toArray()) {

            if(ins instanceof FieldInsnNode) {
                FieldInsnNode field = (FieldInsnNode)ins;
                if(field.owner.equals(oldName)) field.owner = name;
            } else if(ins instanceof MethodInsnNode) {
                MethodInsnNode m = (MethodInsnNode)ins;
                if(m.owner.equals(oldName)) m.owner = name;
            }

            list.add(ins);
        }

        for(int i = 0; i < node.methods.size(); i++) {
            MethodNode method = node.methods.get(i);
            if((method.name.equals(e.name)) && (method.desc.equals(e.desc))) {

                method.instructions.clear();
                method.instructions.add(list);

                return;

            }
        }

        MethodNode m = new MethodNode(e.access, e.name, e.desc, e.signature, e.exceptions.toArray(new String[e.exceptions.size()]));
        m.instructions = list;

        node.methods.add(e);
    }

}
