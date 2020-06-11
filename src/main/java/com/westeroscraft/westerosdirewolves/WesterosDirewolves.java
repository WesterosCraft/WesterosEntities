package com.westeroscraft.westerosdirewolves;

import com.westeroscraft.westerosdirewolves.commands.CommandWCDirewolf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod(modid = WesterosDirewolves.MODID, name = WesterosDirewolves.NAME, version = WesterosDirewolves.VERSION)
public class WesterosDirewolves {
    public static final String MODID = "westerosdirewolves";
    public static final String NAME = "WesterosDirewolves";
    public static final String VERSION = "1.12.2-1.1.0";

    private static Logger logger;
    File dwfPersistenceFile;
    public CommandWCDirewolf commandWCDirewolf;

    @SidedProxy(clientSide="com.westeroscraft.westerosdirewolves.ClientProxy", serverSide="com.westeroscraft.westerosdirewolves.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("westerosdirewolves")
    public static WesterosDirewolves instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(ModEntities.class);
        dwfPersistenceFile = new File(event.getModConfigurationDirectory(), "direwolves.json");
        // create the file if it doesn't already exist
        try {
            dwfPersistenceFile.createNewFile();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Unable to create direwolves.json file");
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
    }

    @EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        commandWCDirewolf = new CommandWCDirewolf(dwfPersistenceFile, logger);
        event.registerServerCommand(commandWCDirewolf);
        MinecraftForge.EVENT_BUS.register(new LogOffCleanup(commandWCDirewolf));
    }

    /*
    Cleanup direwolves when the server shuts down
     */
    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        logger.log(Level.INFO, "Deleting " + commandWCDirewolf.direwolvesByPlayer.size() + " direwolves");
        commandWCDirewolf.cleanupDirewolves();
    }

}
