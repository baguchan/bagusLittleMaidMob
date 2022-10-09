package net.baguchan.bagus_littlemaidmob.client.resource.loader;

import net.baguchan.bagus_littlemaidmob.LittleMaidConfig;
import net.baguchan.bagus_littlemaidmob.client.resource.ResourceWrapper;
import net.baguchan.bagus_littlemaidmob.client.resource.manager.LMSoundManager;
import net.baguchan.bagus_littlemaidmob.resource.loader.LMLoader;
import net.baguchan.bagus_littlemaidmob.resource.util.ResourceHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

//サーバーでは読み込む必要が無いため読み込まない
@OnlyIn(Dist.CLIENT)
public class LMSoundLoader implements LMLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LMSoundManager soundManager;

    public LMSoundLoader(LMSoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public boolean canLoad(String path, Path folderPath, InputStream inputStream, boolean isArchive) {
        return path.endsWith(".ogg") && ResourceHelper.getParentFolderName(path, isArchive).isPresent();
    }

    @Override
    public void load(String path, Path folderPath, InputStream inputStream, boolean isArchive) {
        String packName = ResourceHelper.getFirstParentName(path, folderPath, isArchive).orElse("");
        String parent = ResourceHelper.getParentFolderName(path, isArchive).orElse("");
        String fileName = ResourceHelper.getFileName(path, isArchive);
        ResourceLocation location = ResourceHelper.getLocation(packName, fileName);
        fileName = ResourceHelper.removeExtension(fileName);
        fileName = ResourceHelper.removeNameLastIndex(fileName);
        soundManager.addSound(packName, parent, fileName, location);
        ResourceWrapper.addResourcePath(location, path, folderPath, isArchive);
        if (LittleMaidConfig.isDebugMode) {
            LOGGER.debug("Loaded Sound : " + packName + " : " + fileName);
        }
    }

}
