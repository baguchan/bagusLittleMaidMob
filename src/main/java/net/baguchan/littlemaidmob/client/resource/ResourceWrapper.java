package net.baguchan.littlemaidmob.client.resource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.baguchan.littlemaidmob.LittleMaidMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//外部から読み込んだリソースをマイクラに送るラッパー
@OnlyIn(Dist.CLIENT)
public class ResourceWrapper implements PackResources {
    public static final ResourceWrapper INSTANCE = new ResourceWrapper();
    public static final PackMetadataSection PACK_INFO =
            new PackMetadataSection(Component.literal("LittleMaidModelLoader!!!"), 9);
    protected static final HashMap<ResourceLocation, Resource> PATHS = Maps.newHashMap();


    @Nullable
    @Override
    public InputStream getRootResource(String p_10294_) throws IOException {
        return null;
    }

    //いつ呼ばれるのか不明
    //少なくとも起動時とリロード時は動かず

    //引数のResourceLocationはlittlemaidmodelloader:textures/...の形式

    @Override
    public InputStream getResource(PackType p_10289_, ResourceLocation p_10290_) throws IOException {
        Resource resource = PATHS.get(p_10290_);
        if (resource == null) {
            throw new FileNotFoundException(p_10290_.toString());
        }
        return resource.getInputStream();
    }
    //リロード時に呼ばれる
    //pathInが重要で、これと一致するリソースのみ渡すこと
    //そうでないと画像リソースがフォントに飛んでってクソ時間を食う

    @Override
    public Collection<ResourceLocation> getResources(PackType p_215339_, String namespace, String prefix, Predicate<ResourceLocation> p_215342_) {
        return PATHS.keySet().stream()
                .filter(location -> location.getPath().startsWith(namespace))
                .filter(p_215342_)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasResource(PackType p_10292_, ResourceLocation p_10293_) {
        return PATHS.containsKey(p_10293_);
    }

    @Override
    public Set<String> getNamespaces(PackType p_10283_) {
        return Sets.newHashSet(LittleMaidMod.MODID);
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10291_) throws IOException {
        if (p_10291_.getMetadataSectionName().equals("pack")) {
            return (T) PACK_INFO;
        }
        return null;
    }

    @Override
    public String getName() {
        return "LMModelLoader";
    }

    @Override
    public void close() {
    }

    public static void addResourcePath(ResourceLocation resourcePath, String path, Path homePath, boolean isArchive) {
        PATHS.put(resourcePath, new Resource(path, homePath, isArchive));
    }

    private static class Resource {
        private final String path;
        private final Path homePath;
        private final boolean isArchive;

        private Resource(String path, Path homePath, boolean isArchive) {
            this.path = path;
            this.homePath = homePath;
            this.isArchive = isArchive;
        }

        public InputStream getInputStream() throws IOException {
            if (isArchive) {
                String resourcePath = homePath.toString();
                ZipFile zipfile = new ZipFile(resourcePath);
                ZipEntry zipentry = zipfile.getEntry(path);
                if (zipentry != null) {
                    return zipfile.getInputStream(zipentry);
                }
                //上記より遅い
                /*ZipInputStream zipStream = new ZipInputStream(Files.newInputStream(homePath));
                ZipEntry entry;
                while ((entry = zipStream.getNextEntry()) != null) {
                    if (entry.getName().equals(path)) {
                        return zipStream;
                    }
                }*/
                throw new NoSuchFileException(path);
            } else {
                return Files.newInputStream(Paths.get(homePath.toString(), path));
            }
        }

    }

}
