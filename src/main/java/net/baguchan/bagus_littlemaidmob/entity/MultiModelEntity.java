package net.baguchan.bagus_littlemaidmob.entity;

import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.entity.compound.MultiModelCompound;
import net.baguchan.bagus_littlemaidmob.entity.compound.SoundPlayable;
import net.baguchan.bagus_littlemaidmob.entity.compound.SoundPlayableCompound;
import net.baguchan.bagus_littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.bagus_littlemaidmob.message.SyncMultiModelPacket;
import net.baguchan.bagus_littlemaidmob.registry.ModEntities;
import net.baguchan.bagus_littlemaidmob.resource.holder.ConfigHolder;
import net.baguchan.bagus_littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.bagus_littlemaidmob.resource.util.LMSounds;
import net.baguchan.bagus_littlemaidmob.resource.util.TextureColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * テスト用エンティティ
 */
public class MultiModelEntity extends TamableAnimal implements IHasMultiModel, SoundPlayable {
    protected final MultiModelCompound multiModel;
    protected final SoundPlayableCompound soundPlayer;

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
        soundPlayer = new SoundPlayableCompound(this,
                () -> multiModel.getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
    }

    public void setRandomTexture() {

    }

    @Override
    public void tick() {
        super.tick();
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        this.setRandomTexture();
        SyncMultiModelPacket.sendS2CPacket(this, this);

        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    @Override
    public boolean isFood(ItemStack p_27600_) {
        return false;
    }

    //STOP EVERYTING! - by queen
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
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
        if (!level.isClientSide()) {
            SyncMultiModelPacket.sendS2CPacket(this, this);
        }
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
    public void setTame(boolean p_21836_) {
        this.setContract(p_21836_);
    }

    @Override
    public boolean isTame() {
        return this.isContract();
    }

    @Override
    public boolean isContract() {
        return multiModel.isContract();
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

    public void writeCustomPacket(FriendlyByteBuf packet) {
        multiModel.writeToPacket(packet);
    }

    public void readCustomPacket(FriendlyByteBuf packet) {
        multiModel.readFromPacket(packet);
    }

    public void readMultiModel(MultiModelCompound multiModelCompound) {
        CompoundTag tag = new CompoundTag();
        multiModelCompound.writeToNbt(tag);
        this.multiModel.readFromNbt(tag);
    }
}
