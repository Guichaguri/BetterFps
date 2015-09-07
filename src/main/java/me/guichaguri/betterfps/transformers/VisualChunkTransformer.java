package me.guichaguri.betterfps.transformers;

import java.util.ArrayList;
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

    public boolean isTickable = false;

    public VisualChunkTransformer(int i1, int i2, boolean i3) {
        this(i1, i2);
        isTickable = i3;
    }

    public VisualChunkTransformer(int i1, int i2) {

    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return bytes;

        if(Naming.C_World.is(name)) {
            ClassNode node = readClass(bytes);
            patchChunk(node);
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
        m.localVariables = new ArrayList<LocalVariableNode>();

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
        // Define local variables
        m.localVariables.add(new LocalVariableNode("this", "L" + node.name + ";", null, l1, l2, 0));
        m.localVariables.add(new LocalVariableNode("f1", "I", null, l1, l2, 1));
        m.localVariables.add(new LocalVariableNode("f2", "I", null, l1, l2, 2));
        m.localVariables.add(new LocalVariableNode("f3", "Z", null, l1, l2, 3));

        node.methods.add(m);
    }

    private void patchTickableCheck(MethodNode method) {

    }

}
