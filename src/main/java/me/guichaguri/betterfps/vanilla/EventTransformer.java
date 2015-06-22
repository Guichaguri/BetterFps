package me.guichaguri.betterfps.vanilla;

import java.util.Iterator;
import me.guichaguri.betterfps.transformers.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Used to handle events when its a vanilla client (not Forge)
 *
 * @author Guilherme Chaguri
 */
public class EventTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        try {
            if(Naming.C_Minecraft.is(name)) {
                /*ClassReader cr = new ClassReader(bytes);
                ClassWriter cw = new ClassWriter(cr, 1);
                MinecraftTransformer cv = new MinecraftTransformer(cw);
                cr.accept(cv, 0);
                return cw.toByteArray();*/
                return patchStart(bytes);
            } else if(Naming.C_KeyBinding.is(name)) {
                return patchKeyTick(bytes);
            } else if(Naming.C_GameSettings.is(name)) {
                return patchGameSettings(bytes);
            }
        } catch(Exception ex) {
           ex.printStackTrace();
        }

        return bytes;
    }

    public class MinecraftTransformer extends ClassVisitor {

        public MinecraftTransformer(ClassVisitor visitor) {
            super(Opcodes.ASM4, visitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
            if(Naming.M_startGame.is(name)) {
                System.out.println("START GAME PATCH");
                return new StartGameVisitor(visitor);
            } else {
                return visitor;
            }
        }

    }

    public class StartGameVisitor extends MethodVisitor {
        private int state = 0;
        public StartGameVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if((state == 0) && (opcode == Opcodes.NEW) && (Naming.C_GameSettings.isASM(type))) {
                state = 1;
            }
            super.visitTypeInsn(opcode, type);
        }
        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);
            if((state == 1) && (opcode == Opcodes.PUTFIELD) && (Naming.C_Minecraft.isASM(owner))) {
                state = 2;
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "me/guichaguri/betterfps/test/BetterFpsVanilla",
                        "start", "(L" + owner + ";)V", false);
            }
        }
    }

    private byte[] patchGameSettings(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(method.name.equals("<init>")) {
                LogManager.getLogger().info("Patching Game Settings...");
                InsnList list = new InsnList();
                for(AbstractInsnNode node : method.instructions.toArray()) {
                    list.add(node);
                    if(node instanceof FieldInsnNode) {
                        FieldInsnNode field = (FieldInsnNode)node;
                        if((field.getOpcode() == Opcodes.PUTFIELD) &&
                                (field.owner.equals(classNode.name)) &&
                                (field.name.equals("at"))) { // keyBindings
                            // TODO: deob naming

                            Label label = new Label();
                            list.add(new LabelNode(label));
                            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            list.add(new FieldInsnNode(Opcodes.GETFIELD, field.owner, field.name, field.desc));
                            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "me/guichaguri/betterfps/test/BetterFpsVanilla", "MENU_KEY", "Lbsr;"));
                            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/apache/commons/lang3/ArrayUtils", "add", "([Ljava/lang/Object;Ljava/lang/Object;)[Ljava/lang/Object;", false));
                            list.add(new TypeInsnNode(Opcodes.CHECKCAST, "[Lbsr;"));
                            list.add(new FieldInsnNode(Opcodes.PUTFIELD, field.owner, field.name, field.desc));
                            continue;
                        }
                    }
                }

                method.instructions.clear();
                method.instructions.add(list);
                patch = true;
            }
        }

        if(!patch) return bytes;

        ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
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
                if(Naming.M_startGame.is(method.name)) {
                    if(method.exceptions != null) {
                        for(String s : method.exceptions) {
                            System.out.println(s);
                        }
                    }
                    LogManager.getLogger().info("Patching Game Start...");
                    InsnList list = new InsnList();

                    for(AbstractInsnNode node : method.instructions.toArray()) {
                        if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return
                            // Load the var 0 (that is "this" in Minecraft.startGame)
                            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            // Invoke BetterFpsVanilla.start
                            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "me/guichaguri/betterfps/test/BetterFpsVanilla",
                                    "start", "(L" + classNode.name + ";)V", false));
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

    private byte[] patchKeyTick(byte[] bytes) throws Exception {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        boolean patch = false;

        while(methods.hasNext()) {
            MethodNode method = methods.next();

            if(method.desc.equals("(I)V")) {
                if(Naming.M_onTick.is(method.name)) {
                    LogManager.getLogger().info("Patching Key Event...");
                    InsnList list = new InsnList();
                    for(AbstractInsnNode node : method.instructions.toArray()) {
                        if(node.getOpcode() == Opcodes.RETURN) { // Just before adding the return

                            list.add(new VarInsnNode(Opcodes.ILOAD, 0));
                            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "me/guichaguri/betterfps/test/BetterFpsVanilla", "keyEvent", "(I)V", false));

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
