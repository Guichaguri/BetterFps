package me.guichaguri.betterfps.transformers;

import java.util.Iterator;
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
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Guilherme Chaguri
 */
public class MiscTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        if(Naming.C_Minecraft.is(name)) {
            return patchMinecraft(bytes);
        }

        return bytes;
    }


    public byte[] patchMinecraft(byte[] bytes) {
        if(BetterFpsHelper.CONFIG == null) BetterFpsHelper.loadConfig();

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        boolean patch = false;

        Iterator<FieldNode> i = classNode.fields.iterator();
        while(i.hasNext()) {
            FieldNode field = i.next();
            if((!BetterFpsHelper.PREALLOCATE_MEMORY) && (Naming.F_memoryReserve.is(field.name, field.desc))) {
                i.remove();
                patch = true;
            }
        }

        for(MethodNode method : classNode.methods) {
            if(!BetterFpsHelper.PREALLOCATE_MEMORY) {
                if((Naming.M_freeMemory.is(method.name, method.desc)) ||
                   (Naming.M_StaticBlock.is(method.name, method.desc))) {
                    int loc = -1;
                    for(int o = 0; o < method.instructions.size(); o++) {
                        AbstractInsnNode node = method.instructions.get(o);
                        if((node instanceof FieldInsnNode) && (node.getOpcode() == Opcodes.PUTSTATIC)) {
                            FieldInsnNode field = (FieldInsnNode) node;
                            if(Naming.F_memoryReserve.is(field.name, field.desc)) {
                                loc = o;
                                break;
                            }
                        }
                    }
                    if(loc != -1) {
                        AbstractInsnNode node1 = method.instructions.get(loc - 2);
                        AbstractInsnNode node2 = method.instructions.get(loc - 1);
                        AbstractInsnNode node3 = method.instructions.get(loc);

                        method.instructions.remove(node1);
                        method.instructions.remove(node2);
                        method.instructions.remove(node3);
                    }
                }
            }
        }

        if(patch) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return bytes;
    }
}
