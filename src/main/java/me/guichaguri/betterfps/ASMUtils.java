package me.guichaguri.betterfps;

import java.util.List;
import me.guichaguri.betterfps.tweaker.Naming;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author Guilherme Chaguri
 */
public class ASMUtils {

    public static ClassNode readClass(byte[] bytes, int flags) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, flags);
        return classNode;
    }

    public static byte[] writeClass(ClassNode node, int flags) {
        ClassWriter writer = new ClassWriter(flags);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static MethodNode findMethod(ClassNode node, Naming naming) {
        for(MethodNode m : node.methods) {
            if(naming.is(m.name, m.desc)) return m;
        }
        return null;
    }

    public static FieldNode findField(ClassNode node, Naming naming) {
        for(FieldNode m : node.fields) {
            if(naming.is(m.name, m.desc)) return m;
        }
        return null;
    }

    public static MethodNode findMethod(ClassNode node, String name) {
        for(MethodNode m : node.methods) {
            if(m.name.equals(name)) return m;
        }
        return null;
    }

    public static int getNextAvailableIndex(List<LocalVariableNode> nodes) {
        return getNextAvailableIndex(nodes, 0);
    }

    private static int getNextAvailableIndex(List<LocalVariableNode> nodes, int index) {
        for(LocalVariableNode node : nodes) {
            if(index == node.index) {
                return getNextAvailableIndex(nodes, node.index + 1);
            }
        }
        return index;
    }

    public static InsnList appendNodeList(InsnList initial, InsnList extra) {
        return mergeNodeLists(extra, initial);
    }

    public static InsnList prependNodeList(InsnList initial, InsnList extra) {
        InsnList list = new InsnList();

        boolean added = false;
        for(AbstractInsnNode node : initial.toArray()) {
            if(!added && node instanceof LabelNode) {
                list.add(extra);
                added = true;
            }
            list.add(node);
        }
        return list;
        //return mergeNodeLists(initial, extra);
    }

    public static InsnList mergeNodeLists(InsnList from, InsnList to) {
        InsnList list = new InsnList();

        AbstractInsnNode[] nodes = to.toArray();
        int lastReturn = -1;
        for(int i = 0; i < nodes.length; i++) {
            AbstractInsnNode node = nodes[i];
            if(node.getOpcode() == Opcodes.RETURN) lastReturn = i;
        }
        for(int i = 0; i < nodes.length; i++) {
            AbstractInsnNode node = nodes[i];
            if(i == lastReturn) {
                list.add(from);
            }
            list.add(node);
        }

        return list;
    }

    public static void setVariableToMaxPeriod(AbstractInsnNode[] nodes, LocalVariableNode node) {
        LabelNode first = null, last = null;
        for(AbstractInsnNode n : nodes) {
            if(n instanceof LabelNode) {
                last = (LabelNode)n;
                if(first == null) first = last;
            }
        }
        node.start = first;
        node.end = last;
    }

    public static void addToInsnList(InsnList list, AbstractInsnNode[] nodes) {
        for(AbstractInsnNode node : nodes) {
            list.add(node);
        }
    }

    public static String getAnnotationValue(AnnotationNode node, String k) {
        return getAnnotationValue(node, k, String.class);
    }

    public static <T extends Object> T getAnnotationValue(AnnotationNode node, String k, Class<T> type) {
        if(node.values == null) return null;
        boolean isEnum = type.isEnum();
        for(int x = 0; x < node.values.size() - 1; x += 2) {
            Object key = node.values.get(x);
            Object value = node.values.get(x + 1);
            if(!(key instanceof String) || !key.equals(k)) continue;
            if(isEnum) {
                if(value instanceof String[]) {
                    return (T)Enum.valueOf((Class<? extends Enum>)type, ((String[])value)[1]);
                }
            } else {
                if(value instanceof String[]) {
                    return (T)(((String[])value)[1]);
                } else {
                    return (T)value;
                }
            }
        }
        return null;
    }

}
