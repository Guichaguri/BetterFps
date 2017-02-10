package guichaguri.betterfps.patchers;

import guichaguri.betterfps.transformers.ASMUtils;
import guichaguri.betterfps.transformers.IClassPatcher;
import guichaguri.betterfps.transformers.Patch;
import guichaguri.betterfps.tweaker.Mappings;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Guilherme Chaguri
 */
public class FastHopperPatcher implements IClassPatcher {

    public static final String PICKUP_ITEMS = "pickupItems";

    @Override
    public void patch(Patch patch) {
        ClassNode source = patch.getSourceClass();
        ClassNode target = patch.getTargetClass();

        MethodNode method = ASMUtils.findMethod(target, Mappings.M_captureDroppedItems);
        if(method == null) return; // Method doesn't exist?

        MethodNode pickupItem = patch.getMethod(PICKUP_ITEMS);

        InsnList inst = method.instructions;

        List<JumpInsnNode> ifNullNodes = ASMUtils.findNodes(inst, JumpInsnNode.class, Opcodes.IFNULL);
        LocalVariableNode inv = ASMUtils.findVariable(method, Mappings.C_IInventory);

        if(ifNullNodes == null || ifNullNodes.isEmpty() || inv == null) return; // Wut

        JumpInsnNode ifNull = null;
        JumpInsnNode ifEnd = null;

        for(JumpInsnNode node : ifNullNodes) {
            AbstractInsnNode previous = node.getPrevious();

            if(previous instanceof VarInsnNode) {
                if(((VarInsnNode)previous).var == inv.index) {
                    ifNull = node;
                    break;
                }
            }
        }

        if(ifNull == null) return; // Wut

        for(int i = inst.indexOf(ifNull.label) - 1; i >= 0; i--) {
            AbstractInsnNode node = inst.get(i);
            if(node instanceof JumpInsnNode && node.getOpcode() == Opcodes.GOTO) {
                ifEnd = (JumpInsnNode)node;
                break;
            }
        }

        if(ifEnd == null) return; // Wut

        InsnList ifCanPickupItems = ASMUtils.insertMethod(source, pickupItem, target, method, ifNull.label, false);
        ifCanPickupItems.add(new JumpInsnNode(Opcodes.IFEQ, ifEnd.label));
        inst.insert(ifNull.label, ifCanPickupItems);
    }

}
