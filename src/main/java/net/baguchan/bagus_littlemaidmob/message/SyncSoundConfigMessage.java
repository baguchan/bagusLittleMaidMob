package net.baguchan.bagus_littlemaidmob.message;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.entity.compound.SoundPlayable;
import net.baguchan.bagus_littlemaidmob.resource.manager.LMConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncSoundConfigMessage {
	private final int entityId;
	private final String configName;

	public SyncSoundConfigMessage(FriendlyByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.configName = buf.readUtf(32767);
	}

	public SyncSoundConfigMessage(Entity entity, String configName) {
		this.entityId = entity.getId();
		this.configName = configName;
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeVarInt(entityId);
		buf.writeUtf(configName);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				applySoundConfigClient(entityId, configName);
			} else {
				Player player = ctx.get().getSender();
				if (player == null) return;
				applySoundConfigServer(player, entityId, configName);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendC2SPacket(Entity entity, String configName) {
		LittleMaidMod.CHANNEL.sendToServer(new SyncSoundConfigMessage(entity, configName));
	}

	public static void sendS2CPacket(Entity entity, String configName) {
		LittleMaidMod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
				new SyncSoundConfigMessage(entity, configName));
	}

	@OnlyIn(Dist.CLIENT)
	public static void applySoundConfigClient(int entityId, String configName) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		Level world = player.level;
		Entity entity = world.getEntity(entityId);
		if (entity instanceof SoundPlayable) {
			LMConfigManager.INSTANCE.getConfig(configName)
					.ifPresent(((SoundPlayable) entity)::setConfigHolder);
		}
	}

	public static void applySoundConfigServer(Player player, int entityId, String configName) {
		Level world = player.level;
		Entity entity = world.getEntity(entityId);
		if (!(entity instanceof SoundPlayable)) {
			return;
		}
		if (entity instanceof TamableAnimal
				&& ((TamableAnimal) entity).getOwnerUUID()
				!= player.getUUID()) {
			return;
		}
		LMConfigManager.INSTANCE.getConfig(configName)
				.ifPresent(((SoundPlayable) entity)::setConfigHolder);
		sendS2CPacket(entity, configName);
	}

}