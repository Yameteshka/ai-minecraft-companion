package com.example.examplemod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CompanionRenderer extends HumanoidMobRenderer<EntityCompanion, HumanoidModel<EntityCompanion>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("examplemod", "textures/entity/companion.png");

    public CompanionRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCompanion entity) {
        return TEXTURE;
    }
}
