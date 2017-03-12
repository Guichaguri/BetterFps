package guichaguri.betterfps.tweaker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 *
 * @author Guilherme Chaguri
 */
public enum Mappings {

    // TODO: new mapping system. Maybe reading this class by a gradle task and saving the map in a file?

    // Prefixes
    // C_ is for classes
    // M_ is for methods
    // F_ is for fields

    M_StaticBlock(Type.METHOD, "StaticBlock", "<clinit>"),
    M_Constructor(Type.METHOD, "Constructor", "<init>"),

    C_Minecraft(Type.CLASS, "Minecraft"),
    C_World(Type.CLASS, "World"),
    C_Chunk(Type.CLASS, "Chunk"),
    C_Block(Type.CLASS, "Block"),
    C_EntityPlayer(Type.CLASS, "EntityPlayer"),
    C_EntityPlayerSP(Type.CLASS, "EntityPlayerSP"),
    C_MathHelper(Type.CLASS, "MathHelper"),
    C_PrimedTNT(Type.CLASS, "EntityTNTPrimed"),
    C_ClientBrandRetriever(Type.CLASS, "ClientBrandRetriever"),
    C_GuiOptions(Type.CLASS, "GuiOptions"),
    C_WorldClient(Type.CLASS, "WorldClient"),
    C_WorldServer(Type.CLASS, "WorldServer"),
    C_IntegratedServer(Type.CLASS, "IntegratedServer"),
    C_DedicatedServer(Type.CLASS, "DedicatedServer"),
    C_TileEntityBeacon(Type.CLASS, "TileEntityBeacon"),
    C_TileEntityBeaconRenderer(Type.CLASS, "TileEntityBeaconRenderer"),
    C_BeamSegment(Type.CLASS, "TileEntityBeacon$BeamSegment"),
    C_TileEntityHopper(Type.CLASS, "TileEntityHopper"),
    C_BlockHopper(Type.CLASS, "BlockHopper"),
    C_ModelBox(Type.CLASS, "ModelBox"),
    C_EntityRenderer(Type.CLASS, "EntityRenderer"),
    C_IInventory(Type.CLASS, "IInventory"),
    C_GuiContainerCreative(Type.CLASS, "GuiContainerCreative"),
    C_RenderPlayer(Type.CLASS, "RenderPlayer"),

    M_startGame(Type.METHOD, "startGame"), // Minecraft
    M_sin(Type.METHOD, "sin"), // MathHelper
    M_cos(Type.METHOD, "cos"), // MathHelper
    M_tick(Type.METHOD, "tick"), // World
    M_onUpdate(Type.METHOD, "onUpdate"), // Entity
    M_updateBlocks(Type.METHOD, "updateBlocks"), // World
    M_getClientModName(Type.METHOD, "getClientModName"), // ClientBrandRetriever
    M_freeMemory(Type.METHOD, "freeMemory"), // Minecraft
    M_initGui(Type.METHOD, "initGui"), // GuiScreen
    M_startServer(Type.METHOD, "startServer"), // MinecraftServer
    M_captureDroppedItems(Type.METHOD, "captureDroppedItems"), // TileEntityHopper
    M_getHopperInventory(Type.METHOD, "getHopperInventory"), // TileEntityHopper

    F_memoryReserve(Type.FIELD, "memoryReserve"), // Minecraft
    F_SIN_TABLE(Type.FIELD, "SIN_TABLE"); // MathHelper

    public static void loadMappings(InputStream srg) throws IOException {
        List<String> lines = IOUtils.readLines(srg, "UTF-8");
        for(String line : lines) {
            String[] m = line.split(" ");
            String identifier = m[m.length - 1];

            for(Mappings mp : values()) {
                if(m[0].equals("CL:")) {
                    if(m.length >= 4 && mp.type == Type.CLASS && mp.identifier.equals(identifier)) {
                        mp.deobfName = m[2];
                        mp.obfName = m[1];
                    }
                } else if(m[0].equals("FD:")) {
                    if(m.length >= 4 && mp.type == Type.FIELD && mp.identifier.equals(identifier)) {
                        loadNames(m, mp, 2, 1);
                    }
                } else if(m[0].equals("MD:")) {
                    if(m.length >= 6 && mp.type == Type.METHOD && mp.identifier.equals(identifier)) {
                        loadNames(m, mp, 3, 1);
                        mp.deobfDesc = m[4];
                        mp.obfDesc = m[2];
                    }
                }
            }
        }
    }

    private static void loadNames(String[] m, Mappings mp, int deobf, int obf) {
        String[] name = m[deobf].split("/");
        mp.deobfName = name[name.length - 1];
        mp.ownerDeobfName = StringUtils.join(name, "/", 0, name.length - 1);

        name = m[obf].split("/");
        mp.obfName = name[name.length - 1];
        mp.ownerObfName = StringUtils.join(name, "/", 0, name.length - 1);
    }

    public final Type type;
    public final String identifier;
    protected String deobfName, obfName;
    protected String ownerDeobfName, ownerObfName;
    protected String deobfDesc, obfDesc;

    Mappings(Type type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    Mappings(Type type, String identifier, String name) {
        this(type, identifier);
        this.deobfName = name;
        this.obfName = name;
    }

    public boolean is(String n) {
        n = n.replaceAll("\\.", "/");
        return deobfName.equals(n) || obfName.equals(n);
    }

    public boolean isObf(String n) {
        return !deobfName.equals(obfName) && obfName.equals(n.replaceAll("\\.", "/"));
    }

    public boolean isDesc(String n) {
        return (deobfDesc != null && n.equals(deobfDesc)) || (obfDesc != null && n.equals(obfDesc));
    }

    public boolean isOwner(String n) {
        n = n.replaceAll("\\.", "/");
        return ownerDeobfName == null || ownerObfName == null || n.equals(ownerDeobfName) || n.equals(ownerObfName);
    }

    public boolean is(String n, String d) {
        return is(n) && isDesc(d);
    }

    public boolean is(MethodNode method) {
        return (method.name.equals(deobfName) || method.name.equals(obfName)) && isDesc(method.desc);
    }

    public boolean is(FieldNode field) {
        return (field.name.equals(deobfName) || field.name.equals(obfName)) && isDesc(field.desc);
    }

    public boolean is(MethodInsnNode method) {
        return is(method.name) && isDesc(method.desc) && isOwner(method.owner);
    }

    public boolean is(FieldInsnNode field) {
        return is(field.name) && isDesc(field.desc) && isOwner(field.owner);
    }

    @Override
    public String toString() {
        return identifier;
    }

    enum Type {
        CLASS, METHOD, FIELD
    }

}
