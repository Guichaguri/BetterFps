package guichaguri.betterfps.transformers;

import guichaguri.betterfps.ASMUtils;
import guichaguri.betterfps.BetterFpsConfig;
import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.tweaker.Naming;
import java.util.Iterator;
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
        BetterFpsConfig config = BetterFpsConfig.getConfig();
        if(config == null) config = BetterFpsHelper.loadConfig();

        ClassNode classNode = ASMUtils.readClass(bytes, ClassReader.SKIP_FRAMES);
        boolean patch = false;

        Iterator<FieldNode> i = classNode.fields.iterator();
        while(i.hasNext()) {
            FieldNode field = i.next();
            if((!config.preallocateMemory) && (Naming.F_memoryReserve.is(field.name, field.desc))) {
                i.remove();
                patch = true;
            }
        }

        for(MethodNode method : classNode.methods) {
            if(!config.preallocateMemory) {
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
            return ASMUtils.writeClass(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        }

        return bytes;
    }

}
