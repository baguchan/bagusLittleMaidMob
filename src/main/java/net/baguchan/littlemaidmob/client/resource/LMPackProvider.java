package net.baguchan.littlemaidmob.client.resource;

import net.baguchan.littlemaidmob.LittleMaidMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.Supplier;

//PackFinderはリソースパックを探すクラス
//これをResourcePackListに突っ込むことで、ゲーム内リソースパックから選ぶことができる
//Forgeでも同じことをやってModのリソースを読み込んでいる
@OnlyIn(Dist.CLIENT)
public class LMPackProvider implements RepositorySource {
    @Override
    public void loadPacks(Consumer<Pack> p_10542_, Pack.PackConstructor p_10543_) {
        String name = LittleMaidMod.MODID;
        boolean isAlwaysEnabled = true;
        PackResources resourcePack = ResourceWrapper.INSTANCE;
        Supplier<PackResources> supplier = () -> resourcePack;
        PackMetadataSection resourcePackMeta = ResourceWrapper.PACK_INFO;
        Pack.Position priority = Pack.Position.TOP;
        PackSource decorator = PackSource.BUILT_IN;
        Pack info = p_10543_.create(
                name,
                Component.literal(resourcePack.getName()),
                isAlwaysEnabled,
                supplier,
                resourcePackMeta,
                priority,
                decorator
        );
        p_10542_.accept(info);
    }
}
