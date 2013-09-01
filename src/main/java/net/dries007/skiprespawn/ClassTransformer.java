package net.dries007.skiprespawn;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;

public class ClassTransformer implements IClassTransformer
{
    private static final HashMap<String, String> DEOBF = getDeobf();
    private static final HashMap<String, String> OBF = getObf();

    private static HashMap<String, String> getDeobf()
    {
        HashMap<String, String> hm = new HashMap<String, String>();

        hm.put("className", "net.minecraft.client.gui.GuiGameOver");
        hm.put("targetMethodName", "drawScreen");
        hm.put("javaClassName", "net/minecraft/client/gui/GuiGameOver");
        hm.put("mcName", "mc");
        hm.put("javaMinecraftName", "net/minecraft/client/Minecraft");
        hm.put("thePlayerName", "thePlayer");
        hm.put("javaEntityClientPlayerMPName", "net/minecraft/client/entity/EntityClientPlayerMP");
        hm.put("respawnPlayerMethod", "respawnPlayer");
        hm.put("javaGuiScreenName", "net/minecraft/client/gui/GuiScreen");
        hm.put("displayGuiScreenMethod", "displayGuiScreen");

        return hm;
    }

    private static HashMap<String, String> getObf()
    {
        HashMap<String, String> hm = new HashMap<String, String>();

        hm.put("className", "auz");
        hm.put("targetMethodName", "a");
        hm.put("javaClassName", "auz");
        hm.put("mcName", "f");
        hm.put("javaMinecraftName", "ats");
        hm.put("thePlayerName", "h");
        hm.put("javaEntityClientPlayerMPName", "bdf");
        hm.put("respawnPlayerMethod", "bz");
        hm.put("javaGuiScreenName", "awb");
        hm.put("displayGuiScreenMethod", "a");

        return hm;
    }

    private static boolean applied;

    @Override
    public byte[] transform(String name, String s2, byte[] bytes)
    {
        if (name.equals(DEOBF.get("className")))
            return dotransform(name, bytes, DEOBF);
        else if (name.equals(OBF.get("className")))
            return dotransform(name, bytes, OBF);

        return bytes;
    }

    private byte[] dotransform(String name, byte[] bytes, HashMap<String, String> hm)
    {
        System.out.println("[SkipRespawn] Transforming " + name + " (GuiGameOver)");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();

        while (methods.hasNext())
        {
            MethodNode m = methods.next();

            try
            {
                if (m.name.equals(hm.get("targetMethodName")) && m.desc.equals("(IIF)V"))
                {
                    System.out.println("[SkipRespawn] Found " + m.name + " (drawScreen)");

                    InsnList toInject = new InsnList();

                    LabelNode l0 = new LabelNode(new Label());
                    LabelNode l1 = new LabelNode(new Label());
                    LabelNode l2 = new LabelNode(new Label());
                    LabelNode l3 = new LabelNode(new Label());

                    toInject.add(l0);
                    toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    toInject.add(new FieldInsnNode(Opcodes.GETFIELD, hm.get("javaClassName"), hm.get("mcName"), "L" + hm.get("javaMinecraftName") + ";"));
                    toInject.add(new FieldInsnNode(Opcodes.GETFIELD, hm.get("javaMinecraftName"), hm.get("thePlayerName"), "L" + hm.get("javaEntityClientPlayerMPName") + ";"));
                    toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, hm.get("javaEntityClientPlayerMPName"), hm.get("respawnPlayerMethod"), "()V"));
                    toInject.add(l1);

                    toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    toInject.add(new FieldInsnNode(Opcodes.GETFIELD, hm.get("javaClassName"), hm.get("mcName"), "L" + hm.get("javaMinecraftName") + ";"));
                    toInject.add(new InsnNode(Opcodes.ACONST_NULL));
                    toInject.add(new TypeInsnNode(Opcodes.CHECKCAST, hm.get("javaGuiScreenName")));
                    toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, hm.get("javaMinecraftName"), hm.get("displayGuiScreenMethod"), "(L" + hm.get("javaGuiScreenName") + ";)V"));
                    toInject.add(l2);

                    toInject.add(new InsnNode(Opcodes.RETURN));
                    toInject.add(l3);

                    m.instructions.insert(m.instructions.get(0), toInject);

                    applied = true;
                    System.out.println("[SkipRespawn] Patching " + m.name + " (drawScreen) Complete!");
                }
            }
            catch (Exception e)
            {
                System.out.println("[SkipRespawn] ERROR while patching " + m.name + " (drawScreen)");
                e.printStackTrace();
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        if (!applied)
        {
            System.out.println("########################################################");
            System.out.println("#####                   WARNING                    #####");
            System.out.println("##     [SkipRespawn] Patching GuiGameOver FAILED!     ##");
            System.out.println("########################################################");
        }
        return writer.toByteArray();
    }
}
