package net.baguchan.bagus_littlemaidmob;

import com.mojang.logging.LogUtils;
import net.baguchan.bagus_littlemaidmob.client.ClientRegistrar;
import net.baguchan.bagus_littlemaidmob.client.resource.LMPackProvider;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelLittleMaid_Elsa5;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelLittleMaid_Orign;
import net.baguchan.bagus_littlemaidmob.message.SyncMultiModelPacket;
import net.baguchan.bagus_littlemaidmob.message.SyncSetModePacket;
import net.baguchan.bagus_littlemaidmob.message.SyncSoundConfigMessage;
import net.baguchan.bagus_littlemaidmob.registry.ModEntities;
import net.baguchan.bagus_littlemaidmob.registry.ModItems;
import net.baguchan.bagus_littlemaidmob.registry.ModMenuTypes;
import net.baguchan.bagus_littlemaidmob.resource.classloader.MultiModelClassLoader;
import net.baguchan.bagus_littlemaidmob.resource.loader.LMConfigLoader;
import net.baguchan.bagus_littlemaidmob.resource.loader.LMFileLoader;
import net.baguchan.bagus_littlemaidmob.resource.loader.LMMultiModelLoader;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.nio.file.Paths;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LittleMaidMod.MODID)
public class LittleMaidMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bagus_littlemaidmob";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String NETWORK_PROTOCOL = "2";


    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
            .networkProtocolVersion(() -> NETWORK_PROTOCOL)
            .clientAcceptedVersions(NETWORK_PROTOCOL::equals)
            .serverAcceptedVersions(NETWORK_PROTOCOL::equals)
            .simpleChannel();

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public LittleMaidMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register the commonSetup method for modloading
		ModMenuTypes.MENU_REGISTRY.register(modEventBus);
		ModItems.ITEM_REGISTRY.register(modEventBus);
		ModEntities.ENTITIES_REGISTRY.register(modEventBus);

		this.setupMessages();

		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientRegistrar::setup));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LittleMaidConfig.COMMON_SPEC);
    }

    private void setupMessages() {
		CHANNEL.messageBuilder(SyncSoundConfigMessage.class, 0)
				.encoder(SyncSoundConfigMessage::toBytes).decoder(SyncSoundConfigMessage::new)
				.consumerMainThread(SyncSoundConfigMessage::handle)
				.add();
		CHANNEL.messageBuilder(SyncMultiModelPacket.class, 1)
				.encoder(SyncMultiModelPacket::toBytes).decoder(SyncMultiModelPacket::new)
				.consumerMainThread(SyncMultiModelPacket::handle)
				.add();
		CHANNEL.messageBuilder(SyncSetModePacket.class, 2)
				.encoder(SyncSetModePacket::toBytes).decoder(SyncSetModePacket::new)
				.consumerMainThread(SyncSetModePacket::handle)
				.add();
	}

    public static void initFileLoader() {
        LMFileLoader fileLoader = LMFileLoader.INSTANCE;
        fileLoader.addLoadFolderPath(Paths.get(Minecraft.getInstance().gameDirectory.toString(), "LMMLResources"));
        fileLoader.addLoader(new LMMultiModelLoader(LMModelManager.INSTANCE, new MultiModelClassLoader(fileLoader.getFolderPaths())));
        fileLoader.addLoader(new LMConfigLoader(LMConfigManager.INSTANCE));
    }

    public static void initModelLoader() {
        //モデルを読み込む
        //TODO 1.19にあったregistryを採用し、書き換える
        LMModelManager modelManager = LMModelManager.INSTANCE;
        modelManager.addModel("Default", ModelLittleMaid_Orign.class);
        //modelManager.addModel("Beverly7", ModelLittleMaid_Beverly7.class);
        //modelManager.addModel("Chloe2", ModelLittleMaid_Chloe2.class);
        modelManager.addModel("Elsa5", ModelLittleMaid_Elsa5.class);

        modelManager.setDefaultModel(modelManager.getModel("Default", IHasMultiModel.Layer.SKIN)
                .orElseThrow(RuntimeException::new));
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
		Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LMPackProvider());
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LMFileLoader.INSTANCE.load();
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call

}
