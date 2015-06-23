package me.guichaguri.betterfps.transformers;

import java.util.Iterator;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 *
 * @author Guilherme Chaguri
 */
public class EventTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return new byte[0];

        try {
            if(Naming.C_Minecraft.is(name)) {
                return patchStart(bytes);
            } else if(Naming.C_KeyBinding.is(name)) {
                return patchKeyTick(bytes);
            }
        } catch(Exception ex) {
           ex.printStackTrace();
        }

        return bytes;
    }

    private byte[] patchStart(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(Naming.M_startGame.is(method.name, method.desc)) {
                LogManager.getLogger().info("Patching Game Start...");
                InsnList list = new InsnList();

                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return
                        // Load the var 0 ("this" in Minecraft.startGame)
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        // Invoke BetterFpsVanilla.start
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "me/guichaguri/betterfps/BetterFps", "start",
                                "(L" + classNode.name + ";)V", false));
                    }
                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                patch = true;
            }
        }

        if(!patch) return bytes;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchKeyTick(byte[] bytes) throws Exception {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(Naming.M_onTick.is(method.name, method.desc)) {
                LogManager.getLogger().info("Patching Key Event...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return

                        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "me/guichaguri/betterfps/BetterFps", "keyEvent", "(I)V", false));

                    }
                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                patch = true;
            }
        }

        if(!patch) return bytes;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
