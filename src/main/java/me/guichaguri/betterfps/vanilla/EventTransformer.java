package me.guichaguri.betterfps.vanilla;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
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
                    System.out.println("Patch Start");
                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "me/guichaguri/betterfps/vanilla/BetterFpsVanilla", "start", "()V", false));
                    patch = true;
                }
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
                    System.out.println("Patch Key Tick");
                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "me/guichaguri/betterfps/vanilla/BetterFpsVanilla", "keyEvent", "()V", false));
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
