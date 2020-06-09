package com.westeroscraft.westerosdirewolves.commands;

import com.westeroscraft.westerosdirewolves.entities.EntityDirewolf;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class CommandWCDirewolf extends CommandBase {

    // stores information about who owns which direwolf
    private final HashMap<UUID, EntityDirewolf> direwolvesByPlayer;

    public CommandWCDirewolf() {
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
            // clean up existing wolves
            deleteExistingDirewolf(player.getUniqueID());

            // make a fun cuddly direwolf
            EntityDirewolf direwolf = new EntityDirewolf(player.getEntityWorld());
            direwolf.setPosition(player.posX, player.posY, player.posZ);
            player.getEntityWorld().spawnEntity(direwolf);

            // tame the beast
            direwolf.setTamed(true);
            direwolf.setOwnerId(player.getUniqueID());

            // give the wolf its coloring
            direwolf.setVariant(coat);

            // give it a name if one has been provided
            if (!name.equals("")) direwolf.setCustomNameTag(name);

            // add to the list
            direwolvesByPlayer.put(player.getUniqueID(), direwolf);
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

}
