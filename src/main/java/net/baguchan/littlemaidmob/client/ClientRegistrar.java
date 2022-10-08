package net.baguchan.littlemaidmob.client;

import net.baguchan.littlemaidmob.LittleMaidMod;
import net.baguchan.littlemaidmob.client.renderer.MultiModelRenderer;
import net.baguchan.littlemaidmob.client.screen.LittleMaidScreen;
import net.baguchan.littlemaidmob.client.util.ModelLayerUtils;
import net.baguchan.littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.littlemaidmob.maidmodel.ModelMultiBase;
import net.baguchan.littlemaidmob.registry.ModEntities;
import net.baguchan.littlemaidmob.registry.ModMenuTypes;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
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
				IMultiModel<?> model = modelHolder.getModel(layer);
				if (model instanceof ModelMultiBase<?>) {
					float size = ModelLayerUtils.setSize(layer);
					event.registerLayerDefinition(ModelLayerUtils.setModelLayerID(id, layer), () -> ((ModelMultiBase<?>) model).createBodyLayer(size));
				}
			}
		});

	}

	public static void setup(FMLClientSetupEvent event) {
		MenuScreens.register(ModMenuTypes.LITTLE_MAID_CONTAINER.get(), LittleMaidScreen::new);
	}


}
