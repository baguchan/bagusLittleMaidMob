package net.baguchan.bagus_littlemaidmob.entity.compound;

import net.baguchan.bagus_littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.bagus_littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.bagus_littlemaidmob.resource.util.ArmorPart;
import net.baguchan.bagus_littlemaidmob.resource.util.ArmorSets;
import net.baguchan.bagus_littlemaidmob.resource.util.TextureColors;
import net.baguchan.bagus_littlemaidmob.resource.util.TexturePair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * モデル/テクスチャの管理クラス
 */
public class MultiModelCompound implements IHasMultiModel {

    private final Entity entity;
    private final TextureHolder defaultMainPackage;
    private final TextureHolder defaultArmorPackage;

    private TextureHolder skinTexHolder;
    private IMultiModel skinModel;
    private TexturePair skinTexture;

    private final ArmorSets<TextureHolder> armorsTexHolder = new ArmorSets<>();
    private final ArmorSets<ArmorPart> armorsData = new ArmorSets<>();

    private TextureColors color;
    private boolean isContract;

    public MultiModelCompound(LivingEntity entity, TextureHolder defaultMainPackage, TextureHolder defaultArmorPackage) {
        this.entity = entity;
        this.defaultMainPackage = defaultMainPackage;
        this.defaultArmorPackage = defaultArmorPackage;
        this.color = TextureColors.BROWN;
        update();
    }

    public void update() {
        updateMain();
        updateArmor();
    }

    public void updateMain() {
        if (skinTexHolder == null) {
            skinTexHolder = defaultMainPackage;
        }
        LMModelManager modelManager = LMModelManager.INSTANCE;
        skinModel = modelManager.getOrDefaultModel(skinTexHolder.getModelName(), Layer.SKIN);
        skinTexture = new TexturePair(skinTexHolder.getTexture(color, isContract, false).orElse(null),
                skinTexHolder.getTexture(color, isContract, true).orElse(null));
    }

    public void updateArmor() {
        int index = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (4 < index) {
                break;
            }
            updateArmorPart(Part.getPart(index++), getName(stack.getItem()), getDamagePercent(stack));
        }
    }

    private String getName(Item item) {
        //クライアント限定
        if (entity.level.isClientSide() && item instanceof ArmorItem) {
            return ((ArmorItem) item).getMaterial().getName().toLowerCase();
        }
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
        return location.toString();
    }

    private float getDamagePercent(ItemStack stack) {
        float damagePercent = 0F;
        if (stack.isDamageableItem() && 0 < stack.getMaxDamage()) {
            damagePercent = (float) stack.getDamageValue() / (float) stack.getMaxDamage();
        }
        return damagePercent;
    }

    private void updateArmorPart(Part part, String armorName, float damagePercent) {
        TextureHolder textureHolder = armorsTexHolder.getArmor(part).orElse(defaultArmorPackage);
        armorsTexHolder.setArmor(textureHolder, part);
        LMModelManager manager = LMModelManager.INSTANCE;
        ArmorPart.Builder dataBuilder = ArmorPart.Builder.newInstance();
        dataBuilder.innerModel(manager.getOrDefaultModel(textureHolder.getModelName(), Layer.INNER));
        dataBuilder.outerModel(manager.getOrDefaultModel(textureHolder.getModelName(), Layer.OUTER));
        dataBuilder.innerTex(textureHolder.getArmorTexture(Layer.INNER, armorName,
                damagePercent, false).orElse(null));
        dataBuilder.innerTexLight(textureHolder.getArmorTexture(Layer.INNER, armorName,
                damagePercent, true).orElse(null));
        dataBuilder.outerTex(textureHolder.getArmorTexture(Layer.OUTER, armorName,
                damagePercent, false).orElse(null));
        dataBuilder.outerTexLight(textureHolder.getArmorTexture(Layer.OUTER, armorName,
                damagePercent, true).orElse(null));
        armorsData.setArmor(dataBuilder.build(), part);
    }

    @Override
    public void setTextureHolder(TextureHolder textureHolder, Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            skinTexHolder = textureHolder;
            updateMain();
        } else {
            armorsTexHolder.setArmor(textureHolder, part);
            int index = 0;
            for (ItemStack stack : entity.getArmorSlots()) {
                if (part.getIndex() == index++) {
                    updateArmorPart(part, getName(stack.getItem()), getDamagePercent(stack));
                }
            }
        }
    }

    @Override
    public TextureHolder getTextureHolder(Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            return skinTexHolder;
        } else {
            return armorsTexHolder.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("防具テクスチャホルダーが存在しません。"));
        }
    }

    @Override
    public Optional<IMultiModel> getModel(Layer layer, Part part) {
        if (layer == Layer.SKIN) {
            return Optional.ofNullable(skinModel);
        } else {
            IMultiModel model = armorsData.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("防具データが存在しません"))
                    .getModel(layer);
            return Optional.ofNullable(model);
        }
    }

    @Override
    public Optional<ResourceLocation> getTexture(Layer layer, Part part, boolean isLight) {
        if (layer == Layer.SKIN) {
            return Optional.ofNullable(skinTexture.getTexture(isLight));
        } else {
            ResourceLocation resourceLocation = armorsData.getArmor(part)
                    .orElseThrow(() -> new IllegalStateException("防具データが存在しません"))
                    .getTexture(layer, isLight);
            return Optional.ofNullable(resourceLocation);
        }
    }
    //ちと処理重いか？
    @Override
    public boolean isArmorVisible(Part part) {
        int index = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (part.getIndex() == index++ && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isArmorGlint(Part part) {
        int index = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (part.getIndex() == index++ && !stack.isEmpty()) {
                return stack.hasFoil();
            }
        }
        return false;
    }

    @Override
    public boolean isAllowChangeTexture(Entity changer, TextureHolder textureHolder, Layer layer, Part part) {
        return true;
    }

    public void setColor(TextureColors color) {
        this.color = color;
        updateMain();
    }

    public TextureColors getColor() {
        return color;
    }

    public void setContract(boolean contract) {
        this.isContract = contract;
        updateMain();
    }

    @Override
    public boolean isContract() {
        return isContract;
    }

    public void writeToNbt(CompoundTag nbt) {
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

    public void readFromNbt(CompoundTag nbt) {
        if (nbt.contains("SkinColor")) {
            setColor(TextureColors.getColor(nbt.getByte("SkinColor")));
        }
        setContract(nbt.getBoolean("IsContract"));
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
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
    }

    public void writeToPacket(FriendlyByteBuf packet) {
        packet.writeEnum(getColor());
        packet.writeBoolean(isContract());
        packet.writeUtf(getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
        for (Part part : Part.values()) {
            packet.writeUtf(getTextureHolder(Layer.INNER, part).getTextureName());
            packet.writeUtf(getTextureHolder(Layer.OUTER, part).getTextureName());
        }
    }

    public void readFromPacket(FriendlyByteBuf packet) {
        //readUtf()はクラ処理。このメソッドでは、クラ側なので問題なし
        setColor(packet.readEnum(TextureColors.class));
        setContract(packet.readBoolean());
        LMTextureManager textureManager = LMTextureManager.INSTANCE;
        textureManager.getTexture(packet.readUtf())
                .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.SKIN, Part.HEAD));
        for (Part part : Part.values()) {
            textureManager.getTexture(packet.readUtf())
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.INNER, part));
            textureManager.getTexture(packet.readUtf())
                    .ifPresent(textureHolder -> setTextureHolder(textureHolder, Layer.OUTER, part));
        }
    }

}
