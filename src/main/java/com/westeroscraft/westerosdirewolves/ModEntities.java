package com.westeroscraft.westerosdirewolves;

import com.westeroscraft.westerosdirewolves.entities.EntityDirewolf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

/*
Utility class for registering entities
 */
public class ModEntities {
    public static int ID = 0;

    public static EntityEntry DIREWOLF;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        DIREWOLF = EntityEntryBuilder.create()
                .entity(EntityDirewolf.class)
                .id(new ResourceLocation(WesterosDirewolves.MODID, "direwolf"), ID++)
                .name("direwolf")
                .tracker(64, 2, false)
                .egg(0x7D7D7D, 0x363636)
                .build();
        event.getRegistry().register(DIREWOLF);
    }
}
