package me.guichaguri.betterfps.transformers;

import me.guichaguri.betterfps.BetterFps;
import me.guichaguri.betterfps.BetterFpsConfig;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.ASMUtils;
import me.guichaguri.betterfps.tweaker.Naming;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * @author Guilherme Chaguri
 */
public class MiscTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        if(Naming.C_Minecraft.is(name)) {
            return patchMinecraft(bytes);
        } else if(name.equals("net.minecraftforge.fml.client.FMLClientHandler")) {
            return patchForge(bytes);
        }

        return bytes;
    }


    public byte[] patchMinecraft(byte[] bytes) {
        BetterFpsConfig config = BetterFpsConfig.getConfig();
        if(config == null) config = BetterFpsHelper.loadConfig();

        ClassNode classNode = ASMUtils.readClass(bytes, ClassReader.SKIP_FRAMES);
        boolean patch = false;

        Iterator<FieldNode> i = classNode.fields.iterator();
        while(i.hasNext()) {
            FieldNode field = i.next();
            if((!config.preallocateMemory) && (Naming.F_memoryReserve.is(field.name, field.desc))) {
                i.remove();
                patch = true;
            }
        }

        for(MethodNode method : classNode.methods) {
            if(!config.preallocateMemory) {
                if((Naming.M_freeMemory.is(method.name, method.desc)) ||
                   (Naming.M_StaticBlock.is(method.name, method.desc))) {
                    int loc = -1;
                    for(int o = 0; o < method.instructions.size(); o++) {
                        AbstractInsnNode node = method.instructions.get(o);
                        if((node instanceof FieldInsnNode) && (node.getOpcode() == Opcodes.PUTSTATIC)) {
                            FieldInsnNode field = (FieldInsnNode) node;
                            if(Naming.F_memoryReserve.is(field.name, field.desc)) {
                                loc = o;
                                break;
                            }
                        }
                    }
                    if(loc != -1) {
                        AbstractInsnNode node1 = method.instructions.get(loc - 2);
                        AbstractInsnNode node2 = method.instructions.get(loc - 1);
                        AbstractInsnNode node3 = method.instructions.get(loc);

                        method.instructions.remove(node1);
                        method.instructions.remove(node2);
                        method.instructions.remove(node3);
                    }
                }
            }
        }

        if(patch) {
            return ASMUtils.writeClass(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        }

        return bytes;
    }

    public byte[] patchForge(byte[] bytes) {
        // This will add the BetterFps mod container
        BetterFps.log.info("Adding BetterFps Mod Container");

        ClassNode node = ASMUtils.readClass(bytes, 0);
        MethodNode mcLoad = ASMUtils.findMethod(node, "beginMinecraftLoading");
        MethodNode specialMods = ASMUtils.findMethod(node, "addSpecialModEntries");

        if(mcLoad == null) return bytes;
        if(specialMods == null) return bytes;

        final String ModMetadata = "net/minecraftforge/fml/common/ModMetadata";
        final String DummyModContainer = "net/minecraftforge/fml/common/DummyModContainer";
        final String StringType = "Ljava/lang/String;";

        FieldNode mod = new FieldNode(Opcodes.ACC_PRIVATE, "betterfps_mod", "L" + DummyModContainer + ";", null, null);
        node.fields.add(mod);

        int index = ASMUtils.getNextAvailableIndex(mcLoad.localVariables);
        InsnList l = new InsnList();

        l.add(new LabelNode());
        //ModMetadata m = new ModMetadata();
        l.add(new TypeInsnNode(Opcodes.NEW, ModMetadata));
        l.add(new InsnNode(Opcodes.DUP));
        l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ModMetadata, "<init>", "()V", false));
        l.add(new VarInsnNode(Opcodes.ASTORE, index));
        //m.modId = "BetterFps";
        setField(l, index, "BetterFps", ModMetadata, "modId", StringType);
        //m.name = "BetterFps";
        setField(l, index, "BetterFps", ModMetadata, "name", StringType);
        //m.version = BetterFpsHelper.VERSION;
        setField(l, index, BetterFpsHelper.VERSION, ModMetadata, "version", StringType);
        //m.url = BetterFpsHelper.URL;
        setField(l, index, BetterFpsHelper.URL, ModMetadata, "url", StringType);
        //m.description = "Performance Improvements";
        setField(l, index, "Performance Improvements", ModMetadata, "description", StringType);

        //m.authorList = Arrays.asList(new String[]{"Guichaguri"});
        l.add(new VarInsnNode(Opcodes.ALOAD, index));
        l.add(new InsnNode(Opcodes.ICONST_1));
        l.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));
        l.add(new InsnNode(Opcodes.DUP));
        l.add(new InsnNode(Opcodes.ICONST_0));
        l.add(new LdcInsnNode("Guichaguri"));
        l.add(new InsnNode(Opcodes.AASTORE));
        l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "asList",
                                "([Ljava/lang/Object;)Ljava/util/List;", false));
        l.add(new FieldInsnNode(Opcodes.PUTFIELD, ModMetadata, "authorList", "Ljava/util/List;"));

        //DummyModContainer d = new DummyModContainer(m);
        l.add(new VarInsnNode(Opcodes.ALOAD, 0));
        l.add(new TypeInsnNode(Opcodes.NEW, DummyModContainer));
        l.add(new InsnNode(Opcodes.DUP));
        l.add(new VarInsnNode(Opcodes.ALOAD, index));
        l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, DummyModContainer, "<init>", "(L" + ModMetadata + ";)V", false));
        l.add(new FieldInsnNode(Opcodes.PUTFIELD, node.name, mod.name, mod.desc));

        InsnList oldList = new InsnList();
        ASMUtils.addToInsnList(oldList, mcLoad.instructions.toArray());
        mcLoad.instructions.clear();
        mcLoad.instructions.add(ASMUtils.prependNodeList(oldList, l));
        LocalVariableNode vn = new LocalVariableNode("betterfps_m", "L" + ModMetadata + ";", null, null, null, index);
        ASMUtils.setVariableToMaxPeriod(mcLoad.instructions.toArray(), vn);
        mcLoad.localVariables.add(vn);

        l = new InsnList();

        //mods.add(betterfps_mod);
        l.add(new LabelNode());
        l.add(new VarInsnNode(Opcodes.ALOAD, 1));
        l.add(new VarInsnNode(Opcodes.ALOAD, 0));
        l.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, mod.name, mod.desc));
        l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false));
        l.add(new InsnNode(Opcodes.POP));

        oldList = new InsnList();
        ASMUtils.addToInsnList(oldList, specialMods.instructions.toArray());
        specialMods.instructions.clear();
        specialMods.instructions.add(ASMUtils.appendNodeList(oldList, l));


        return ASMUtils.writeClass(node, ClassWriter.COMPUTE_MAXS);
    }

    private void setField(InsnList l, int local, Object ldc, String owner, String field, String type) {
        l.add(new VarInsnNode(Opcodes.ALOAD, local));
        l.add(new LdcInsnNode(ldc));
        l.add(new FieldInsnNode(Opcodes.PUTFIELD, owner, field, type));
    }
}
