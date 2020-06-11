package com.westeroscraft.westerosentities.entities;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.world.World;

public class EntityBrownBear extends EntityPolarBear {
    public EntityBrownBear(World worldIn) {
        super(worldIn);
    }

    /*
    No children allowed
    */
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }
}
