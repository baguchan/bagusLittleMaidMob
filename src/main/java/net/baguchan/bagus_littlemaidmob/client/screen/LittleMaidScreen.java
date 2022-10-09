package net.baguchan.bagus_littlemaidmob.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.baguchan.bagus_littlemaidmob.entity.LittleMaidBaseEntity;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.menutype.LittleMaidInventoryMenu;
import net.baguchan.bagus_littlemaidmob.message.SyncSetModePacket;
import net.baguchan.bagus_littlemaidmob.message.SyncSoundConfigMessage;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMConfigManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LittleMaidScreen extends AbstractContainerScreen<LittleMaidInventoryMenu> {
	private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("lmreengaged", "textures/gui/container/littlemaidinventory2.png");
	private final LittleMaidBaseEntity maid;
	private float xMouse;
	private float yMouse;
	private static final ItemStack ARMOR = Items.LEATHER_CHESTPLATE.getDefaultInstance();
	private static final ItemStack BOOK = Items.BOOK.getDefaultInstance();
	private static final ItemStack NOTE = Items.NOTE_BLOCK.getDefaultInstance();
	private static final ItemStack FEATHER = Items.FEATHER.getDefaultInstance();


	public LittleMaidScreen(LittleMaidInventoryMenu maidInventoryContainer, Inventory inventory, Component titleIn) {
		super(maidInventoryContainer, inventory, titleIn);
		this.maid = maidInventoryContainer.maid;
		this.passEvents = false;
		this.imageHeight = 256;
		this.imageWidth = 256;
	}

	@Override
	protected void init() {
		super.init();
		if (this.maid == null) {
			minecraft.setScreen(null);
			return;
		}

		int left = (int) ((this.width - imageWidth) / 2F) - 5;
		int top = (int) ((this.height - imageHeight) / 2F);
		int size = 20;
		int layer = -1;
		/*this.addRenderableWidget(new Button(left - size, top + size * ++layer, size, size, Component.literal(""),
				button -> OpenIFFScreenPacket.sendC2SPacket(maid)) {
			@Override
			public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
				super.renderButton(matrices, mouseX, mouseY, partialTicks);
				itemRenderer.renderGuiItem(BOOK, this.x - 8 + this.width / 2, this.y - 8 + this.height / 2);
			}
		});*/
		this.addRenderableWidget(new Button(left - size, top + size * ++layer, size, size, Component.literal(""),
				button -> {
					maid.setConfigHolder(LMConfigManager.INSTANCE.getAnyConfig());
					SyncSoundConfigMessage.sendC2SPacket(maid, maid.getConfigHolder().getName());
				}, (button, matrices, x, y) -> {
			String text = maid.getConfigHolder().getName();
			float renderX = Math.max(0, x - font.width(text) / 2F);
			font.draw(matrices, text, renderX,
					y - font.lineHeight / 2F, 0xFFFFFF);
		}) {
			@Override
			public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
				super.renderButton(matrices, mouseX, mouseY, partialTicks);
				itemRenderer.renderGuiItem(NOTE, this.x - 8 + this.width / 2, this.y - 8 + this.height / 2);
			}
		});
		this.addRenderableWidget(new Button(left - size, top + size * ++layer, size, size, Component.literal(""),
				button -> minecraft.setScreen(new ModelSelectScreen(title, maid.level, (IHasMultiModel) maid))) {
			@Override
			public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
				super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
				itemRenderer.renderGuiItem(ARMOR, this.x - 8 + this.width / 2, this.y - 8 + this.height / 2);
			}
		});
		this.addRenderableWidget(new Button(left - size, top + size * ++layer, size, size, Component.literal(""),
				button -> {
					SyncSetModePacket.sendC2SPacket((Entity) maid, LittleMaidBaseEntity.MoveState.get(maid.getMovingState()) == LittleMaidBaseEntity.MoveState.FREEDOM
							? LittleMaidBaseEntity.MoveState.WAITING
							: LittleMaidBaseEntity.MoveState.FREEDOM);
				}) {
			@Override
			public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
				super.renderButton(matrixStack, mouseX, mouseY, delta);
				itemRenderer.renderGuiItem(FEATHER, this.x - 8 + this.width / 2, this.y - 8 + this.height / 2);
			}
		});
	}

	protected void renderBg(PoseStack p_98821_, float p_98822_, int p_98823_, int p_98824_) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, HORSE_INVENTORY_LOCATION);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(p_98821_, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float) (i + 51) - this.xMouse, (float) (j + 75 - 50) - this.yMouse, this.maid);
	}

	public void render(PoseStack p_98826_, int p_98827_, int p_98828_, float p_98829_) {
		this.renderBackground(p_98826_);
		this.xMouse = (float) p_98827_;
		this.yMouse = (float) p_98828_;
		super.render(p_98826_, p_98827_, p_98828_, p_98829_);
		this.renderTooltip(p_98826_, p_98827_, p_98828_);
	}
}