package net.baguchan.littlemaidmob.entity;
import net.baguchan.littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.littlemaidmob.entity.compound.MultiModelCompound;
import net.baguchan.littlemaidmob.entity.compound.SoundPlayable;
import net.baguchan.littlemaidmob.entity.compound.SoundPlayableCompound;
import net.baguchan.littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.littlemaidmob.registry.ModEntities;
import net.baguchan.littlemaidmob.resource.holder.ConfigHolder;
import net.baguchan.littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.LMSounds;
import net.baguchan.littlemaidmob.resource.util.TextureColors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * テスト用エンティティ
 */
public class MultiModelEntity extends PathfinderMob implements IHasMultiModel, SoundPlayable {
    protected final MultiModelCompound multiModel;
    protected final SoundPlayableCompound soundPlayer;

    protected boolean isMale;

    public MultiModelEntity(Level worldIn) {
        this(ModEntities.MULTI_MODEL.get(), worldIn);
    }

    public MultiModelEntity(EntityType<? extends MultiModelEntity> type, Level worldIn) {
        super(type, worldIn);
        multiModel = new MultiModelCompound(this,
                LMTextureManager.INSTANCE.getTexture("default")
                        .orElseThrow(() -> new IllegalStateException("デフォルトモデルが存在しません。")),
                LMTextureManager.INSTANCE.getTexture("default")
                        .orElseThrow(() -> new IllegalStateException("デフォルトモデルが存在しません。")));
        this.setRandomTexture();
        soundPlayer = new SoundPlayableCompound(this,
                () -> multiModel.getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
    }

    public void setRandomTexture() {

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        multiModel.writeToNbt(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        multiModel.readFromNbt(nbt);
    }

    @Override
    public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
        ItemStack stack = p_19980_.getItemInHand(p_19982_);
        if (stack.getItem() instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) stack.getItem();
            this.setItemSlot(armor.getSlot(), stack);
            return InteractionResult.sidedSuccess(p_19980_.level.isClientSide());
        }
        if (level.isClientSide()) {
            //openGUI(player.isSneaking());
            play(LMSounds.LIVING_DAYTIME);
        }
        return super.interactAt(p_19980_, p_19981_, p_19982_);
    }


    //このままだとEntityDimensionsが作っては捨てられてを繰り返すのでパフォーマンスはよろしくない
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions;
        IMultiModel model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        float height = model.getHeight(this, pose);
        float width = model.getWidth(this, pose);
        dimensions = EntityDimensions.fixed(width, height);
        return dimensions.scale(getScale());
    }

    //視点調整


    @Override
    public float getEyeHeightAccess(Pose pose, EntityDimensions size) {
        if (multiModel == null) return size.height * 0.85F;
        return getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel())
                .getEyeHeight(this, pose);
    }

    //上になんか乗ってるやつのオフセット
    @Override
    public double getMyRidingOffset() {
        IMultiModel model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        return model.getMountedYOffset(this);
    }


    //騎乗時のオフセット
    @Override
    public double getPassengersRidingOffset() {
        IMultiModel model = getModel(Layer.SKIN, Part.HEAD)
                .orElse(LMModelManager.INSTANCE.getDefaultModel());
        return model.getyOffset(this) - this.getBbHeight();
    }


    @Override
    public void setItemSlot(EquipmentSlot p_21416_, ItemStack p_21417_) {
        super.setItemSlot(p_21416_, p_21417_);
        if (p_21416_.getType() == EquipmentSlot.Type.ARMOR) {
            multiModel.updateArmor();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ResourceLocation> getTexture(IHasMultiModel.Layer layer, IHasMultiModel.Part part, boolean isLight) {
        return multiModel.getTexture(layer, part, isLight);
    }

    @Override
    public void setTextureHolder(TextureHolder textureHolder, Layer layer, Part part) {
        multiModel.setTextureHolder(textureHolder, layer, part);
        if (layer == Layer.SKIN) {
            refreshDimensions();
        }
    }

    @Override
    public TextureHolder getTextureHolder(IHasMultiModel.Layer layer, IHasMultiModel.Part part) {
        return multiModel.getTextureHolder(layer, part);
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
    public void setContract(boolean isContract) {
        multiModel.setContract(isContract);
    }

    @Override
    public boolean isContract() {
        return multiModel.isContract();
    }

    @Override
    public void setMale(boolean isMale) {
        multiModel.setMale(isMale);
    }

    @Override
    public boolean isMale() {
        return multiModel.isMale();
    }

    @Override
    public Optional<IMultiModel> getModel(Layer layer, Part part) {
        return multiModel.getModel(layer, part);
    }


    @Override
    public boolean isArmorVisible(Part part) {
        return multiModel.isArmorVisible(part);
    }

    @Override
    public boolean isArmorGlint(Part part) {
        return multiModel.isArmorGlint(part);
    }

    @Override
    public boolean isAllowChangeTexture(Entity changer, TextureHolder textureHolder, IHasMultiModel.Layer layer, IHasMultiModel.Part part) {
        return true;
    }

    @Override
    public void play(String soundName) {
        soundPlayer.play(soundName);
    }

    @Override
    public void setConfigHolder(ConfigHolder configHolder) {
        soundPlayer.setConfigHolder(configHolder);
    }

    @Override
    public ConfigHolder getConfigHolder() {
        return soundPlayer.getConfigHolder();
    }


    public MultiModelCompound getMultiModel() {
        return multiModel;
    }

    public void readMultiModel(MultiModelCompound multiModelCompound){
        CompoundTag tag = new CompoundTag();
        multiModelCompound.writeToNbt(tag);
        this.multiModel.readFromNbt(tag);
    }
}
