package net.baguchan.littlemaidmob.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.baguchan.littlemaidmob.LittleMaidMod;
import net.baguchan.littlemaidmob.client.util.ModelLayerUtils;
import net.baguchan.littlemaidmob.entity.MultiModelEntity;
import net.baguchan.littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.littlemaidmob.maidmodel.ModelBase;
import net.baguchan.littlemaidmob.maidmodel.ModelMultiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
//そのまま使ってもいいし継承して使ってもいい
//別な奴を継承しながら使いたいなら移譲でどうにかするか自作してね
@OnlyIn(Dist.CLIENT)
public class MultiModelRenderer<T extends MultiModelEntity> extends MobRenderer<T, EntityModel<T>> {
    private static final ResourceLocation NULL_TEXTURE = new ResourceLocation(LittleMaidMod.MODID, "null");

    private ModelMultiBase<T> base;
    private ModelMultiBase<T> inner;
    private ModelMultiBase<T> outer;

    public MultiModelRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NullModel<>(), 0.5F);
    }

    @Override
    public void render(T livingEntity, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();

        livingEntity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .filter(model -> model instanceof ModelMultiBase)
                .ifPresent(model -> {
                    if(base == null || base != model) {
                        base = (ModelMultiBase<T>) model;
                        base.init(entityModelSet.bakeLayer(ModelLayerUtils.setModelLayerID(livingEntity.getMultiModel().getTextureHolder(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD).getModelName(), IHasMultiModel.Layer.SKIN)));
                    }
               });
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            livingEntity.getModel(IHasMultiModel.Layer.INNER, part)
                    .filter(model -> model instanceof ModelMultiBase)
                    .ifPresent(model -> {
                        if(inner == null || inner != model) {
                            inner = (ModelMultiBase<T>) model;
                            inner.init(entityModelSet.bakeLayer(ModelLayerUtils.setModelLayerID(livingEntity.getMultiModel().getTextureHolder(IHasMultiModel.Layer.INNER, part).getModelName(), IHasMultiModel.Layer.INNER)));
                        }
                    });
            livingEntity.getModel(IHasMultiModel.Layer.OUTER, part)
                    .filter(model -> model instanceof ModelMultiBase)
                    .ifPresent(model -> {
                        if(outer == null || outer != model) {
                            outer = (ModelMultiBase<T>) model;
                            outer.init(entityModelSet.bakeLayer(ModelLayerUtils.setModelLayerID(livingEntity.getMultiModel().getTextureHolder(IHasMultiModel.Layer.OUTER, part).getModelName(), IHasMultiModel.Layer.OUTER)));
                        }

                    });
        }

        model = base;
        if(model != null) {
            super.render(livingEntity, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD, false)
                .orElse(NULL_TEXTURE);
    }
}
