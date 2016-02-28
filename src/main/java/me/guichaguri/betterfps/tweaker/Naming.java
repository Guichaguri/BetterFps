package me.guichaguri.betterfps.tweaker;

/**
 *
 * @author Guilherme Chaguri
 */
public enum Naming { // TODO BUKKIT

    // Prefixes
    // C_ is for classes
    // M_ is for methods
    // F_ is for fields

    M_StaticBlock("<clinit>", null, null, "()V"),
    M_Constructor("<init>", null, null, "()V"),

    C_Minecraft("net.minecraft.client.Minecraft", null), //TODO obfuscated
    C_KeyBinding("net.minecraft.client.settings.KeyBinding", null), //TODO obfuscated
    C_World("net.minecraft.world.World", null, null), //TODO obfuscated + bukkit
    C_Chunk("net.minecraft.world.chunk.Chunk", null, null), // TODO obfuscated + bukkit
    C_ChunkCoordIntPair("net.minecraft.world.ChunkCoordIntPair", null, null), // TODO obfuscated + bukkit
    C_EntityPlayer("net.minecraft.entity.player.EntityPlayer", null, null), // TODO obfuscated + bukkit
    C_MathHelper("net.minecraft.util.MathHelper", null, null), // TODO obfuscated + bukkit
    C_PrimedTNT("net.minecraft.entity.item.EntityTNTPrimed", null, null), //TODO obfuscated + bukkit
    C_ClientBrandRetriever("net.minecraft.client.ClientBrandRetriever", null),
    C_GuiOptions("net.minecraft.client.gui.GuiOptions", null), //TODO obfuscated
    C_WorldClient("net.minecraft.client.multiplayer.WorldClient", null), // TODO obfuscated
    C_WorldServer("net.minecraft.world.WorldServer", null, null), // TODO obfuscated + bukkit
    C_IntegratedServer("net.minecraft.server.integrated.IntegratedServer", null), // TODO obfuscated
    C_DedicatedServer("net.minecraft.server.dedicated.DedicatedServer", null, null), // TODO obfuscated + bukkit
    C_TileEntityBeacon("net.minecraft.tileentity.TileEntityBeacon", null, null), // TODO obfuscated + bukkit
    C_BeamSegment("net.minecraft.tileentity.TileEntityBeacon$BeamSegment", null, null), // TODO obfuscated + bukkit
    C_TileEntityHopper("net.minecraft.tileentity.TileEntityHopper", null, null), // TODO obfuscated + bukkit
    C_BlockHopper("net.minecraft.block.BlockHopper", null, null), // TODO obfuscated + bukkit
    C_ModelBox("net.minecraft.client.model.ModelBox", null), // TODO obfuscated
    C_EntityRenderer("net.minecraft.client.renderer.EntityRenderer", null),

    M_startGame("startGame", null, null, "()V"), //TODO obfuscated // Minecraft
    M_onTick("onTick", null, null, "(I)V"), //TODO obfuscated // KeyBinding
    M_sin("sin", "a", null, "(F)F"), // MathHelper
    M_cos("cos", "b", null, "(F)F"), // MathHelper
    M_tick("tick", null, null, "()V"), //TODO obfuscated // World
    M_onUpdate("onUpdate", null, null, "()V"), //TODO obfuscated // Entity
    M_updateBlocks("updateBlocks", null, null, "()V"), // TODO obfuscated // World
    M_getClientModName("getClientModName", null, null, "()Ljava/lang/String;"), // ClientBrandRetriever
    M_freeMemory("freeMemory", null, null, "()V"), // TODO obfuscated // Minecraft
    M_initGui("initGui", null, null, "()V"), // TODO obfuscated // GuiScreen
    M_startServer("startServer", null, null, "()Z"), // TODO obfuscated // MinecraftServer
    M_setActivePlayerChunksAndCheckLight("setActivePlayerChunksAndCheckLight", null, null, "()V"), // TODO obfuscated // World
    M_captureDroppedItems("captureDroppedItems", null, null, null), // TODO obfuscated // TileEntityHopper

    F_memoryReserve("memoryReserve", null, null, "[B"), // TODO obfuscated // Minecraft
    F_SIN_TABLE("SIN_TABLE", "a", null, "[F"); // MathHelper


    private String deob;
    private String deobRepl;
    private String ob;
    private String obRepl;
    private String bukkit;
    private String bukkitRepl;

    private boolean useOb = false;
    private boolean useBukkit = false;

    // Only used for methods and fields
    private String desc;

    // For client classes only
    Naming(String deob, String ob) {
        this(deob, ob, null);
    }

    // For common classes
    Naming(String deob, String ob, String bukkit) {
        this.deob = deob;
        this.deobRepl = deob.replaceAll("\\.", "/");
        if(ob != null) {
            this.useOb = true;
            this.ob = ob;
            this.obRepl = ob.replaceAll("\\.", "/");
        }
        if(bukkit != null) {
            this.useBukkit = true;
            this.bukkit = bukkit;
            this.bukkitRepl = bukkit.replaceAll("\\.", "/");
        }
    }

    // For methods/fields
    Naming(String deob, String ob, String bukkit, String desc) {
        this(deob, ob, bukkit);
        this.desc = desc;
    }

    // Used to check names
    public boolean is(String name) {
        if(name.equals(deob)) return true;
        if(useOb && name.equals(ob)) return true;
        if(useBukkit && name.equals(bukkit)) return true;
        return false;
    }

    // Used to check class names inside ASM
    public boolean isASM(String name) {
        if(name.equals(deobRepl)) return true;
        if(useOb && name.equals(obRepl)) return true;
        if(useBukkit && name.equals(bukkitRepl)) return true;
        return false;
    }

    // Used to check names and desc
    public boolean is(String name, String desc) {
        if((this.desc.equals(desc)) && (is(name))) {
            return true;
        }
        return false;
    }

}
