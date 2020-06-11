package com.westeroscraft.westerosentities;

import com.westeroscraft.westerosentities.entities.EntityBrownBear;
import com.westeroscraft.westerosentities.entities.EntityDirewolf;
import com.westeroscraft.westerosentities.entities.EntityZombieBear;
import com.westeroscraft.westerosentities.renderers.RenderBrownBear;
import com.westeroscraft.westerosentities.renderers.RenderDirewolf;
import com.westeroscraft.westerosentities.renderers.RenderZombieBear;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {
    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderers() {
        /*
        Renderers are registered here because they are a client-side thing.
        Sorry about the deprecated method. I couldn't find a better way to do this.
         */
        RenderDirewolf direwolfRenderer = new RenderDirewolf(Minecraft.getMinecraft().getRenderManager());
        RenderZombieBear zombieBearRenderer = new RenderZombieBear(Minecraft.getMinecraft().getRenderManager());
        RenderBrownBear brownBearRenderer = new RenderBrownBear(Minecraft.getMinecraft().getRenderManager());

        RenderingRegistry.registerEntityRenderingHandler(EntityDirewolf.class, direwolfRenderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityZombieBear.class, zombieBearRenderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityBrownBear.class, brownBearRenderer);
    }
}
