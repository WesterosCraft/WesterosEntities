package com.westeroscraft.westerosdirewolves;

import com.westeroscraft.westerosdirewolves.commands.CommandWCDirewolf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = WesterosDirewolves.MODID, name = WesterosDirewolves.NAME, version = WesterosDirewolves.VERSION)
public class WesterosDirewolves {
    public static final String MODID = "westerosdirewolves";
    public static final String NAME = "WesterosDirewolves";
    public static final String VERSION = "1.12.2-0.1.0";

    private static Logger logger;
    public CommandWCDirewolf commandWCDirewolf;

    @SidedProxy(clientSide="com.westeroscraft.westerosdirewolves.ClientProxy", serverSide="com.westeroscraft.westerosdirewolves.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("westerosdirewolves")
    public static WesterosDirewolves instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(ModEntities.class);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
    }

    @EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        commandWCDirewolf = new CommandWCDirewolf();
        event.registerServerCommand(commandWCDirewolf);
        MinecraftForge.EVENT_BUS.register(new LogOffCleanup(commandWCDirewolf));
    }

}
