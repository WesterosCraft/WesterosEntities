package com.westeroscraft.westerosentities.renderers;

import com.westeroscraft.westerosentities.entities.EntityZombieBear;
import net.minecraft.client.model.ModelPolarBear;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderZombieBear extends RenderLiving<EntityZombieBear> {
    public static final ResourceLocation ZOMBIE_BEAR_TEXTURES = new ResourceLocation("westerosentities:textures/entity/zombiebear/zombie_bear.png");

    public RenderZombieBear(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPolarBear(), 0.7f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityZombieBear entity) {
        return ZOMBIE_BEAR_TEXTURES;
    }
}
