package net.baguchan.littlemaidmob.menutype;

import com.mojang.datafixers.util.Pair;
import net.baguchan.littlemaidmob.entity.LittleMaidBaseEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LittleMaidInventoryMenu extends AbstractContainerMenu {
	private final Container maidContainer;
	private final LittleMaidBaseEntity maid;

	public LittleMaidInventoryMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, data.readVarInt());
	}

	public LittleMaidInventoryMenu(int p_39656_, Inventory p_39657_, int entityId) {
		super((MenuType<?>) null, p_39656_);
		LittleMaidBaseEntity maid = (LittleMaidBaseEntity) p_39657_.player.level.getEntity(entityId);
		this.maidContainer = maid.getInventory();
		this.maid = maid;
		int i = 3;
		maid.getInventory().startOpen(p_39657_.player);
		int j = -18;

		ResourceLocation atlas = new ResourceLocation("textures/atlas/blocks.png");
		//index 0~17
		for (int i1 = 0; i1 < 2; ++i1) {
			for (int k1 = 0; k1 < 9; ++k1) {
				this.addSlot(new Slot(maidContainer, k1 + i1 * 9 + 9 + 1, 8 + k1 * 18, 76 + i1 * 18 + -18));
			}
		}

		//18~19
		addSlot(new Slot(maidContainer, 0, 116, 44));
		addSlot(new Slot(maidContainer, 1 + 18 + 4, 152, 44) {
			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(atlas, new ResourceLocation("item/empty_armor_slot_shield"));
			}
		});

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

		for (int i1 = 0; i1 < 3; ++i1) {
			for (int k1 = 0; k1 < 9; ++k1) {
				this.addSlot(new Slot(p_39657_, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
			}
		}

		for (int j1 = 0; j1 < 9; ++j1) {
			this.addSlot(new Slot(p_39657_, j1, 8 + j1 * 18, 142));
		}

	}

	public boolean stillValid(Player p_39661_) {
		return this.maidContainer.stillValid(p_39661_) && this.maid.isAlive() && this.maid.distanceTo(p_39661_) < 8.0F;
	}

	public ItemStack quickMoveStack(Player p_39665_, int p_39666_) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(p_39666_);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			int i = this.maidContainer.getContainerSize();
			if (p_39666_ < i) {
				if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).mayPlace(itemstack1)) {
				if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i <= 1 || !this.moveItemStackTo(itemstack1, 1, i, false)) {
				int j = i + 27;
				int k = j + 9;
				if (p_39666_ >= j && p_39666_ < k) {
					if (!this.moveItemStackTo(itemstack1, i, j, false)) {
						return ItemStack.EMPTY;
					}
				} else if (p_39666_ >= i && p_39666_ < j) {
					if (!this.moveItemStackTo(itemstack1, j, k, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.moveItemStackTo(itemstack1, j, j, false)) {
					return ItemStack.EMPTY;
				}

				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	public void removed(Player p_39663_) {
		super.removed(p_39663_);
		this.maidContainer.stopOpen(p_39663_);
	}
}