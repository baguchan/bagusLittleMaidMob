package net.baguchan.bagus_littlemaidmob.client.resource.loader;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.client.resource.ResourceWrapper;
import net.baguchan.bagus_littlemaidmob.resource.loader.LMLoader;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.bagus_littlemaidmob.resource.util.ResourceHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 画像ファイルを読み込み、ゲームに登録するクラス
 */
//サーバーでは読み込まない
@OnlyIn(Dist.CLIENT)
public class LMTextureLoader implements LMLoader {
    private final LMTextureManager textureManager;
    private final HashMap<String, String> converter = new HashMap<>();

    public LMTextureLoader(LMTextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void addPathConverter(String target, String to) {
        converter.put(target, to);
    }

    @Override
    public boolean canLoad(String path, Path folderPath, InputStream inputStream, boolean isArchive) {
        return path.endsWith(".png") && ResourceHelper.getParentFolderName(path, isArchive).isPresent()
                && ResourceHelper.getIndex(path) != -1;
    }

    @Override
    public void load(String path, Path folderPath, InputStream inputStream, boolean isArchive) {
        ResourceLocation texturePath = getResourceLocation(path, isArchive)
                .orElseThrow(() -> new IllegalArgumentException("引数が不正です。"));
        String textureName = ResourceHelper.getTexturePackName(path, isArchive)
                .orElseThrow(() -> new IllegalArgumentException("引数が不正です。"));
        String modelName = ResourceHelper.getModelName(textureName);
        textureManager.addTexture(ResourceHelper.getFileName(path, isArchive), textureName, modelName,
                ResourceHelper.getIndex(path), texturePath);
        ResourceWrapper.addResourcePath(texturePath, path, folderPath, isArchive);
    }

    /**
     * パスをテクスチャのパスに変換
     * assets/minecraft/textures/entity/littlemaid/[texture]_[model]/xxxx_[index].png
     * または
     * mob/littlemaid/[texture]_[model]/xxxx_[index].png
     * を
     * littlemaidmodelloader:textures.entity.littlemaid.[texture]_[model].xxxx_[index].png
     * に変換
     */
    private Optional<ResourceLocation> getResourceLocation(String path, boolean isArchive) {
        //小文字にする
        String texturePath = path.toLowerCase();
        if (!isArchive) {
            texturePath = texturePath.replace("\\", "/");
        }
        //すべてminecraftから始まるように変換
        for (Map.Entry<String, String> entry : converter.entrySet()) {
            texturePath = texturePath.replace(entry.getKey(), entry.getValue());
        }
        //minecraft/なら9
        int firstSplitter = texturePath.indexOf("/");
        //ファイル階層が無い場合はnullを返す
        if (firstSplitter == -1) {
            return Optional.empty();
        }
        //使用不能文字を変換
        texturePath = texturePath.replaceAll("[^a-z0-9/._\\-]", "-");

        String namePath = texturePath.substring(firstSplitter + 1);
        return Optional.of(new ResourceLocation(LittleMaidMod.MODID, namePath));
    }
}
