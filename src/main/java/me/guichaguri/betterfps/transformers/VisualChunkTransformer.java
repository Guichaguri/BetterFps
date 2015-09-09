package me.guichaguri.betterfps.transformers;

import java.util.ArrayList;
import java.util.List;
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
public class VisualChunkTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return bytes;

        if(Naming.C_WorldServer.is(name)) {
            ClassNode node = readClass(bytes);
            for(MethodNode m : node.methods) {
                if(Naming.M_updateBlocks.is(m.name, m.desc)) {
                    BetterFps.log.info("PATCH TICK +++++++++++++++++++++++++ " + node.name);
                    patchTick(m);
                }
            }
            return toBytes(node);
        } else if(Naming.C_WorldClient.is(name)) {
            ClassNode node = readClass(bytes);
            for(MethodNode m : node.methods) {
                if(Naming.M_updateBlocks.is(m.name, m.desc)) {
                    BetterFps.log.info("PATCH TICK +++++++++++++++++++++++++ " + node.name);
                    patchTick(m);
                }
            }
            return toBytes(node);
        } else if(Naming.C_World.is(name)) {
            ClassNode node = readClass(bytes);
            for(MethodNode m : node.methods) {
                if(Naming.M_setActivePlayerChunksAndCheckLight.is(m.name, m.desc)) {
                    patchTickableCheck(m);
                }
            }
            return toBytes(node);
        } else if(Naming.C_ChunkCoordIntPair.is(name)) {
            ClassNode node = readClass(bytes);
            patchChunk(node);
            return toBytes(node);
        }

        return bytes;
    }

    private ClassNode readClass(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        return node;
    }

    private byte[] toBytes(ClassNode node) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }

    private void patchChunk(ClassNode node) {
        node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "isTickable", "Z", null, null));

        MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(IIZ)V", null, null);
        m.instructions = new InsnList();

        LabelNode l1 = new LabelNode();
        m.instructions.add(l1);
        // this(f1, f2)
        m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, node.name, "<init>", "(II)V", false));

        // isTickable = f3;
        m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        m.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, node.name, "isTickable", "Z"));

        LabelNode l2 = new LabelNode();
        m.instructions.add(l2);
        m.instructions.add(new InsnNode(Opcodes.RETURN));

        // Define local variables
        m.localVariables.clear();
        m.localVariables.add(new LocalVariableNode("this", "L" + node.name + ";", null, l1, l2, 0));
        m.localVariables.add(new LocalVariableNode("f1", "I", null, l1, l2, 1));
        m.localVariables.add(new LocalVariableNode("f2", "I", null, l1, l2, 2));
        m.localVariables.add(new LocalVariableNode("f3", "Z", null, l1, l2, 3));

        node.methods.add(m);
    }

    private void patchTickableCheck(MethodNode method) {
        InsnList list = new InsnList();
        for(AbstractInsnNode node : method.instructions.toArray()) {
            if(node instanceof MethodInsnNode) {
                MethodInsnNode m = (MethodInsnNode)node;
                if((Naming.C_ChunkCoordIntPair.isASM(m.owner)) && (Naming.M_Constructor.is(m.name))) {
                    BetterFps.log.info("Patching tickable chunks check...");
                    list.add(new VarInsnNode(Opcodes.ILOAD, 6)); // i1
                    list.add(new VarInsnNode(Opcodes.ILOAD, 7)); // j1
                    list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/guichaguri/betterfps/BetterFps", "isTickable", "(II)Z", false));
                    m.desc = "(IIZ)V";
                }
            }
            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);
    }

    private void patchTick(MethodNode method) {
        String coordName = null;
        List<Integer> lvs = new ArrayList<Integer>();
        for(LocalVariableNode node : method.localVariables) {
            int s = node.desc.length();
            if(s <= 2) continue;
            String c = node.desc.substring(1, s - 1);
            if(Naming.C_ChunkCoordIntPair.isASM(c)) {
                coordName = c;
                lvs.add(node.index);
                BetterFps.log.info("PATCH TICK ----------------------- " + node.index + "  - " + method.name);
            }
        }
        if(lvs.isEmpty()) return;

        InsnList list = new InsnList();

        for(AbstractInsnNode node : method.instructions.toArray()) {
            list.add(node);
            if((node.getOpcode() == Opcodes.ASTORE) && (node instanceof VarInsnNode)) {
                VarInsnNode var = (VarInsnNode)node;
                if(lvs.contains(var.var)) {
                    list.add(new VarInsnNode(Opcodes.ALOAD, var.var));
                    list.add(new FieldInsnNode(Opcodes.GETFIELD, coordName, "isTickable", "Z"));
                    LabelNode l1 = new LabelNode();
                    list.add(new JumpInsnNode(Opcodes.IFEQ, l1));
                    LabelNode l2 = new LabelNode();
                    list.add(l2);
                    list.add(new InsnNode(Opcodes.RETURN));
                    list.add(l1);
                    list.add(new FrameNode(Opcodes.F_APPEND, 1, new Object[]{coordName}, 0, null));
                }
            }
        }

        method.instructions.clear();
        method.instructions.add(list);
    }
}
