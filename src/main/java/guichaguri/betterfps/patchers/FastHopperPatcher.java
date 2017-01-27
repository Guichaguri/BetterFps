package guichaguri.betterfps.patchers;

import guichaguri.betterfps.ASMUtils;
import guichaguri.betterfps.transformers.patcher.IClassPatcher;
import guichaguri.betterfps.transformers.patcher.Patch;
import guichaguri.betterfps.tweaker.Mappings;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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

        JumpInsnNode ifNull = ASMUtils.findNode(inst, JumpInsnNode.class, Opcodes.IFNULL, 0);
        JumpInsnNode ifEnd = null;

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
