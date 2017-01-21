package guichaguri.betterfps;

import guichaguri.betterfps.transformers.patcher.annotations.Param;
import guichaguri.betterfps.tweaker.Mappings;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
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

    public static MethodNode findMethod(ClassNode node, Mappings mappings) {
        for(MethodNode m : node.methods) {
            if(mappings.is(m.name, m.desc)) return m;
        }
        return null;
    }

    public static FieldNode findField(ClassNode node, Mappings mappings) {
        for(FieldNode m : node.fields) {
            if(mappings.is(m.name, m.desc)) return m;
        }
        return null;
    }

    public static MethodNode findMethod(ClassNode node, String name, String desc) {
        for(MethodNode m : node.methods) {
            if(m.name.equals(name) && m.desc.equals(desc)) return m;
        }
        return null;
    }

    public static FieldNode findField(ClassNode node, String name, String desc) {
        for(FieldNode f : node.fields) {
            if(f.name.equals(name) && f.desc.equals(desc)) return f;
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

    public static AnnotationNode getAnnotation(List<AnnotationNode> annotations, Class<? extends Annotation> type) {
        if(annotations == null || annotations.isEmpty()) return null;

        String t = Type.getDescriptor(type);
        for(AnnotationNode node : annotations) {
            if(node.desc.equals(t)) return node;
        }
        return null;
    }

    public static String getAnnotationValue(AnnotationNode node, String k) {
        return getAnnotationValue(node, k, String.class);
    }

    public static <T extends Object> T getAnnotationValue(AnnotationNode node, String k, Class<T> type) {
        return getAnnotationValue(node, k, type, null);
    }

    public static <T extends Object> T getAnnotationValue(AnnotationNode node, String k, Class<T> type, T def) {
        if(node.values == null) return def;
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
        return def;
    }

    public static String getReturnDesc(String desc) {
        return Type.getReturnType(desc).getDescriptor();
    }

    public static String[] getParametersDesc(String desc) {
        Type[] t = Type.getArgumentTypes(desc);
        String[] r = new String[t.length];

        for(int i = 0; i < t.length; i++) {
            r[i] = t[i].getDescriptor();
        }
        return r;
    }

    private static boolean isNode(AbstractInsnNode node, Class type, int opcode, String owner, String name, String desc) {
        if(node.getOpcode() != opcode || node.getClass() != type) return false;
        if(type == MethodInsnNode.class) {
            MethodInsnNode method = (MethodInsnNode)node;
            if(owner != null && !method.owner.equals(owner)) return false;
            if(name != null && !method.name.equals(name)) return false;
            if(desc != null && !method.desc.equals(desc)) return false;
        } else if(type == FieldInsnNode.class) {
            FieldInsnNode field = (FieldInsnNode)node;
            if(owner != null && !field.owner.equals(owner)) return false;
            if(name != null && !field.name.equals(name)) return false;
            if(desc != null && !field.desc.equals(desc)) return false;
        }
        return true;
    }

    private static boolean isNode(AbstractInsnNode node, Class type, int opcode, Mappings name) {
        if(node.getOpcode() != opcode || node.getClass() != type) return false;
        if(type == MethodInsnNode.class) {
            if(!name.is((MethodInsnNode)node)) return false;
        } else if(type == FieldInsnNode.class) {
            if(!name.is((FieldInsnNode)node)) return false;
        }
        return true;
    }

    public static <T extends AbstractInsnNode> List<T> findNodes(InsnList nodes, Class<T> type, int opcode,
                                                                 String owner, String name, String desc) {
        List<T> list = new ArrayList<T>();

        for(int i = 0; i < nodes.size(); i++) {
            AbstractInsnNode node = nodes.get(i);
            if(isNode(node, type, opcode, owner, name, desc)) {
                list.add((T)node);
            }
        }

        return list;
    }

    public static <T extends AbstractInsnNode> List<T> findNodes(InsnList nodes, Class<T> type, int opcode) {
        return findNodes(nodes, type, opcode, null, null, null);
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int opcode, int index,
                                                          String owner, String name, String desc) {
        for(int i = 0; i < nodes.size(); i++) {
            AbstractInsnNode node = nodes.get(i);
            if(isNode(node, type, opcode, owner, name, desc)) {
                if(index-- < 0) return (T)node;
            }
        }

        return null;
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int opcode, int index, Mappings name) {
        for(int i = 0; i < nodes.size(); i++) {
            AbstractInsnNode node = nodes.get(i);
            if(isNode(node, type, opcode, name)) {
                if(index-- < 0) return (T)node;
            }
        }

        return null;
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int opcode, int index) {
        return findNode(nodes, type, opcode, index, null, null, null);
    }

    public static LocalVariableNode findVariable(MethodNode method, String desc) {
        for(LocalVariableNode var : method.localVariables) {
            // TODO: check if the variable is available at the injection point
            if(var.desc.equals(desc)) return var;
        }
        return null;
    }

    public static int[] getLocalVariables(MethodNode method, ClassNode targetClass, MethodNode targetMethod) {
        int[] variables = new int[method.parameters.size()];

        List<AnnotationNode>[] array = method.visibleParameterAnnotations;
        //String[] nodes = ;

        for(int i = 0; i < array.length; i++) {
            List<AnnotationNode> annotations = array[i];

            if(annotations != null && !annotations.isEmpty()) {
                AnnotationNode annotation = getAnnotation(annotations, Param.class);
                if(annotation != null) {
                    Integer value = getAnnotationValue(annotation, "value", Integer.class);
                    if(value != null) {
                        variables[i] = value;
                        continue;
                    }
                }
            }

            //variables[i] = findVariable(target, method)
        }

        return variables;
    }

    public static LocalVariableNode insertVariable(MethodNode m, String name, String desc, String sig, LabelNode l1, LabelNode l2) {
        LocalVariableNode var = new LocalVariableNode(name, desc, sig, l1, l2, m.maxLocals);
        m.localVariables.add(var);
        m.maxLocals++;
        return var;
    }

    public static LabelNode getFirstLabel(InsnList instructions) {
        for(int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode node = instructions.get(i);
            if(node instanceof LabelNode) return (LabelNode)node;
        }
        return null;
    }

    public static LabelNode getLastLabel(InsnList instructions) {
        for(int i = instructions.size() - 1; i >= 0; i--) {
            AbstractInsnNode node = instructions.get(i);
            if(node instanceof LabelNode) return (LabelNode)node;
        }
        return null;
    }

    public static boolean isNodeInside(InsnList list, AbstractInsnNode node, LabelNode from, LabelNode to) {
        boolean inside = false;

        for(int i = 0; i < list.size(); i++) {
            AbstractInsnNode n = list.get(i);
            if(n == from) {
                inside = true;
            } else if(n == to) {
                return false;
            }
            if(n == node) {
                return inside;
            }
        }
        return false;
    }

    public static List<InsnNode> getReturnNodes(InsnList instructions) {
        List<InsnNode> nodes = new ArrayList<InsnNode>();

        for(int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode node = instructions.get(i);
            if(!(node instanceof InsnNode)) continue;
            switch(node.getOpcode()) {
                case Opcodes.ARETURN:
                case Opcodes.DRETURN:
                case Opcodes.FRETURN:
                case Opcodes.IRETURN:
                case Opcodes.LRETURN:
                case Opcodes.RETURN:
                    nodes.add((InsnNode)node);
                    break;
                default:
                    break;
            }
        }

        return nodes;
    }

    public static boolean hasAccess(MethodNode m, int access) {
        return (m.access & access) != 0;
    }

    public static boolean hasAccess(FieldNode f, int access) {
        return (f.access & access) != 0;
    }

    public static Object findVariable(String desc, ClassNode clazz, MethodNode method, AbstractInsnNode pos) {
        for(LocalVariableNode var : method.localVariables) {
            System.out.println(var.desc + " - " + desc + " - " + var.desc.equals(desc));
            if(var.desc.equals(desc) && isNodeInside(method.instructions, pos, var.start, var.end)) {
                System.out.println("FOUND IT!");
                return var;
            }
        }

        boolean isStatic = hasAccess(method, Opcodes.ACC_STATIC);
        for(FieldNode field : clazz.fields) {
            if(isStatic && !hasAccess(field, Opcodes.ACC_STATIC)) continue;
            if(field.desc.equals(desc)) {
                return field;
            }
        }

        // Nothing has been found. Lets return just the descriptor
        return desc;
    }

    /**
     * Finds the local variables or fields corresponding to the desc, based on the method and the injection position
     */
    public static Object[] findParametersValue(String desc, ClassNode clazz, MethodNode method, AbstractInsnNode pos) {
        Type[] parameters = Type.getArgumentTypes(desc);
        Object[] values = new Object[parameters.length];

        for(int i = 0; i < parameters.length; i++) {
            String d = parameters[i].getDescriptor();
            values[i] = findVariable(d, clazz, method, pos);
        }
        return values;
    }

    public static AbstractInsnNode getReadNodeForVariable(ClassNode clazz, Object variable) {
        if(variable instanceof LocalVariableNode) {
            LocalVariableNode var = (LocalVariableNode)variable;
            return new VarInsnNode(Type.getType(var.desc).getOpcode(Opcodes.ILOAD), var.index);
        } else if(variable instanceof FieldNode) {
            FieldNode field = (FieldNode)variable;
            return new FieldInsnNode(Opcodes.GETFIELD, clazz.name, field.name, field.desc);
        }
        return new InsnNode(Opcodes.ACONST_NULL);
    }

    public static AbstractInsnNode getWriteNodeForVariable(ClassNode clazz, Object variable) {
        if(variable instanceof LocalVariableNode) {
            LocalVariableNode var = (LocalVariableNode)variable;
            return new VarInsnNode(Type.getType(var.desc).getOpcode(Opcodes.ISTORE), var.index);
        } else if(variable instanceof FieldNode) {
            FieldNode field = (FieldNode)variable;
            return new FieldInsnNode(Opcodes.PUTFIELD, clazz.name, field.name, field.desc);
        }
        return null;
    }

    public static InsnList insertMethod(ClassNode sourceClass, MethodNode sourceMethod,
                                        ClassNode targetClass, MethodNode targetMethod,
                                        AbstractInsnNode pos, InsnList beforeReturn, InsnList afterReturn) {

        List<InsnNode> returnNodes = getReturnNodes(sourceMethod.instructions);
        Object[] parameters = findParametersValue(sourceMethod.desc, targetClass, targetMethod, pos);

        ASMUtils.copyMethod(sourceClass, targetClass, sourceMethod, true);

        InsnList list = new InsnList();

        if(beforeReturn != null) list.add(beforeReturn);

        for(Object o : parameters) {
            list.add(getReadNodeForVariable(targetClass, o));
        }
        int opcode = hasAccess(sourceMethod, Opcodes.ACC_STATIC) ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
        list.add(new MethodInsnNode(opcode, targetClass.name, sourceMethod.name, sourceMethod.desc, false));

        if(afterReturn != null) list.add(afterReturn);

        if(returnNodes.size() > 1) {
            // The method will have to be copied instead of injected
            // It might be possible to inject the method replacing return nodes with jump nodes
            // TODO: take a look into jump nodes

        } else {
            // The method can be injected!
            // I don't always copy it because it might prevent conflict with other transformers

        }

        return list;
    }

    public static void insertMethodIntoVariable(LocalVariableNode var) {

    }

    public static AbstractInsnNode findHead(InsnList list) {
        return list.get(0);
    }

    /**
     * Replaces class references to another class in a method
     */
    public static void replaceReferences(String className, String replacement, MethodNode method) {
        // "Descify" the class names
        String classNameDesc = String.format("L%s;", className);
        String replacementDesc = String.format("L%s;", replacement);

        method.desc = method.desc.replaceAll(classNameDesc, replacementDesc);

        InsnList list = method.instructions;

        for(int i = 0; i < list.size(); i++) {
            AbstractInsnNode node = list.get(i);

            if(node instanceof MethodInsnNode) { // Method calls
                MethodInsnNode m = (MethodInsnNode)node;

                m.desc = m.desc.replaceAll(classNameDesc, replacementDesc);
                if(m.owner.equals(className)) m.owner = replacement;

            } else if(node instanceof FieldInsnNode) { // Field read/write
                FieldInsnNode f = (FieldInsnNode)node;

                f.desc = f.desc.replaceAll(classNameDesc, replacementDesc);
                if(f.owner.equals(className)) f.owner = replacement;

            } else if(node instanceof TypeInsnNode) { // Type (for arrays, casts, instanceof, etc)
                TypeInsnNode t = (TypeInsnNode)node;

                t.desc = t.desc.replaceAll(classNameDesc, replacementDesc);
            }
        }

        for(LocalVariableNode var : method.localVariables) { // Any local variables (including "this")
            if(var.desc.equals(classNameDesc)) var.desc = replacementDesc;
        }
    }

    /**
     * Replaces class references to another class in a field
     */
    public static void replaceReferences(String className, String replacement, FieldNode field) {
        field.desc = field.desc.replaceAll(className, replacement);
    }

    /**
     * Copies a method to another class, taking care of references and replacement
     */
    public static void copyMethod(ClassNode original, ClassNode target, MethodNode method, boolean replace) {
        MethodNode replacedMethod = findMethod(target, method.name, method.desc);

        if(replacedMethod != null) {
            if(!replace) return;
            target.methods.remove(replacedMethod);
        }

        replaceReferences(original.name, target.name, method);
        target.methods.add(method);
    }

    /**
     * Copies a field to another class, taking care of references, and replacement
     */
    public static void copyField(ClassNode original, ClassNode target, FieldNode field, boolean replace) {
        FieldNode replacedField = findField(target, field.name, field.desc);

        if(replacedField != null) {
            if(!replace) return;
            target.methods.remove(replacedField);
        }

        replaceReferences(original.name, target.name, field);
        target.fields.add(field);
    }

}
