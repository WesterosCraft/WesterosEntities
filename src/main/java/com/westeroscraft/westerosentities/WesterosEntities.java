package com.westeroscraft.westerosentities;

import com.westeroscraft.westerosentities.commands.CommandWCDirewolf;
import com.westeroscraft.westerosentities.persistence.PlayerEntitiesDatabaseHandler;
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

@Mod(modid = WesterosEntities.MODID, name = WesterosEntities.NAME, version = WesterosEntities.VERSION)
public class WesterosEntities {
    public static final String MODID = "westerosentities";
    public static final String NAME = "WesterosEntities";
    public static final String VERSION = "1.12.2-1.3.0";

    private static Logger logger;
    PlayerEntitiesDatabaseHandler dbHandler;
    public CommandWCDirewolf commandWCDirewolf;

    @SidedProxy(clientSide="com.westeroscraft.westerosentities.ClientProxy", serverSide="com.westeroscraft.westerosentities.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("westerosentities")
    public static WesterosEntities instance;

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
        dbHandler = new PlayerEntitiesDatabaseHandler(logger);
        commandWCDirewolf = new CommandWCDirewolf(dbHandler, logger);
        event.registerServerCommand(commandWCDirewolf);
        MinecraftForge.EVENT_BUS.register(new LogOffCleanup(commandWCDirewolf));
    }

    /*
    Cleanup direwolves when the server shuts down
    Close the database connection
     */
    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        logger.log(Level.INFO, "Deleting " + commandWCDirewolf.direwolvesByPlayer.size() + " direwolves");
        commandWCDirewolf.cleanupDirewolves();
    }

}
