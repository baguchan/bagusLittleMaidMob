package net.baguchan.littlemaidmob.entity;

import com.google.common.collect.Lists;
import net.baguchan.littlemaidmob.menutype.LittleMaidInventoryMenu;
import net.baguchan.littlemaidmob.message.SyncMultiModelPacket;
import net.baguchan.littlemaidmob.registry.ModTags;
import net.baguchan.littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.TextureColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LittleMaidBaseEntity extends MultiModelEntity implements ContainerListener, InventoryCarrier {
	private static final EntityDataAccessor<String> MOVING_STATE = SynchedEntityData.defineId(LittleMaidBaseEntity.class, EntityDataSerializers.STRING);
	protected SimpleContainer inventory;

	public LittleMaidBaseEntity(EntityType<? extends LittleMaidBaseEntity> p_21683_, Level p_21684_) {
		super(p_21683_, p_21684_);
		((GroundPathNavigation) getNavigation()).setCanPassDoors(true);
		this.createInventory();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return MultiModelEntity.createAttributes().add(Attributes.MAX_HEALTH, 20.0F).add(Attributes.MOVEMENT_SPEED, 0.22F);
	}

	protected void createInventory() {
		SimpleContainer simplecontainer = this.inventory;
		this.inventory = new SimpleContainer(18 + 4 + 2);
		if (simplecontainer != null) {
			simplecontainer.removeListener(this);
			int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = simplecontainer.getItem(j);
				if (!itemstack.isEmpty()) {
					this.inventory.setItem(j, itemstack.copy());
				}
			}
		}

		this.inventory.addListener(this);
		this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(MOVING_STATE, MoveState.NORMAL.name());
	}

	public void setMovingState(MoveState moveState) {
		this.entityData.set(MOVING_STATE, moveState.name());
	}

	public String getMovingState() {
		return this.entityData.get(MOVING_STATE);
	}

	@Override
	public InteractionResult mobInteract(Player p_21472_, InteractionHand p_21473_) {
		ItemStack itemstack = p_21472_.getItemInHand(p_21473_);
		Item item = itemstack.getItem();
		InteractionResult interactionresult = super.mobInteract(p_21472_, p_21473_);
		if (isTame()) {
			if (itemstack.is(ModTags.Items.LITTLE_MAID_HEALABLE)) {
				if (itemstack.isEdible()) {
					heal(itemstack.getFoodProperties(this).getNutrition());
					itemstack.finishUsingItem(this.level, this);
					return InteractionResult.SUCCESS;
				} else if (itemstack.is(ModTags.Items.CAKE)) {
					heal(20);
					itemstack.shrink(1);
					return InteractionResult.SUCCESS;
				} else {
					itemstack.shrink(1);
					heal(1);
					return InteractionResult.SUCCESS;
				}
			}


			if (!interactionresult.shouldSwing()) {
				if (p_21472_ instanceof ServerPlayer) {
					NetworkHooks.openScreen((ServerPlayer) p_21472_,
							new SimpleMenuProvider((windowId, inv, playerEntity) ->
									new LittleMaidInventoryMenu(windowId, inv, this.getId()), Component.empty()),
							buf -> buf.writeVarInt(this.getId()));
					return InteractionResult.SUCCESS;
				}
			}
		} else if (itemstack.is(ModTags.Items.CAKE)) {
			heal(this.getMaxHealth());
			this.setMovingState(MoveState.WAITING);
			this.setOwnerUUID(p_21472_.getUUID());
			this.setTame(true);
			this.setContract(true);
			itemstack.shrink(1);
			return InteractionResult.SUCCESS;
		}
		return interactionresult;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		ListTag listtag = new ListTag();

		for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
			ItemStack itemstack = this.inventory.getItem(i);
			if (!itemstack.isEmpty()) {
				listtag.add(itemstack.save(new CompoundTag()));
			}
		}
		nbt.put("Inventory", listtag);

		nbt.putByte("SkinColor", (byte) getColor().getIndex());
		nbt.putBoolean("IsContract", isContract());
		nbt.putString("SkinTexture", getTextureHolder(Layer.SKIN, Part.HEAD).getTextureName());
		for (Part part : Part.values()) {
			nbt.putString("ArmorTextureInner" + part.getPartName(),
					getTextureHolder(Layer.INNER, part).getTextureName());
			nbt.putString("ArmorTextureOuter" + part.getPartName(),
					getTextureHolder(Layer.OUTER, part).getTextureName());
		}

		nbt.putString("MoveState", this.getMovingState());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		ListTag listtag = nbt.getList("Inventory", 10);

		for (int i = 0; i < listtag.size(); ++i) {
			ItemStack itemstack = ItemStack.of(listtag.getCompound(i));
			if (!itemstack.isEmpty()) {
				this.inventory.addItem(itemstack);
			}
		}
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

		this.setMovingState(MoveState.get(nbt.getString("MoveState")));
		if (!level.isClientSide()) {
			SyncMultiModelPacket.sendS2CPacket(this, this);
		}
	}

	public SimpleContainer getInventory() {
		return inventory;
	}

	protected void dropEquipment() {
		super.dropEquipment();
		this.inventory.removeAllItems().forEach(this::spawnAtLocation);
	}

	public SlotAccess getSlot(int p_149514_) {
		if (p_149514_ >= 0 && p_149514_ < 36) {
			return SlotAccess.forContainer(this.getInventory(), p_149514_);
		}
		return super.getSlot(p_149514_);
	}
	//マルチモデル関連

	@Override
	public boolean isAllowChangeTexture(Entity entity, TextureHolder textureHolder, Layer layer, Part part) {
		return multiModel.isAllowChangeTexture(entity, textureHolder, layer, part);
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
	public Iterable<ItemStack> getHandSlots() {
		return Lists.newArrayList(getMainHandItem(), getOffhandItem());
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Lists.newArrayList(
				getItemBySlot(EquipmentSlot.FEET),
				getItemBySlot(EquipmentSlot.LEGS),
				getItemBySlot(EquipmentSlot.CHEST),
				getItemBySlot(EquipmentSlot.HEAD));
	}

	@Override
	protected void hurtCurrentlyUsedShield(float amount) {
		if (this.useItem.is(Items.SHIELD)) {

			if (amount >= 3.0F) {
				int i = 1 + Mth.floor(amount);
				InteractionHand hand = this.getUsedItemHand();
				this.useItem.hurtAndBreak(i, (LivingEntity) this, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));
				if (this.useItem.isEmpty()) {
					if (hand == InteractionHand.MAIN_HAND) {
						this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					} else {
						this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}

					this.useItem = ItemStack.EMPTY;
					this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
				}
			}

		}
	}

	protected void hurtArmor(DamageSource p_36251_, float p_36252_) {
		this.hurtArmor(p_36251_, p_36252_, false);
	}

	protected void hurtHelmet(DamageSource p_150103_, float p_150104_) {
		this.hurtArmor(p_150103_, p_150104_, true);
	}

	public void hurtArmor(DamageSource p_150073_, float p_150074_, boolean onlyHelmet) {
		if (!(p_150074_ <= 0.0F)) {
			p_150074_ /= 4.0F;
			if (p_150074_ < 1.0F) {
				p_150074_ = 1.0F;
			}

			if (!onlyHelmet) {
				int i = 0;
				for (ItemStack itemstack : this.getArmorSlots()) {
					if ((!p_150073_.isFire() || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ArmorItem) {
						int finalI = i;
						itemstack.hurtAndBreak((int) p_150074_, this, (p_35997_) -> {
							p_35997_.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, finalI));
						});
						i++;
					}
				}
			} else {
				ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
				if ((!p_150073_.isFire() || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ArmorItem) {
					itemstack.hurtAndBreak((int) p_150074_, this, (p_35997_) -> {
						p_35997_.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, EquipmentSlot.HEAD.getIndex()));
					});
				}
			}

		}
	}

	@Override
	public ItemStack getProjectile(ItemStack stack) {
		if (!(stack.getItem() instanceof ProjectileWeaponItem)) {
			return ItemStack.EMPTY;
		} else {
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) stack.getItem()).getAllSupportedProjectiles();
			ItemStack itemStack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
			if (!itemStack.isEmpty()) {
				return itemStack;
			} else {
				predicate = ((ProjectileWeaponItem) stack.getItem()).getSupportedHeldProjectiles();

				for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
					ItemStack itemStack2 = this.getInventory().getItem(i);
					if (predicate.test(itemStack2)) {
						return itemStack2;
					}
				}

				return ItemStack.EMPTY;
			}
		}
	}

	@Override
	public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
		super.setItemSlot(slot, stack);
		if (slot.getType() == EquipmentSlot.Type.ARMOR) {
			this.inventory.setItem(1 + 18 + slot.getIndex(), stack);
			multiModel.updateArmor();
		} else if (slot == EquipmentSlot.MAINHAND) {
			this.inventory.setItem(0, stack);
		} else if (slot == EquipmentSlot.OFFHAND) {
			this.inventory.setItem(18 + 4 + 1, stack);
		}
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlot slot) {
		if (this.inventory != null) {
			if (slot.getType() == EquipmentSlot.Type.ARMOR) {
				return this.inventory.getItem(1 + 18 + slot.getIndex());
			} else if (slot == EquipmentSlot.MAINHAND) {
				return this.inventory.getItem(0);
			} else if (slot == EquipmentSlot.OFFHAND) {
				return this.inventory.getItem(18 + 4 + 1);
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void containerChanged(Container p_18983_) {

	}

	public boolean hasInventoryChanged(Container p_149512_) {
		return this.inventory != p_149512_;
	}

	private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
		if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
			return itemHandler.cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		if (itemHandler != null) {
			net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
			itemHandler = null;
			oldHandler.invalidate();
		}
	}

	public enum MoveState {
		WAITING, NORMAL, FREEDOM;

		private static final Map<String, MoveState> lookup;

		static {
			lookup = Arrays.stream(values()).collect(Collectors.toMap(Enum::name, p_220362_0_ -> p_220362_0_));
		}

		public static MoveState get(String nameIn) {
			for (MoveState movestate : values()) {
				if (movestate.name().equals(nameIn))
					return movestate;
			}
			return NORMAL;
		}

		private MoveState() {
		}
	}
}
