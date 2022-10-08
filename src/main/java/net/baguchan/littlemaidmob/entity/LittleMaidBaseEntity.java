package net.baguchan.littlemaidmob.entity;

import net.baguchan.littlemaidmob.menutype.LittleMaidInventoryMenu;
import net.baguchan.littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.TextureColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class LittleMaidBaseEntity extends MultiModelEntity {

    protected SimpleContainer inventory;

    public LittleMaidBaseEntity(EntityType<? extends MultiModelEntity> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        ((GroundPathNavigation) getNavigation()).setCanPassDoors(true);
    }

    @Override
    protected InteractionResult mobInteract(Player p_21472_, InteractionHand p_21473_) {
        ItemStack itemstack = p_21472_.getItemInHand(p_21473_);
        Item item = itemstack.getItem();
        InteractionResult interactionresult = super.mobInteract(p_21472_, p_21473_);
        if (!interactionresult.consumesAction()) {
            if (p_21472_ instanceof ServerPlayer) {
                NetworkHooks.openScreen((ServerPlayer) p_21472_,
                        new SimpleMenuProvider((windowId, inv, playerEntity) ->
                                new LittleMaidInventoryMenu(windowId, inv, this), Component.empty()),
                        buf -> buf.writeVarInt(this.getId()));
                return InteractionResult.SUCCESS;
            }
        }
        return interactionresult;
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

        if (!this.inventory.isEmpty()) {
            nbt.put("Inventory", this.inventory.createTag());
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

        this.inventory.fromTag(nbt.getList("Inventory", 10));
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
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
                .filter(h -> h.hasSkinTexture(false))//野生テクスチャである
                .filter(h -> LMModelManager.INSTANCE.hasModel(h.getModelName()))//モデルがある
                .min(Comparator.comparingInt(h -> ThreadLocalRandom.current().nextInt()))//ランダム抽出
                .ifPresent(h -> Arrays.stream(TextureColors.values())
                        .filter(c -> h.getTexture(c, false, false).isPresent())
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

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            SimpleContainer inv = getInventory();
            inv.setItem(1 + 18 + slot.getIndex(), stack);
            multiModel.updateArmor();
        } else if (slot == EquipmentSlot.MAINHAND) {
            getInventory().setItem(0, stack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            getInventory().setItem(18 + 4 + 1, stack);
        }
    }
}
