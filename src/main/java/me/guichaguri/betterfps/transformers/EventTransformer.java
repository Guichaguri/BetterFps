package me.guichaguri.betterfps.transformers;

import java.util.Iterator;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
            } else if(Naming.C_EntityPlayer.is(name)) {
                return patchPlayerTick(bytes);
            } else if(Naming.C_ClientBrandRetriever.is(name)) {
                return patchClientBrand(bytes);
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


    private byte[] patchPlayerTick(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(Naming.M_onUpdate.is(method.name, method.desc)) {
                LogManager.getLogger().info("Patching Player Event...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return

                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "me/guichaguri/betterfps/BetterFps", "playerTick", "()V", false));

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


    public byte[] patchClientBrand(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(Naming.M_getClientModName.is(method.name, method.desc)) {
                LogManager.getLogger().info("Patching Client Brand...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node instanceof LdcInsnNode) {
                        LdcInsnNode ldc = (LdcInsnNode)node;
                        if((ldc.cst instanceof String) && (ldc.cst.equals("vanilla"))) {
                            ldc.cst = "BetterFps-" + BetterFpsHelper.VERSION;
                        }
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
