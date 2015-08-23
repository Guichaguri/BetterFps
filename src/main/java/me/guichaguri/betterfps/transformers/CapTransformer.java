package me.guichaguri.betterfps.transformers;

import java.util.Iterator;
import me.guichaguri.betterfps.BetterFps;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author Guilherme Chaguri
 */
public class CapTransformer implements IClassTransformer {


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        if(Naming.C_PrimedTNT.is(name)) {
            return patchEntityUpdateCap(bytes, "TNT_TICKS", "MAX_TNT_TICKS");
        }

        return bytes;
    }

    private byte[] patchEntityUpdateCap(byte[] bytes, String fieldName, String maxFieldName) {

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();
            if(Naming.M_onUpdate.is(method.name, method.desc)) {
                BetterFps.log.info("Patching Entity Cap... (" + classNode.name + ")");
                InsnList list = new InsnList();

                boolean b = false;
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(!b && node instanceof LabelNode) {
                        // All of this = if(fieldName++ > maxFieldName) return;

                        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "me/guichaguri/betterfps/BetterFps", fieldName, "I"));

                        list.add(new InsnNode(Opcodes.DUP));
                        list.add(new InsnNode(Opcodes.ICONST_1));
                        list.add(new InsnNode(Opcodes.IADD));
                        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "me/guichaguri/betterfps/BetterFps", fieldName, "I"));

                        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "me/guichaguri/betterfps/BetterFps", maxFieldName, "I"));
                        list.add(new JumpInsnNode(Opcodes.IF_ICMPLE, (LabelNode)node));
                        list.add(new InsnNode(Opcodes.RETURN));
                        list.add(node);
                        b = true;
                        continue;
                    }
                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                patch = true;
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
