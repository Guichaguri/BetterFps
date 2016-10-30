package guichaguri.betterfps.transformers;

import guichaguri.betterfps.tweaker.Mappings;
import java.util.Iterator;
import guichaguri.betterfps.BetterFpsHelper;
import net.minecraft.launchwrapper.IClassTransformer;
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
            if(Mappings.C_Minecraft.is(name)) {
                return patchClientStart(bytes);
            } else if(Mappings.C_World.is(name)) {//tick
                return patchWorldTick(bytes);
            } else if(Mappings.C_ClientBrandRetriever.is(name)) {
                return patchClientBrand(bytes);
            } else if(Mappings.C_WorldClient.is(name)) {
                return patchClientWorldLoad(bytes);
            } else if(Mappings.C_DedicatedServer.is(name)) {
                return patchServerStart(bytes);
            } else if(Mappings.C_IntegratedServer.is(name)) {
                return patchServerStart(bytes);
            }
        } catch(Exception ex) {
           ex.printStackTrace();
        }

        return bytes;
    }

    private byte[] patchClientStart(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(Mappings.M_startGame.is(method.name, method.desc)) {
                BetterFpsHelper.LOG.info("Patching Game Start...");
                InsnList list = new InsnList();

                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return
                        // Load the var 0 ("this" in Minecraft.startGame)
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        // Invoke BetterFpsVanilla.start
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "guichaguri/betterfps/BetterFpsClient", "start",
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


    private byte[] patchWorldTick(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(Mappings.M_tick.is(method.name, method.desc)) {
                BetterFpsHelper.LOG.info("Patching World Event...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return

                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "guichaguri/betterfps/BetterFps", "worldTick", "()V", false));

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

            if(Mappings.M_getClientModName.is(method.name, method.desc)) {
                BetterFpsHelper.LOG.info("Patching Client Brand...");
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

    public byte[] patchClientWorldLoad(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        boolean patch = false;

        for(MethodNode method : classNode.methods) {
            if(Mappings.M_Constructor.is(method.name)) {
                BetterFpsHelper.LOG.info("Patching World Client Event...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return

                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "guichaguri/betterfps/BetterFpsClient", "worldLoad", "()V", false));

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

    public byte[] patchServerStart(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        boolean patch = false;

        for(MethodNode method : classNode.methods) {
            if(Mappings.M_startServer.is(method.name, method.desc)) {
                BetterFpsHelper.LOG.info("Patching Server Start Event...");
                InsnList list = new InsnList();
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "guichaguri/betterfps/BetterFps", "serverStart", "()V", false));
                for(AbstractInsnNode node : method.instructions.toArray()) {
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
