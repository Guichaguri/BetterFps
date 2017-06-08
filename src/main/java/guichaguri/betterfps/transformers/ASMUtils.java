package guichaguri.betterfps.transformers;

import guichaguri.betterfps.transformers.annotations.Param;
import guichaguri.betterfps.tweaker.Mappings;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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

    public static boolean isReturn(int opcode) {
        return opcode == Opcodes.RETURN || opcode == Opcodes.ARETURN || opcode == Opcodes.DRETURN ||
                opcode == Opcodes.FRETURN || opcode == Opcodes.IRETURN || opcode == Opcodes.LRETURN;
    }

    public static void appendNodeList(InsnList initial, InsnList extra) {
        List<AbstractInsnNode> returns = findReturns(initial);
        for(AbstractInsnNode r : returns) {
            initial.insertBefore(r, extra);
        }
    }

    public static void prependNodeList(InsnList initial, InsnList extra) {
        AbstractInsnNode head = findHead(initial);
        initial.insert(head, extra);
    }

    public static void removeLastReturn(InsnList list) {
        for(int i = list.size() - 1; i >= 0; i--) {
            AbstractInsnNode node = list.get(i);
            if(isReturn(node.getOpcode())) {
                list.remove(node);
                break;
            }
        }
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
        if(opcode != -1 && node.getOpcode() != opcode) return false;
        if(node.getClass() != type) return false;
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
        List<T> list = null;

        for(int i = 0; i < nodes.size(); i++) {
            AbstractInsnNode node = nodes.get(i);
            if(isNode(node, type, opcode, owner, name, desc)) {
                if(list == null) list = new ArrayList<T>();
                list.add((T)node);
            }
        }

        return list;
    }

    public static <T extends AbstractInsnNode> List<T> findNodes(InsnList nodes, Class<T> type, int opcode) {
        return findNodes(nodes, type, opcode, null, null, null);
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int index, Predicate<T> predicate) {
        if(index >= 0) {
            for(int i = 0; i < nodes.size(); i++) {
                AbstractInsnNode node = nodes.get(i);
                if(node.getClass() == type && predicate.test((T)node)) {
                    if(index-- <= 0) return (T)node;
                }
            }
        } else {
            for(int i = nodes.size() - 1; i >= 0; i--) {
                AbstractInsnNode node = nodes.get(i);
                if(node.getClass() == type && predicate.test((T)node)) {
                    if(index++ >= -1) return (T)node;
                }
            }
        }

        return null;
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int opcode, int index,
                                                          String owner, String name, String desc) {
        for(int i = 0; i < nodes.size(); i++) {
            AbstractInsnNode node = nodes.get(i);
            if(isNode(node, type, opcode, owner, name, desc)) {
                if(index-- <= 0) return (T)node;
            }
        }

        return null;
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int opcode, int index, Mappings name) {
        for(int i = 0; i < nodes.size(); i++) {
            AbstractInsnNode node = nodes.get(i);
            if(isNode(node, type, opcode, name)) {
                if(index-- <= 0) return (T)node;
            }
        }

        return null;
    }

    public static <T extends AbstractInsnNode> T findNode(InsnList nodes, Class<T> type, int opcode, int index) {
        return findNode(nodes, type, opcode, index, null, null, null);
    }

    public static void replace(InsnList list, AbstractInsnNode from, AbstractInsnNode to) {
        list.insertBefore(from, to);
        list.remove(from);
    }

    public static void replace(InsnList list, AbstractInsnNode from, InsnList to) {
        list.insertBefore(from, to);
        list.remove(from);
    }

    public static LocalVariableNode findVariable(MethodNode method, int index) {
        for(LocalVariableNode var : method.localVariables) {
            if(var.index == index) return var;
        }
        return null;
    }

    public static LocalVariableNode findVariable(MethodNode method, Mappings mappings) {
        for(LocalVariableNode var : method.localVariables) {
            if(mappings.is(Type.getType(var.desc).getClassName())) return var;
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

    public static void removeLabelSection(InsnList list, LabelNode label) {
        Iterator<AbstractInsnNode> i = list.iterator(list.indexOf(label) + 1);
        for(AbstractInsnNode node = i.next(); i.hasNext(); node = i.next()) {
            if(node instanceof LabelNode) break;
            i.remove();
        }
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

    public static boolean hasAccess(MethodNode m, int access) {
        return (m.access & access) != 0;
    }

    public static boolean hasAccess(FieldNode f, int access) {
        return (f.access & access) != 0;
    }

    public static int indexOf(Type[] types, Type[] needle) {
        int count = 0;
        for(int i = 0; i < types.length; i++) {
            if(types[i].getDescriptor().equals(needle[count].getDescriptor())) {
                count++;
                if(count >= needle.length) {
                    return i - count;
                }
            } else {
                count = 0;
            }
        }
        return -1;
    }

    private static Object findVariable(MethodNode sourceMethod, ClassNode targetClass, MethodNode targetMethod,
                                      AbstractInsnNode pos, int i, String desc, boolean isStatic,
                                      int targetParamsStart, int targetParamsEnd) {

        // Parses the @Param annotation returning the requested variable
        List<AnnotationNode>[] annotations = sourceMethod.invisibleParameterAnnotations;
        if(annotations != null && annotations.length > i) {
            AnnotationNode annotation = getAnnotation(annotations[i], Param.class);

            if(annotation != null) {
                int index = getAnnotationValue(annotation, "value", int.class, -1);
                LocalVariableNode var = index != -1 ? findVariable(sourceMethod, index) : null;

                if(var != null && var.desc.equals(desc)) {
                    return var;
                }
            }
        }

        // Uses the pattern found in the desc to use the right variable order from the target method
        if(targetParamsStart != -1 && i >= targetParamsStart && i < targetParamsEnd) {
            return targetMethod.localVariables.get(i - targetParamsStart + (isStatic ? 0 : 1));
        }

        // Tries to find local variables that match the same desc
        for(LocalVariableNode var : targetMethod.localVariables) {
            if(var.desc.equals(desc) && isNodeInside(targetMethod.instructions, pos, var.start, var.end)) {
                return var;
            }
        }

        // Tries to find fields that match the same desc (and are accessible)
        for(FieldNode field : targetClass.fields) {
            if(isStatic && !hasAccess(field, Opcodes.ACC_STATIC)) continue;
            if(field.desc.equals(desc)) {
                return field;
            }
        }

        // Nothing has been found :(
        return null;
    }

    /**
     * Finds the local variables or fields corresponding to the desc, based on the method and the injection position
     */
    public static Object[] findParametersValue(MethodNode sourceMethod, ClassNode targetClass,
                                               MethodNode targetMethod, AbstractInsnNode pos) {

        Type[] parameters = Type.getArgumentTypes(sourceMethod.desc);
        Object[] values = new Object[parameters.length];

        Type[] targetParameters = Type.getArgumentTypes(targetMethod.desc);
        int targetParamsStart = indexOf(parameters, targetParameters);
        int targetParamsEnd = targetParamsStart + targetParameters.length;
        boolean isStatic = hasAccess(targetMethod, Opcodes.ACC_STATIC);

        for(int i = 0; i < parameters.length; i++) {
            String d = parameters[i].getDescriptor();

            values[i] = findVariable(sourceMethod, targetClass, targetMethod, pos,
                                    i, d, isStatic, targetParamsStart, targetParamsEnd);
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
                                        AbstractInsnNode pos, boolean ignoreReturnValue) {

        Object[] parameters = findParametersValue(sourceMethod, targetClass, targetMethod, pos);
        ASMUtils.copyMethod(sourceClass, targetClass, sourceMethod, true);

        boolean isStatic = hasAccess(sourceMethod, Opcodes.ACC_STATIC);
        InsnList list = new InsnList();
        int stack = parameters.length;

        if(!isStatic) {
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            stack++;
        }

        for(Object o : parameters) {
            list.add(getReadNodeForVariable(targetClass, o));
        }

        if(targetMethod.maxStack < stack) targetMethod.maxStack = stack; // Fix max stack if needed

        int opcode = isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
        list.add(new MethodInsnNode(opcode, targetClass.name, sourceMethod.name, sourceMethod.desc, false));

        if(ignoreReturnValue && Type.getReturnType(sourceMethod.desc) != Type.VOID_TYPE) {
            list.add(new InsnNode(Opcodes.POP)); // Pop the return value
        }

        // The method can be injected instead of copied and fired
        // It's totally possible to add the instructions, sort local variables and reuse some of them
        // It might also be possible to inject the method by replacing return nodes with jump nodes
        // But is it worth it?

        return list;
    }

    public static AbstractInsnNode findHead(InsnList list) {
        for(int i = 0; i < list.size(); i++) {
            AbstractInsnNode node = list.get(i);
            if(node instanceof LabelNode) return node;
        }
        return list.get(0);
    }

    public static List<AbstractInsnNode> findReturns(InsnList list) {
        List<AbstractInsnNode> nodes = new ArrayList<AbstractInsnNode>();
        for(int i = 0; i < list.size(); i++) {
            AbstractInsnNode node = list.get(i);
            if(isReturn(node.getOpcode())) nodes.add(node);
        }
        return nodes;
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
     * Replaces super calls to a replaced method from the method
     *
     * Tries to find "super" calls to the replaced method, if any, renames the method
     * If the replaced method is null, it changes the method call to the super-super class
     */
    public static void replaceSuperCalls(MethodNode method, ClassNode replacedClass, MethodNode replacedMethod) {
        InsnList list = method.instructions;
        boolean addedReplacedMethod = false;
        boolean hasReplacedMethod = replacedMethod != null;

        for(int i = 0; i < list.size(); i++) {
            AbstractInsnNode node = list.get(i);

            if(node.getOpcode() != Opcodes.INVOKESPECIAL) continue;
            if(!(node instanceof MethodInsnNode)) continue;
            MethodInsnNode m = (MethodInsnNode)node;

            if(!m.owner.equals(replacedClass.name)) continue;
            if(hasReplacedMethod && (!m.name.equals(replacedMethod.name) || !m.desc.equals(replacedMethod.desc))) continue;

            if(!hasReplacedMethod) {

                // Replace the owner class to an upper level to prevent infinite loops
                if(m.owner.equals(replacedClass.name)) m.owner = replacedClass.superName;
                continue;

            } else if(!addedReplacedMethod) {

                replacedMethod.name += "_BetterFps";

                // Not really required for Oracle's JVM, but another JVM implementation might require this change
                m.setOpcode(Opcodes.INVOKEVIRTUAL);

                if(!replacedClass.methods.contains(replacedMethod)) {
                    replacedClass.methods.add(replacedMethod);
                }
                addedReplacedMethod = true;

            }

            m.name = replacedMethod.name;
        }
    }

    /**
     * Merges {@link LocalVariableNode} from a method to another method, while taking care of duplicate variables
     */
    public static void mergeLocalVariables(MethodNode from, MethodNode to) {
        for(LocalVariableNode var : from.localVariables) {
            LocalVariableNode var2 = ASMUtils.findVariable(to, var.index);

            if(var2 == null) {
                // No variable with this index, we can just add it
                to.localVariables.add(var);
                continue;
            }
            if(var2.desc.equals(var.desc)) {
                // The variable exists and it's the same desc, we can just reuse it
                continue;
            }

            // There is a conflict, we'll need to add and patch it
            int oldIndex = var.index;
            var.index = to.maxLocals;
            to.localVariables.add(var);
            to.maxLocals++;

            InsnList list = from.instructions;
            for(int i = 0; i < list.size(); i++) {
                AbstractInsnNode node = list.get(i);
                if(node instanceof VarInsnNode) {
                    VarInsnNode varNode = (VarInsnNode)node;
                    if(varNode.var == oldIndex) varNode.var = var.index;
                }
            }
        }
    }

    /**
     * Copies a method to another class, taking care of references and replacement
     */
    public static void copyMethod(ClassNode original, ClassNode target, MethodNode method, boolean replace) {
        copyMethod(original, target, method, findMethod(target, method.name, method.desc), replace);
    }

    /**
     * Copies a method to another class, taking care of references and replacement
     */
    public static void copyMethod(ClassNode original, ClassNode target, MethodNode method, MethodNode replacedMethod, boolean replace) {
        if(replacedMethod != null) {
            if(!replace) return;
            target.methods.remove(replacedMethod);
        }

        replaceSuperCalls(method, target, replacedMethod);
        replaceReferences(original.name, target.name, method);
        target.methods.add(method);
    }

    /**
     * Appends a method to another class, taking care of references
     */
    public static void appendMethod(ClassNode original, ClassNode target, MethodNode method) {
        appendMethod(original, target, method, findMethod(target, method.name, method.desc));
    }

    /**
     * Appends a method to another class, taking care of references
     */
    public static void appendMethod(ClassNode original, ClassNode target, MethodNode method, MethodNode targetMethod) {
        if(targetMethod == null) {
            target.methods.add(method);
        } else {
            method.name += "_BetterFps";

            InsnList list = targetMethod.instructions;
            List<AbstractInsnNode> returns = findReturns(list);
            for(AbstractInsnNode r : returns) {
                list.insertBefore(r, insertMethod(original, method, target, targetMethod, r, true));
            }
        }
    }

    /**
     * Prepends a method to another class, taking care of references
     */
    public static void prependMethod(ClassNode original, ClassNode target, MethodNode method) {
        prependMethod(original, target, method, findMethod(target, method.name, method.desc));
    }

    /**
     * Prepends a method to another class, taking care of references
     */
    public static void prependMethod(ClassNode original, ClassNode target, MethodNode method, MethodNode targetMethod) {
        if(targetMethod == null) {
            target.methods.add(method);
        } else {
            method.name += "_BetterFps";

            InsnList list = targetMethod.instructions;
            AbstractInsnNode head = findHead(list);
            list.insert(head, insertMethod(original, method, target, targetMethod, head, true));
        }
    }

    /**
     * Copies a field to another class, taking care of references, and replacement
     */
    public static void copyField(ClassNode original, ClassNode target, FieldNode field, boolean replace) {
        copyField(original, target, field, findField(target, field.name, field.desc), replace);
    }

    /**
     * Copies a field to another class, taking care of references, and replacement
     */
    public static void copyField(ClassNode original, ClassNode target, FieldNode field, FieldNode replacedField, boolean replace) {
        if(replacedField != null) {
            if(!replace) return;
            target.fields.remove(replacedField);
        }

        replaceReferences(original.name, target.name, field);
        target.fields.add(field);
    }

}
