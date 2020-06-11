package com.westeroscraft.westerosentities;

import com.westeroscraft.westerosentities.commands.CommandWCDirewolf;
import net.minecraft.entity.projectile.EntityEgg;
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
    public static final String VERSION = "1.12.2-1.2.0";

    private static Logger logger;
    File dwfPersistenceFile;
    public CommandWCDirewolf commandWCDirewolf;

    @SidedProxy(clientSide="com.westeroscraft.westerosentities.ClientProxy", serverSide="com.westeroscraft.westerosentities.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance("westerosentities")
    public static WesterosEntities instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(ModEntities.class);
        EntityEgg eg;
        /*
        Persistence bullshit
        will probably get removed soon
         */
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
