package com.westeroscraft.westerosentities.commands;

import com.westeroscraft.westerosentities.persistence.DirewolfData;
import com.westeroscraft.westerosentities.persistence.DirewolfStorage;
import com.westeroscraft.westerosentities.entities.EntityDirewolf;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommandWCDirewolf extends CommandBase {

    // stores information about who owns which direwolf
    public final HashMap<UUID, EntityDirewolf> direwolvesByPlayer;
    private File dwfPersistenceFile;
    private Logger logger;
    private DirewolfStorage direwolfStorageHandler;

    public CommandWCDirewolf(File dwfPersistenceFile, Logger logger) {
        this.logger = logger;
        this.dwfPersistenceFile = dwfPersistenceFile;
        direwolfStorageHandler = new DirewolfStorage(dwfPersistenceFile, logger);
        logger.log(Level.INFO, "CommandWCDirewolf got persistence file: " + dwfPersistenceFile.getPath());
        direwolvesByPlayer = new HashMap<UUID, EntityDirewolf>();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (!sender.getEntityWorld().isRemote) {
            if (!(sender instanceof EntityPlayer)) {
                // we got called by a non-player. something's not right
                return;
            }
            // parse arguments
            int coat = new Random().nextInt(12);
            String name = "";
            if (params.length != 0) {
                // parse coat preference
                switch (params[0]) {
                    case "amber":
                        coat = EntityDirewolf.AMBER;
                        break;
                    case "black":
                        coat = EntityDirewolf.BLACK;
                        break;
                    case "blackwhite":
                        coat = EntityDirewolf.BLACKWHITE;
                        break;
                    case "brown":
                        coat = EntityDirewolf.BROWN;
                        break;
                    case "chestnut":
                        coat = EntityDirewolf.CHESTNUT;
                        break;
                    case "cinnamon":
                        coat = EntityDirewolf.CINNAMON;
                        break;
                    case "grey":
                        coat = EntityDirewolf.GREY;
                        break;
                    case "greywhite":
                        coat = EntityDirewolf.GREYWHITE;
                        break;
                    case "mixed":
                        coat = EntityDirewolf.MIXED;
                        break;
                    case "tan":
                        coat = EntityDirewolf.TAN;
                        break;
                    case "white":
                        coat = EntityDirewolf.WHITE;
                        break;
                    case "whitegrey":
                        coat = EntityDirewolf.WHITEGREY;
                        break;
                    default:
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid coat provided; choosing randomly. Use /help wcdirewolf for a list of valid colorings."));
                }
                // parse the name, if it has been given
                if (params.length > 1) {
                    name = params[1];
                }
            }

            EntityPlayer player = (EntityPlayer) sender;

            // handle direwolf persistence with JSON
            if (params.length >= 2) {
                DirewolfData thisData = new DirewolfData(player.getPersistentID(), coat, name);
                direwolfStorageHandler.writeToJson(thisData);
            }
            if (params.length == 0) {
                // attempt to load data
                try {
                    DirewolfData thisData = direwolfStorageHandler.readFromJson(player.getPersistentID());
                    coat = thisData.coat;
                    name = thisData.name;
                } catch (Exception e) {
                    // couldn't load data
                }
            }

            // clean up existing wolves
            deleteExistingDirewolf(player.getPersistentID());

            // make a fun cuddly direwolf
            EntityDirewolf direwolf = new EntityDirewolf(player.getEntityWorld());
            direwolf.setPosition(player.posX, player.posY, player.posZ);
            player.getEntityWorld().spawnEntity(direwolf);

            // tame the beast
            direwolf.setTamed(true);
            direwolf.setOwnerId(player.getPersistentID());

            // give the wolf its coloring
            direwolf.setVariant(coat);

            // give it a name if one has been provided
            if (!name.equals("")) direwolf.setCustomNameTag(name);

            // add to the list
            direwolvesByPlayer.put(player.getPersistentID(), direwolf);
        }
    }

    @Override
    public String getName() {
        return "wcdirewolf";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.wcdirewolf.usage";
    }

    // everyone can have a direwolf c:
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public void deleteExistingDirewolf(UUID uuid) {
        EntityDirewolf dwf = direwolvesByPlayer.get(uuid);
        if (!(dwf == null)) {
            dwf.getEntityWorld().removeEntity(dwf);
            direwolvesByPlayer.remove(uuid);
        }
    }

    public void cleanupDirewolves() {
        for (Map.Entry<UUID, EntityDirewolf> entry : direwolvesByPlayer.entrySet()) {
            entry.getValue().getEntityWorld().removeEntity(entry.getValue());
            direwolvesByPlayer.remove(entry.getKey());
        }
    }

}
