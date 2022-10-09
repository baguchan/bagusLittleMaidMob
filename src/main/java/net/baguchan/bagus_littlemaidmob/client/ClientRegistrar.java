package net.baguchan.bagus_littlemaidmob.client;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.client.renderer.MultiModelRenderer;
import net.baguchan.bagus_littlemaidmob.client.screen.LittleMaidScreen;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelMultiBase;
import net.baguchan.bagus_littlemaidmob.registry.ModEntities;
import net.baguchan.bagus_littlemaidmob.registry.ModMenuTypes;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMModelManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = LittleMaidMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrar {
	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.MULTI_MODEL.get(), MultiModelRenderer::new);
		event.registerEntityRenderer(ModEntities.LITTLE_MAID.get(), MultiModelRenderer::new);
		event.registerEntityRenderer(ModEntities.DUMMY.get(), MultiModelRenderer::new);
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event) {
		LMModelManager.INSTANCE.getModels().forEach((id, modelHolder) -> {
			for(IHasMultiModel.Layer layer : IHasMultiModel.Layer.values()) {
				for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
					IMultiModel<?> model = modelHolder.getModel(layer);
					if (model instanceof ModelMultiBase<?>) {
					}
				}
			}
		});
	}

	public static void setup(FMLClientSetupEvent event) {
		MenuScreens.register(ModMenuTypes.LITTLE_MAID_CONTAINER.get(), LittleMaidScreen::new);

	}

}
