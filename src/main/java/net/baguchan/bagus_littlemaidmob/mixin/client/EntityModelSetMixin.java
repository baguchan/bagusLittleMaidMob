package net.baguchan.bagus_littlemaidmob.mixin.client;

import com.google.common.collect.ImmutableMap;
import net.baguchan.bagus_littlemaidmob.client.IGetRoot;
import net.baguchan.bagus_littlemaidmob.client.resource.loader.LMSoundLoader;
import net.baguchan.bagus_littlemaidmob.client.resource.loader.LMTextureLoader;
import net.baguchan.bagus_littlemaidmob.client.resource.manager.LMSoundManager;
import net.baguchan.bagus_littlemaidmob.resource.loader.LMFileLoader;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.bagus_littlemaidmob.resource.util.LMSounds;
import net.baguchan.bagus_littlemaidmob.resource.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

import static net.baguchan.bagus_littlemaidmob.LittleMaidMod.initFileLoader;
import static net.baguchan.bagus_littlemaidmob.LittleMaidMod.initModelLoader;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin implements IGetRoot {

	@Shadow
	private Map<ModelLayerLocation, LayerDefinition> roots;

	@Inject(method = "onResourceManagerReload", at = @At("TAIL"))
	public void onResourceManagerReload(ResourceManager p_171102_, CallbackInfo callbackInfo) {
		initFileLoader();
		initModelLoader();

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

	@OnlyIn(Dist.CLIENT)
	public void initTextureLoader() {
		LMFileLoader fileLoader = LMFileLoader.INSTANCE;
		LMTextureLoader textureProcessor = new LMTextureLoader(LMTextureManager.INSTANCE);
		textureProcessor.addPathConverter("assets/", "");
		textureProcessor.addPathConverter("mob/", "minecraft/textures/entity/");
		fileLoader.addLoader(textureProcessor);
	}

	@OnlyIn(Dist.CLIENT)
	public void initSoundLoader() {
		LMFileLoader.INSTANCE.addLoader(new LMSoundLoader(LMSoundManager.INSTANCE));
	}

	public void addGhastMaidVoice() {
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

	private void addVoice(String soundName, SoundEvent soundId, ImmutableMap.Builder<String, String> configMap) {
		configMap.put(soundName, soundId.getLocation().toString());
	}

	@Override
	public Map<ModelLayerLocation, LayerDefinition> getRoot() {
		return this.roots;
	}

	@Override
	public void setRoot(Map<ModelLayerLocation, LayerDefinition> map) {
		this.roots = map;
	}
}
