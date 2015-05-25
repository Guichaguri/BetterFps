package me.guichaguri.betterfps.vanilla;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Used to handle events when its not Forge
 *
 * @author Guilherme Chaguri
 */
public class EventTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        if((name.equals("net.minecraft.client.Minecraft")) || (name.equals("bsu"))) {
            return patchStart(bytes);
        } else if((name.equals("net.minecraft.client.settings.KeyBinding")) || (name.equals("bsr"))) {
            return patchKeyTick(bytes);
        }

        return bytes;
    }

    private byte[] patchStart(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(method.desc.equals("()V")) {
                if((method.name.equals("startGame")) || (method.name.equals("aj"))) {
                    if(method.exceptions != null) {
                        for(String s : method.exceptions) {
                            System.out.println(s);
                        }
                    }
                    LogManager.getLogger().info("Patching Game Start...");
                    InsnList list = new InsnList();
                    for(AbstractInsnNode node : method.instructions.toArray()) {
                        if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return
                            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "me/guichaguri/betterfps/vanilla/BetterFpsVanilla", "start", "()V", false));
                        }
                        list.add(node);
                    }

                    method.instructions.clear();
                    method.instructions.add(list);
                    patch = true;
                }
            } else if(method.name.equals("<init>")) {
                LogManager.getLogger().info("Patching Minecraft Init...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "me/guichaguri/betterfps/vanilla/BetterFpsVanilla", "setMc",
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

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchKeyTick(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(method.desc.equals("(I)V")) {
                if((method.name.equals("onTick")) || (method.name.equals("a"))) {
                    LogManager.getLogger().info("Patching Key Event...");
                    InsnList list = new InsnList();
                    for(AbstractInsnNode node : method.instructions.toArray()) {
                        if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return
                            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "me/guichaguri/betterfps/vanilla/BetterFpsVanilla", "keyEvent", "()V", false));
                        }
                        list.add(node);
                    }

                    method.instructions.clear();
                    method.instructions.add(list);
                    patch = true;
                }
            }
        }

        if(!patch) return bytes;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
