package com.westeroscraft.westerosdirewolves;

import com.westeroscraft.westerosdirewolves.entities.EntityDirewolf;
import com.westeroscraft.westerosdirewolves.renderers.RenderDirewolf;
import net.minecraft.client.Minecraft;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityDirewolf.class, direwolfRenderer);
    }
}
