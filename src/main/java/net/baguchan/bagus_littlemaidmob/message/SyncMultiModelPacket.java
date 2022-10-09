package net.baguchan.bagus_littlemaidmob.message;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.bagus_littlemaidmob.resource.util.ArmorSets;
import net.baguchan.bagus_littlemaidmob.resource.util.TextureColors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncMultiModelPacket {
	private final int entityId;
	private final String textureName;
	private final ArmorSets<String> armorTextureName = new ArmorSets<>();
	private final TextureColors color;
	private final boolean isContract;

	public SyncMultiModelPacket(FriendlyByteBuf buf) {
		entityId = buf.readVarInt();
		textureName = buf.readUtf(32767);
		for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
			armorTextureName.setArmor(buf.readUtf(32767), part);
		}
		color = buf.readEnum(TextureColors.class);
		isContract = buf.readBoolean();
	}

	public SyncMultiModelPacket(Entity entity, IHasMultiModel hasMultiModel) {
		entityId = entity.getId();
		textureName = hasMultiModel.getTextureHolder(IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD)
				.getTextureName();
		for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
			armorTextureName.setArmor(hasMultiModel.getTextureHolder(IHasMultiModel.Layer.INNER, part)
					.getTextureName(), part);
		}
		color = hasMultiModel.getColor();
		isContract = hasMultiModel.isContract();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeVarInt(entityId);
		buf.writeUtf(textureName);
		for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
			buf.writeUtf(armorTextureName.getArmor(part).orElseThrow(IllegalArgumentException::new));
		}
		buf.writeEnum(color);
		buf.writeBoolean(isContract);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				applyMultiModelClient(entityId, textureName, armorTextureName, color, isContract);
			} else {
				Player player = ctx.get().getSender();
				if (player == null) {
					return;
				}
				applyMultiModelServer(entityId, textureName, armorTextureName, color, isContract, player);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendC2SPacket(Entity entity, IHasMultiModel hasMultiModel) {
		LittleMaidMod.CHANNEL.sendToServer(new SyncMultiModelPacket(entity, hasMultiModel));
	}

	public static void sendS2CPacket(Entity entity, IHasMultiModel hasMultiModel) {
		LittleMaidMod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
				new SyncMultiModelPacket(entity, hasMultiModel));
	}

	@OnlyIn(Dist.CLIENT)
	public static void applyMultiModelClient(int entityId, String textureName, ArmorSets<String> armorTextureName,
											 TextureColors color, boolean isContract) {
		Level level = Minecraft.getInstance().level;
		if (level == null) return;
		Entity entity = level.getEntity(entityId);
		if (!(entity instanceof IHasMultiModel)) return;
		IHasMultiModel multiModel = (IHasMultiModel) entity;
		multiModel.setColor(color);
		multiModel.setContract(isContract);
		LMTextureManager textureManager = LMTextureManager.INSTANCE;
		textureManager.getTexture(textureName).filter(textureHolder ->
						multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD))
				.ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD));
		for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
			String armorName = armorTextureName.getArmor(part)
					.orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
			textureManager.getTexture(armorName).filter(textureHolder ->
							multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.INNER, part))
					.ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.INNER, part));
		}
	}

	public static void applyMultiModelServer(int entityId, String textureName, ArmorSets<String> armorTextureName, TextureColors color,
											 boolean isContract, Player player) {
		Entity entity = player.level.getEntity(entityId);
		if (!(entity instanceof IHasMultiModel)) return;
		IHasMultiModel multiModel = (IHasMultiModel) entity;
		multiModel.setColor(color);
		multiModel.setContract(isContract);
		LMTextureManager textureManager = LMTextureManager.INSTANCE;
		textureManager.getTexture(textureName).filter(textureHolder ->
						multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD))
				.ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.SKIN, IHasMultiModel.Part.HEAD));
		for (IHasMultiModel.Part part : IHasMultiModel.Part.values()) {
			String armorName = armorTextureName.getArmor(part)
					.orElseThrow(() -> new IllegalStateException("テクスチャが存在しません。"));
			textureManager.getTexture(armorName).filter(textureHolder ->
							multiModel.isAllowChangeTexture(entity, textureHolder, IHasMultiModel.Layer.INNER, part))
					.ifPresent(textureHolder -> multiModel.setTextureHolder(textureHolder, IHasMultiModel.Layer.INNER, part));
		}
		sendS2CPacket(entity, multiModel);
	}

}