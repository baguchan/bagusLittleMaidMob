package net.baguchan.littlemaidmob.entity;

import net.baguchan.littlemaidmob.entity.compound.MultiModelCompound;
import net.baguchan.littlemaidmob.entity.compound.SoundPlayableCompound;
import net.baguchan.littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.TextureColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

//NM読み込むのに要る
public class LittleMaidBaseEntity extends MultiModelEntity {

    public LittleMaidBaseEntity(EntityType<? extends MultiModelEntity> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        ((GroundPathNavigation) getNavigation()).setCanPassDoors(true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("SkinColor", (byte) getColor().getIndex());
        nbt.putBoolean("IsContract", isContract());
        nbt.putString("SkinTexture", getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            nbt.putString("ArmorTextureInner" + part.getPartName(),
                    getTextureHolder(Layer.INNER, part).getTextureName());
            nbt.putString("ArmorTextureOuter" + part.getPartName(),
                    getTextureHolder(Layer.OUTER, part).getTextureName());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        if (nbt.contains("SkinColor"))
            setColor(TextureColors.getColor(nbt.getByte("SkinColor")));
        setContract(nbt.getBoolean("IsContract"));
        if (nbt.contains("SkinTexture")) {
            textureManager.getTexture(nbt.getString("SkinTexture"))
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        }
        for (Part part : Part.values()) {
            String inner = "ArmorTextureInner" + part.getPartName();
            String outer = "ArmorTextureOuter" + part.getPartName();
            if (nbt.contains(inner)) {
                textureManager.getTexture(nbt.getString(inner))
                        .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.INNER, part));
            }
            if (nbt.contains(outer)) {
                textureManager.getTexture(nbt.getString(outer))
                        .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.OUTER, part));
            }
        }
        if (nbt.contains("SoundConfigName"))
            LMConfigManager.INSTANCE.getConfig(nbt.getString("SoundConfigName"))
                    .ifPresent(this::setConfigHolder);

    }

    //マルチモデル関連

    @Override
    public boolean isAllowChangeTexture(Entity entity, TextureHolder textureHolder, Layer layer, Part part) {
        return multiModel.isAllowChangeTexture(entity, textureHolder, layer, part);
    }

    @Override
    public void setTextureHolder(TextureHolder textureHolder, Layer layer, Part part) {
        multiModel.setTextureHolder(textureHolder, layer, part);
        if (layer == Layer.SKIN) {
            refreshDimensions();
        }
    }

    @Override
    public void setColor(TextureColors color) {
        multiModel.setColor(color);
    }

    @Override
    public TextureColors getColor() {
        return multiModel.getColor();
    }

    @Override
    public TextureHolder getTextureHolder(Layer layer, Part part) {
        return multiModel.getTextureHolder(layer, part);
    }

    public void setRandomTexture() {
        LMTextureManager.INSTANCE.getAllTextures().stream()
                .filter(h -> h.hasSkinTexture(false, isMale()))//野生テクスチャである
                .filter(h -> LMModelManager.INSTANCE.hasModel(h.getModelName()))//モデルがある
                .min(Comparator.comparingInt(h -> ThreadLocalRandom.current().nextInt()))//ランダム抽出
                .ifPresent(h -> Arrays.stream(TextureColors.values())
                        .filter(c -> h.getTexture(c, false, false, isMale()).isPresent())
                        .min(Comparator.comparingInt(c -> ThreadLocalRandom.current().nextInt()))
                        .ifPresent(c -> {
                            this.setColor(c);
                            this.setTextureHolder(h, Layer.SKIN, Part.HEAD);
                            if (h.hasArmorTexture()) {
                                setTextureHolder(h, Layer.INNER, Part.HEAD);
                                setTextureHolder(h, Layer.INNER, Part.BODY);
                                setTextureHolder(h, Layer.INNER, Part.LEGS);
                                setTextureHolder(h, Layer.INNER, Part.FEET);
                                setTextureHolder(h, Layer.OUTER, Part.HEAD);
                                setTextureHolder(h, Layer.OUTER, Part.BODY);
                                setTextureHolder(h, Layer.OUTER, Part.LEGS);
                                setTextureHolder(h, Layer.OUTER, Part.FEET);
                            }
                        }));
    }

}
