package net.baguchan.bagus_littlemaidmob.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelBase;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelMultiBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiModelRenderer<T extends MultiModelEntity> extends MobRenderer<T, ModelBase<T>> {
    private static final ResourceLocation NULL_TEXTURE = new ResourceLocation(LittleMaidMod.MODID, "null");

    private ModelMultiBase<T> base;
    private ModelMultiBase<T> inner;
    private ModelMultiBase<T> outer;

    public MultiModelRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NullModel<>(), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
    }

    @Override
    public void render(T livingEntity, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {

        livingEntity.getModel(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
                .filter(model -> model instanceof ModelMultiBase)
                .ifPresent(model -> {
                    if(base == null || base != model) {
                        base = (ModelMultiBase<T>) model;
                    }
               });
        for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
            livingEntity.getModel(IHasMultiModel.Layer.INNER, part)
                    .filter(model -> model instanceof ModelMultiBase)
                    .ifPresent(model -> {
                        if(inner == null || inner != model) {
                            inner = (ModelMultiBase<T>) model;
						}
                    });
            livingEntity.getModel(IHasMultiModel.Layer.OUTER, part)
                    .filter(model -> model instanceof ModelMultiBase)
                    .ifPresent(model -> {
                        if(outer == null || outer != model) {
                            outer = (ModelMultiBase<T>) model;
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
