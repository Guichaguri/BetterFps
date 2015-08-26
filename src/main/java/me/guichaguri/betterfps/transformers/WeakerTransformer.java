package me.guichaguri.betterfps.transformers;

import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This will not work for other classes using the field.
 * @author Guilherme Chaguri
 */
public class WeakerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;


        // TODO: use this

        return bytes;
    }

    public byte[] patchWeakKeys(byte[] bytes, Naming[] fieldsToWeak) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        boolean patch = false;

        for(FieldNode field : classNode.fields) {
            weakLoop: for(Naming f : fieldsToWeak) {
                if(f.is(field.name, field.desc)) {
                    String oldDesc = field.desc;
                    field.desc = "Ljava/lang/ref/WeakReference;";
                    field.signature = "Ljava/lang/ref/WeakReference<" + oldDesc + ">;";
                    patch = true;
                    break weakLoop;
                }
            }
        }

        for(MethodNode method : classNode.methods) {

            InsnList newList = new InsnList();
            AbstractInsnNode n = null;

            instLoop: for(AbstractInsnNode node : method.instructions.toArray()) {
                if(n != null) newList.add(n);
                n = node;
                if(node instanceof FieldInsnNode) {
                    FieldInsnNode fNode = (FieldInsnNode)node;
                    if(!fNode.owner.equals(classNode.name)) continue instLoop;
                    weakLoop: for(Naming f : fieldsToWeak) {
                        if(f.is(fNode.name, fNode.desc)) {
                            fNode.desc = "Ljava/lang/ref/WeakReference;";
                            if(fNode.getOpcode() == Opcodes.PUTFIELD) {
                                newList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/ref/WeakReference", "<init>", "(Ljava/lang/Object;)V", false));
                                newList.add(fNode);
                                n = null;
                            } else if(fNode.getOpcode() == Opcodes.GETFIELD) {
                                newList.add(fNode);
                                newList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ref/WeakReference", "get", "()Ljava/lang/Object;", false));
                                n = null;
                            }
                            patch = true;
                            break weakLoop;
                        }
                    }
                }
            }
            if(n != null) newList.add(n);

            method.instructions.clear();
            method.instructions.add(newList);
        }

        if(patch) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return bytes;
    }

}
