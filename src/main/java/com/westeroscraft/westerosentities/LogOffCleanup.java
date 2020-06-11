package com.westeroscraft.westerosentities;

import com.westeroscraft.westerosentities.commands.CommandWCDirewolf;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/*
This simple class has an SubscribeEvent handler that listens for player logoffs.
When a player logs off, their direwolf is deleted.
 */
public class LogOffCleanup {

    CommandWCDirewolf wcdirewolf;

    public LogOffCleanup(CommandWCDirewolf command) {
        this.wcdirewolf = command;
    }

    @SubscribeEvent
    public void playerLogOff(PlayerEvent.PlayerLoggedOutEvent event) {
        wcdirewolf.deleteExistingDirewolf(event.player.getUniqueID());
    }

}
