package com.westeroscraft.westerosentities.commands;

import com.westeroscraft.westerosentities.persistence.DirewolfData;
import com.westeroscraft.westerosentities.entities.EntityDirewolf;
import com.westeroscraft.westerosentities.persistence.PlayerEntitiesDatabaseHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommandWCDirewolf extends CommandBase {

    // stores information about who owns which direwolf
    public final HashMap<UUID, EntityDirewolf> direwolvesByPlayer;
    private PlayerEntitiesDatabaseHandler dbHandler;

    // when players attempt to do something dangerous (like resetting their direwolf), they must confirm their decision
    // this stores information about their command pending
    private ArrayList<PendingConfirmationCommandInfo> pendingCommands = new ArrayList<>();

    public CommandWCDirewolf(PlayerEntitiesDatabaseHandler dbHandler, Logger logger) {
        this.dbHandler = dbHandler;
        direwolvesByPlayer = new HashMap<UUID, EntityDirewolf>();
    }

    // remembers information about what a player was going to do before they were asked to confirm their decision
    // we store a date so an old CommandInfo will not be called on accident (expires in 20 seconds)
    private class PendingConfirmationCommandInfo {
        public boolean confirmed = false;
        public ICommandSender sender;
        public String[] params;
        public Date timeSent;
        public PendingConfirmationCommandInfo(ICommandSender sender, String[] params) {
            this.sender = sender;
            this.params = params;
            this.timeSent = new Date();
        }
        public boolean isExpired() {
            long timeNow = new Date().getTime();
            // if the original command was sent more than 20 seconds ago, this is expired
            boolean expired = timeNow - timeSent.getTime() > 20000;
            if (expired) pendingCommands.remove(this);
            return expired;
        }
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
            int growthStage = 1;
            if (params.length != 0) {
                // if the first arg is 'confirm' we look for unexpired pending command info
                if (params[0].equalsIgnoreCase("confirm")) {
                    for (int i = 0; i < pendingCommands.size(); i++) {
                        PendingConfirmationCommandInfo command = pendingCommands.get(i);
                        if (!command.isExpired() && command.sender == sender) {
                            // execute the command again after confirming it
                            command.confirmed = true;
                            sender.sendMessage(new TextComponentString(TextFormatting.BLUE + "Goodbye!"));
                            execute(server, command.sender, command.params);
                            pendingCommands.remove(command);
                            return;
                        }
                    }
                    // if nothing was found, there is nothing to confirm
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Nothing to confirm. Your previous command may have timed out."));
                    return;
                }
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
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid coat provided; will choose randomly. Use /help wcdirewolf for a list of valid colorings."));
                }
                // parse the name, if it has been given
                if (params.length > 1) {
                    name = params[1];
                }
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (params.length >= 2) {
                DirewolfData thisData = new DirewolfData(player.getPersistentID(), coat, name, new Date());
                // safe to continue will be set to true if a pending command info
                // that is confirmed and for this sender is found
                // if it is false at the end of the loop, the sender is told to use the /wcdirewolf confirm command
                boolean safeToContinue = false;
                for (int i = 0; i < pendingCommands.size(); i++) {
                    PendingConfirmationCommandInfo command = pendingCommands.get(i);
                    if (!command.isExpired() && command.sender == sender && command.confirmed) {
                        safeToContinue = true;
                    }
                }
                if (!safeToContinue) {
                    // the unsafe action has not been confirmed
                    // inform the player and return. also make a new PendingConfirmationCommandInfo
                    DirewolfData loadedData = dbHandler.getDirewolfData(player.getPersistentID());
                    if (loadedData != null) {
                        // this means they have a direwolf
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, MMMMM dd, yyyy");
                        String formattedDate = sdf.format(loadedData.dateCreated);
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "You adopted " + loadedData.name + " on " + formattedDate + "."));
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Do you wish to release " + loadedData.name + " and adopt a new direwolf?"));
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "This action cannot be reversed!"));
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "Use /wcdirewolf confirm to continue."));
                        pendingCommands.add(new PendingConfirmationCommandInfo(sender, params));
                        return;
                    }
                }
                dbHandler.storeDirewolfData(thisData);
            }
            if (params.length == 0) {
                DirewolfData loadedData = dbHandler.getDirewolfData(player.getPersistentID());
                // data will be null if something went wrong or if data hasn't been recorded previously
                if (loadedData != null) {
                    coat = loadedData.coat;
                    name = loadedData.name;
                    // get the growth stage
                    long timeNow = new Date().getTime();
                    long difference = timeNow - loadedData.dateCreated.getTime();
                    if (difference > EntityDirewolf.SENIOR) {
                        growthStage = 4;
                    } else if (difference > EntityDirewolf.ADULT) {
                        growthStage = 3;
                    } else if (difference > EntityDirewolf.YOUNG) {
                        growthStage = 2;
                    }
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

            // set the growth stage
            direwolf.setGrowthStage(growthStage);

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
