package com.westeroscraft.westerosentities.renderers;

import com.westeroscraft.westerosentities.entities.EntityBrownBear;
import com.westeroscraft.westerosentities.entities.EntityZombieBear;
import net.minecraft.client.model.ModelPolarBear;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderBrownBear extends RenderLiving<EntityBrownBear> {
    public static final ResourceLocation BROWN_BEAR_TEXTURES = new ResourceLocation("westerosentities:textures/entity/brownbear/brown_bear.png");

    public RenderBrownBear(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPolarBear(), 0.7f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBrownBear entity) {
        return BROWN_BEAR_TEXTURES;
    }
}
