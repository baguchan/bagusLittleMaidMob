package net.baguchan.bagus_littlemaidmob.message;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.entity.LittleMaidBaseEntity;
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

public class SyncSetModePacket {
	private final int entityId;
	private final LittleMaidBaseEntity.MoveState mode;

	public SyncSetModePacket(FriendlyByteBuf buf) {
		entityId = buf.readVarInt();
		mode = LittleMaidBaseEntity.MoveState.get(buf.readUtf());
	}

	public SyncSetModePacket(Entity entity, LittleMaidBaseEntity.MoveState mode) {
		entityId = entity.getId();
		this.mode = mode;
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeVarInt(entityId);
		buf.writeUtf(mode.name());
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				applyMultiModelClient(entityId, mode);
			} else {
				Player player = ctx.get().getSender();
				if (player == null) {
					return;
				}
				applyMultiModelServer(entityId, mode, player);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendC2SPacket(Entity entity, LittleMaidBaseEntity.MoveState state) {
		LittleMaidMod.CHANNEL.sendToServer(new SyncSetModePacket(entity, state));
	}

	public static void sendS2CPacket(Entity entity, LittleMaidBaseEntity.MoveState state) {
		LittleMaidMod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
				new SyncSetModePacket(entity, state));
	}

	@OnlyIn(Dist.CLIENT)
	public static void applyMultiModelClient(int entityId, LittleMaidBaseEntity.MoveState mode) {
		Level level = Minecraft.getInstance().level;
		if (level == null) return;
		Entity entity = level.getEntity(entityId);
		if (!(entity instanceof LittleMaidBaseEntity)) return;

		((LittleMaidBaseEntity) entity).setMovingState(mode);
	}

	public static void applyMultiModelServer(int entityId, LittleMaidBaseEntity.MoveState mode, Player player) {
		Entity entity = player.level.getEntity(entityId);
		if (!(entity instanceof LittleMaidBaseEntity)) return;
		((LittleMaidBaseEntity) entity).setMovingState(mode);

		sendS2CPacket(entity, mode);
	}

}