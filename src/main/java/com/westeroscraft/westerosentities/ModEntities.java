package com.westeroscraft.westerosentities;

import com.westeroscraft.westerosentities.entities.EntityBrownBear;
import com.westeroscraft.westerosentities.entities.EntityDirewolf;
import com.westeroscraft.westerosentities.entities.EntityZombieBear;
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
    public static EntityEntry ZOMBIE_BEAR;
    public static EntityEntry BROWN_BEAR;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        DIREWOLF = EntityEntryBuilder.create()
                .entity(EntityDirewolf.class)
                .id(new ResourceLocation(WesterosEntities.MODID, "direwolf"), ID++)
                .name("direwolf")
                .tracker(64, 2, false)
                .egg(0x7D7D7D, 0x363636)
                .build();
        ZOMBIE_BEAR = EntityEntryBuilder.create()
                .entity(EntityZombieBear.class)
                .id(new ResourceLocation(WesterosEntities.MODID, "zombiebear"), ID++)
                .name("zombiebear")
                .tracker(64, 2, false)
                .egg(0x6C736A, 0x103617)
                .build();
        BROWN_BEAR = EntityEntryBuilder.create()
                .entity(EntityBrownBear.class)
                .id(new ResourceLocation(WesterosEntities.MODID, "brownbear"), ID++)
                .name("brownbear")
                .tracker(64, 2, false)
                .egg(0x6B4C1A, 0xC98922)
                .build();
        event.getRegistry().registerAll(DIREWOLF, ZOMBIE_BEAR, BROWN_BEAR);
    }
}
