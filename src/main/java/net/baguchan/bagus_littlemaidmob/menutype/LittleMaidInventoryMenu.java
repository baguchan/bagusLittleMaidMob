package net.baguchan.bagus_littlemaidmob.menutype;

import com.mojang.datafixers.util.Pair;
import net.baguchan.bagus_littlemaidmob.entity.LittleMaidBaseEntity;
import net.baguchan.bagus_littlemaidmob.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LittleMaidInventoryMenu extends AbstractContainerMenu {
	private final Container maidContainer;
	public final LittleMaidBaseEntity maid;

	public LittleMaidInventoryMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, data.readVarInt());
	}

	public LittleMaidInventoryMenu(int p_39656_, Inventory p_39657_, int entityId) {
		super(ModMenuTypes.LITTLE_MAID_CONTAINER.get(), p_39656_);
		LittleMaidBaseEntity maid = (LittleMaidBaseEntity) p_39657_.player.level.getEntity(entityId);
		if (maid == null)
			this.maidContainer = new SimpleContainer(18 + 4 + 2);
		else
			this.maidContainer = maid.getInventory();
		this.maid = maid;
		int i = 3;
		p_39657_.startOpen(p_39657_.player);
		int j = -18;
		layoutMaidContainerSlots();
		layoutPlayerInventorySlots(p_39657_, 8, 126);
	}

	private int addSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
		for (int i = 0; i < amount; i++) {
			addSlot(new Slot(handler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	private int addSlotBox(Container handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
		for (int j = 0; j < verAmount; j++) {
			index = addSlotRange(handler, index, x, y, horAmount, dx);
			y += dy;
		}
		return index;
	}

	private void layoutPlayerInventorySlots(Inventory playerInventory, int leftCol, int topRow) {
		// Player inventory
		addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

		// Hotbar
		topRow += 58;
		addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
	}

	private void layoutMaidContainerSlots() {
		//index 0~17
		addSlotBox(maidContainer, 1, 8, 76, 9, 18, 2, 18);

		//18~19
		addSlot(new Slot(maidContainer, 0, 116, 44));
		addSlot(new Slot(maidContainer, 1 + 18 + 4, 152, 44));

		ResourceLocation atlas = new ResourceLocation("textures/atlas/blocks.png");

		//20~23
		addSlot(new Slot(maidContainer, 1 + 18 + EquipmentSlot.HEAD.getIndex(), 8, 8) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return Mob.getEquipmentSlotForItem(stack) == EquipmentSlot.HEAD;
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(atlas, new ResourceLocation("item/empty_armor_slot_helmet"));
			}
		});
		addSlot(new Slot(maidContainer, 1 + 18 + EquipmentSlot.CHEST.getIndex(), 8, 44) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return Mob.getEquipmentSlotForItem(stack) == EquipmentSlot.CHEST;
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(atlas, new ResourceLocation("item/empty_armor_slot_chestplate"));
			}
		});
		addSlot(new Slot(maidContainer, 1 + 18 + EquipmentSlot.LEGS.getIndex(), 80, 8) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return Mob.getEquipmentSlotForItem(stack) == EquipmentSlot.LEGS;
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(atlas, new ResourceLocation("item/empty_armor_slot_leggings"));
			}
		});
		addSlot(new Slot(maidContainer, 1 + 18 + EquipmentSlot.FEET.getIndex(), 80, 44) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return Mob.getEquipmentSlotForItem(stack) == EquipmentSlot.FEET;
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(atlas, new ResourceLocation("item/empty_armor_slot_boots"));
			}
		});
	}

	public boolean stillValid(Player p_39661_) {
		return !this.maid.hasInventoryChanged(this.maid.getInventory()) && this.maidContainer.stillValid(p_39661_) && this.maid.isAlive() && this.maid.distanceTo(p_39661_) < 8.0F;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(invSlot);
		if (slot == null || !slot.hasItem()) {
			return newStack;
		}
		ItemStack originalStack = slot.getItem();
		newStack = originalStack.copy();
		if (invSlot < 18) {//メイド->プレイヤー
			if (!this.moveItemStackTo(originalStack, 24, 60, false)) {
				return ItemStack.EMPTY;
			}
		} else if (invSlot < 24) {//ハンド、防具->メイド
			if (!this.moveItemStackTo(originalStack, 0, 18, true)) {
				return ItemStack.EMPTY;
			}
		} else {//プレイヤー->メイド
			if (!this.moveItemStackTo(originalStack, 0, 18, false)) {
				return ItemStack.EMPTY;
			}
		}

		if (originalStack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}

		return newStack;
	}

	public void removed(Player p_39663_) {
		super.removed(p_39663_);
		this.maidContainer.stopOpen(p_39663_);
	}
}