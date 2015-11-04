package me.guichaguri.betterfps.transformers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.guichaguri.betterfps.BetterFps;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.transformers.ClonerTransformer.CopyMode.Mode;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 * This class will clone .class methods/fields to the real class
 * @author Guilherme Chaguri
 */
public class ClonerTransformer implements IClassTransformer {

    private static final List<Clone> clones = new ArrayList<Clone>();

    public static void addClone(String clazz, Naming target) {
        clones.add(new Clone(clazz.replaceAll("\\.", "/"), target));
    }

    static {
        addClone("me.guichaguri.betterfps.clones.tileentity.BeaconLogic", Naming.C_TileEntityBeacon);
        addClone("me.guichaguri.betterfps.clones.tileentity.HopperLogic", Naming.C_TileEntityHopper);
        addClone("me.guichaguri.betterfps.clones.block.HopperBlock", Naming.C_BlockHopper);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return bytes;

        List<Clone> foundClones = null;
        for(Clone c : clones) {
            if(c.target.is(name)) {
                if(foundClones == null) foundClones = new ArrayList<Clone>();
                foundClones.add(c);
            }
        }

        if(foundClones != null) {
            BetterFps.log.info("Found " + foundClones.size() + " class patches for " + name);
            return patchClones(foundClones, bytes);
        }

        return bytes;
    }

    public byte[] patchClones(List<Clone> clones, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean patched = false;
        ClassReader reader;

        for(Clone c : clones) {

            try {
                if(BetterFpsHelper.LOC == null) { // Development or vanilla environment?
                    reader = new ClassReader(c.clonePath);
                } else { // Forge environment
                    JarFile jar = new JarFile(BetterFpsHelper.LOC);
                    ZipEntry e = jar.getEntry(c.clonePath + ".class");
                    reader = new ClassReader(jar.getInputStream(e));
                    jar.close();
                }

                ClassNode cloneClass = new ClassNode();
                reader.accept(cloneClass, 0);

                for(FieldNode field : cloneClass.fields) {
                    CopyMode.Mode mode = Mode.REPLACE;
                    Naming name = null;
                    if(field.visibleAnnotations != null) {
                        mode = getCopyMode(field.visibleAnnotations);
                        name = getNaming(field.visibleAnnotations);
                    }
                    cloneField(field, classNode, mode, name);
                    patched = true;
                }

                for(MethodNode method : cloneClass.methods) {
                    CopyMode.Mode mode = Mode.REPLACE;
                    Naming name = null;
                    if(method.visibleAnnotations != null) {
                        mode = getCopyMode(method.visibleAnnotations);
                        name = getNaming(method.visibleAnnotations);
                    }
                    cloneMethod(method, classNode, cloneClass, mode, name);
                    patched = true;
                }

            } catch(Exception ex) {
                BetterFps.log.error("Could not patch with " + c.clonePath + ": " + ex.getLocalizedMessage());
            }

        }

        if(!patched) return bytes;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private CopyMode.Mode getCopyMode(List<AnnotationNode> annotations) {
        for(AnnotationNode node : annotations) {
            if(node.desc.equals(Type.getDescriptor(CopyMode.class))) {
                if(node.values == null) continue;

                for(int x = 0; x < node.values.size() - 1; x += 2) {
                    Object key = node.values.get(x);
                    Object value = node.values.get(x + 1);
                    if((key instanceof String) && (key.equals("value")) && (value instanceof String[])) {
                        return CopyMode.Mode.valueOf(((String[])value)[1]);
                    }
                }
            }
        }
        return Mode.REPLACE;
    }

    private Naming getNaming(List<AnnotationNode> annotations) {
        for(AnnotationNode node : annotations) {
            if(node.desc.equals(Type.getDescriptor(Named.class))) {
                if(node.values == null) continue;

                for(int x = 0; x < node.values.size() - 1; x += 2) {
                    Object key = node.values.get(x);
                    Object value = node.values.get(x + 1);
                    if((key instanceof String) && (key.equals("value")) && (value instanceof String[])) {
                        return Naming.valueOf(((String[])value)[1]);
                    }
                }
            }
        }
        return null;
    }


    private void cloneField(FieldNode e, ClassNode node, Mode mode, Naming name) {
        if(mode == Mode.IGNORE) return;
        for(int i = 0; i < node.fields.size(); i++) {
            FieldNode field = node.fields.get(i);
            boolean b = false;
            if((name != null) && (name.is(field.name, field.desc))) {
                b = true;
                e.name = field.name;
                e.desc = field.desc;
            } else if((field.name.equals(e.name)) && (field.desc.equals(e.desc))) {
                b = true;
            }
            if(b) {
                if(mode == Mode.ADD_IF_NOT_EXISTS) return;
                node.fields.remove(field);
                break;
            }
        }
        node.fields.add(e);
    }

    private boolean cloneMethod(MethodNode e, ClassNode node, ClassNode original, Mode mode, Naming name) {
        if(mode == Mode.IGNORE) return false;
        for(int i = 0; i < node.methods.size(); i++) {
            MethodNode method = node.methods.get(i);
            boolean b = false;
            if((name != null) && (name.is(method.name)) && (method.desc.equals(e.desc))) {
                b = true;
                e.name = method.name;
                e.desc = method.desc;
            } else if((method.name.equals(e.name)) && (method.desc.equals(e.desc))) {
                b = true;
            }
            if(b) {
                if(mode == Mode.ADD_IF_NOT_EXISTS) return false;
                node.methods.remove(method);
                break;
            }
        }
        replaceOcurrences(e, node, original);
        node.methods.add(e);
        return true;
    }

    private void replaceOcurrences(MethodNode method, ClassNode classNode, ClassNode original) {
        String originalDesc = "L" + original.name + ";";
        String classDesc = "L" + classNode.name + ";";
        for(AbstractInsnNode node : method.instructions.toArray()) {
            if(node instanceof FieldInsnNode) {
                FieldInsnNode f = (FieldInsnNode)node;
                if(f.owner.equals(original.name)) {
                    f.owner = classNode.name;
                }
            } else if(node instanceof MethodInsnNode) {
                MethodInsnNode m = (MethodInsnNode)node;
                if(m.owner.equals(original.name)) {
                    m.owner = classNode.name;
                }
            } else if(node instanceof TypeInsnNode) {
                TypeInsnNode t = (TypeInsnNode)node;
                if(t.desc.equals(original.name)) {
                    t.desc = classNode.name;
                }
            }
        }
        for(LocalVariableNode var : method.localVariables) {
            if(var.desc == originalDesc) {
                var.desc = classDesc;
            }
        }
    }

    public static class Clone {
        public final String clonePath;
        public final Naming target;

        public Clone(String clonePath, Naming target) {
            this.clonePath = clonePath;
            this.target = target;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface CopyMode {

        public Mode value(); // Mode that the object will be copied, not needed if you'll use REPLACE

        public enum Mode {
            REPLACE, ADD_IF_NOT_EXISTS, IGNORE
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
    public static @interface Named {

        public Naming value(); // Original name of the class/method/field

    }

}
