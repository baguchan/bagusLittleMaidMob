package net.baguchan.littlemaidmob;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import net.baguchan.littlemaidmob.client.ClientRegistrar;
import net.baguchan.littlemaidmob.client.resource.LMPackProvider;
import net.baguchan.littlemaidmob.client.resource.loader.LMSoundLoader;
import net.baguchan.littlemaidmob.client.resource.loader.LMTextureLoader;
import net.baguchan.littlemaidmob.client.resource.manager.LMSoundManager;
import net.baguchan.littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.littlemaidmob.maidmodel.ModelLittleMaid_Elsa5;
import net.baguchan.littlemaidmob.maidmodel.ModelLittleMaid_Orign;
import net.baguchan.littlemaidmob.message.SyncMaidModelMessage;
import net.baguchan.littlemaidmob.registry.ModEntities;
import net.baguchan.littlemaidmob.resource.classloader.MultiModelClassLoader;
import net.baguchan.littlemaidmob.resource.loader.LMConfigLoader;
import net.baguchan.littlemaidmob.resource.loader.LMFileLoader;
import net.baguchan.littlemaidmob.resource.loader.LMMultiModelLoader;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.LMSounds;
import net.baguchan.littlemaidmob.resource.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.nio.file.Paths;
import java.util.Collection;

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
    public LittleMaidMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading

        ModEntities.ENTITIES_REGISTRY.register(modEventBus);


        initFileLoader();
        initModelLoader();

        this.setupMessages();

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientRegistrar::setup));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LittleMaidConfig.COMMON_SPEC);
    }

    private void setupMessages() {
        CHANNEL.messageBuilder(SyncMaidModelMessage.class, 0)
                .encoder(SyncMaidModelMessage::serialize).decoder(SyncMaidModelMessage::deserialize)
                .consumerMainThread(SyncMaidModelMessage::handle)
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
        Collection<ResourceLocation> resourceLocations = Minecraft.getInstance().getResourceManager()
                .listResourceStacks("textures/entity/littlemaid", s -> true).keySet();
        //テクスチャを読み込む
        resourceLocations.forEach(resourcePath -> {
            String path = resourcePath.getPath();
            ResourceHelper.getTexturePackName(path, false).ifPresent(textureName -> {
                String modelName = ResourceHelper.getModelName(textureName);
                int index = ResourceHelper.getIndex(path);
                if (index != -1) {
                    LMTextureManager.INSTANCE
                            .addTexture(ResourceHelper.getFileName(path, false), textureName, modelName, index, resourcePath);
                }
            });
        });

        addGhastMaidVoice();
        initTextureLoader();
        initSoundLoader();
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LMFileLoader.INSTANCE.load();
    }
    @OnlyIn(Dist.CLIENT)
    public static void initTextureLoader() {
        LMFileLoader fileLoader = LMFileLoader.INSTANCE;
        LMTextureLoader textureProcessor = new LMTextureLoader(LMTextureManager.INSTANCE);
        textureProcessor.addPathConverter("assets/", "");
        textureProcessor.addPathConverter("mob/", "minecraft/textures/entity/");
        fileLoader.addLoader(textureProcessor);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initSoundLoader() {
        LMFileLoader.INSTANCE.addLoader(new LMSoundLoader(LMSoundManager.INSTANCE));
    }

    public static void addGhastMaidVoice() {
        String packName = "DefaultGhast";

        var configMap = new ImmutableMap.Builder<String, String>();
        addVoice(LMSounds.HURT, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.HURT_FIRE, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.HURT_FALL, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.DEATH, SoundEvents.GHAST_DEATH, configMap);
        addVoice(LMSounds.ATTACK, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.ATTACK_BLOOD_SUCK, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.SHOOT, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.SHOOT_BURST, SoundEvents.GHAST_HURT, configMap);
        addVoice(LMSounds.LIVING_DAYTIME, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_MORNING, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_NIGHT, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_WHINE, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_RAIN, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_SNOW, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_COLD, SoundEvents.GHAST_AMBIENT, configMap);
        addVoice(LMSounds.LIVING_HOT, SoundEvents.GHAST_AMBIENT, configMap);
        LMConfigManager.INSTANCE.addConfig(packName, "", "littlemaidmob", configMap.build());
    }

    private static void addVoice(String soundName, SoundEvent soundId, ImmutableMap.Builder<String, String> configMap) {
        configMap.put(soundName, soundId.getLocation().toString());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call

}
