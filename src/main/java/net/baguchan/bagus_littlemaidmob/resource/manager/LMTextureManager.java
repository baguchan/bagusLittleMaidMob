package net.baguchan.bagus_littlemaidmob.resource.manager;

import net.baguchan.bagus_littlemaidmob.LittleMaidConfig;
import net.baguchan.bagus_littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.bagus_littlemaidmob.resource.util.ResourceHelper;
import net.baguchan.bagus_littlemaidmob.resource.util.TextureIndexes;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LMTextureManager {
    public static final LMTextureManager INSTANCE = new LMTextureManager();
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, TextureHolder> textures = new HashMap<>();

    public void addTexture(String fileName, String textureName, String modelName, int index, ResourceLocation texturePath) {
        TextureHolder textureHolder = textures.computeIfAbsent(textureName.toLowerCase(),
                k -> new TextureHolder(textureName, modelName));
        if (TextureIndexes.getTextureIndexes(index).isArmor()) {
            textureHolder.addArmorTexture(getArmorName(fileName), index, texturePath);
        } else {
            textureHolder.addTexture(index, texturePath);
        }
        if (LittleMaidConfig.isDebugMode) {
            LOGGER.debug("Loaded Texture : " + texturePath);
        }
    }

    public String getArmorName(String fileName) {
        String name = fileName.substring(0, fileName.indexOf('_'));
        if (name.contains("chainmail")) {
            return name;
        }
        return name.replace("chain", "chainmail");
    }

    public Optional<TextureHolder> getTexture(String textureName) {
        TextureHolder textureHolder = textures.get(textureName.toLowerCase());
        //サーバー側で読み込んでないテクスチャでもテクスチャ名だけは保持する
        if (textureHolder == null) {
            TextureHolder serverHolder = new TextureHolder(textureName, ResourceHelper.getModelName(textureName));
            textures.put(textureName.toLowerCase(), serverHolder);
            return Optional.of(serverHolder);
        }
        return Optional.ofNullable(textureHolder);
    }

    public Collection<TextureHolder> getAllTextures() {
        return textures.values();
    }
}
