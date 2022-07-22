package net.baguchan.littlemaidmob.client;

import com.google.common.collect.ImmutableMap;
import net.baguchan.littlemaidmob.LittleMaidMod;
import net.baguchan.littlemaidmob.client.renderer.MultiModelRenderer;
import net.baguchan.littlemaidmob.client.resource.LMPackProvider;
import net.baguchan.littlemaidmob.client.resource.loader.LMSoundLoader;
import net.baguchan.littlemaidmob.client.resource.loader.LMTextureLoader;
import net.baguchan.littlemaidmob.client.resource.manager.LMSoundManager;
import net.baguchan.littlemaidmob.client.util.ModelLayerUtils;
import net.baguchan.littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.littlemaidmob.maidmodel.ModelMultiBase;
import net.baguchan.littlemaidmob.registry.ModEntities;
import net.baguchan.littlemaidmob.resource.loader.LMFileLoader;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.LMSounds;
import net.baguchan.littlemaidmob.resource.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = LittleMaidMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrar {
	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.MULTI_MODEL.get(), MultiModelRenderer::new);
		event.registerEntityRenderer(ModEntities.LITTLE_MAID.get(), MultiModelRenderer::new);
		event.registerEntityRenderer(ModEntities.LITTLE_SERVANT.get(), MultiModelRenderer::new);
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event) {
		LMModelManager.INSTANCE.getModels().forEach((id, modelHolder) -> {
			for(IHasMultiModel.Layer layer : IHasMultiModel.Layer.values()) {
				IMultiModel<?> model = modelHolder.getModel(layer);
				if(model instanceof ModelMultiBase<?>) {
					float size = ModelLayerUtils.setSize(layer);
					event.registerLayerDefinition(ModelLayerUtils.setModelLayerID(id, layer), () -> ((ModelMultiBase<?>) model).createBodyLayer(size));
				}
			}
		});

	}

	public static void setup(FMLConstructModEvent event) {

	}



}
