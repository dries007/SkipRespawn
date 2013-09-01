package net.dries007.skiprespawn;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Arrays;
import java.util.Map;

public class SkipRespawn extends DummyModContainer implements IFMLLoadingPlugin
{
    public static final String MODID = "SkipRespawn";
    private static final String MCVERSION = "[1.6.2]";

    public SkipRespawn()
    {
        super(new ModMetadata());
        ModMetadata meta = super.getMetadata();
        meta.modId = MODID;
        meta.name = MODID;
        meta.description = "Skip that stupid respawn screen!";
        meta.version = "1.0";
        meta.url = "https://github.com/dries007/SkipRespawn";
        meta.authorList = Arrays.asList("Dries007", "Woutwoot");
    }

    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {ClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return this.getClass().getName();
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return VersionParser.parseRange(MCVERSION);
    }
}
