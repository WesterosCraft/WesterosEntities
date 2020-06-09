package com.westeroscraft.westerosdirewolves.renderers;

import com.westeroscraft.westerosdirewolves.entities.EntityDirewolf;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderDirewolf extends RenderLiving<EntityDirewolf> {
    private static final ResourceLocation[] DIREWOLF_TEXTURES = new ResourceLocation[]{
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_amber.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_black.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_blackwhite.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_brown.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_chestnut.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_cinnamon.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_grey.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_greywhite.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_mixed.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_tan.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_white.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_whitegrey.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_amber_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_black_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_blackwhite_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_brown_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_chestnut_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_cinnamon_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_grey_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_greywhite_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_mixed_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_tan_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_white_angry.png"),
            new ResourceLocation("westerosdirewolves:textures/entity/direwolf/wolf_whitegrey_angry.png")
    };

    public RenderDirewolf(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelWolf(), 1.0F);
    }

    protected float handleRotationFloat(EntityDirewolf livingBase, float partialTicks) {
        return livingBase.getTailRotation();
    }

    public void doRender(EntityDirewolf entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.isWolfWet()) {
            float f = entity.getBrightness() * entity.getShadingWhileWet(partialTicks);
            GlStateManager.color(f, f, f);
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityDirewolf entity) {
        if (entity.isAngry()) {
            return DIREWOLF_TEXTURES[entity.getVariant() + 12];
        }
        return DIREWOLF_TEXTURES[entity.getVariant()];
    }

    @Override
    protected void preRenderCallback(EntityDirewolf a, float b) {
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

}
